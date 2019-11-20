# android_mvvm_kotlin_dsl
淡化了LiveData、Lifecycle的存在，便于阅读

## 使用说明
两步完成订阅和更新数据
### View - 装载订阅
```kotlin
viewModel<XXViewModel> {
    attachObserve<String> {
        key {
            method = "setUserName"
        }
        observe {
            onChange {tv_user_name.text = "$this"} // 更新UI
        }
    }
    
    attachObserve<XX> {
        // ...
    }
}
```
### ViewModel - 设置、更新数据
未来版本使用kapt等技术可以自动生成更新数据的方法，可以减少开发代码
```kotlin
fun setUserName(name:String) {
    setValue<String> {
        key {
            method = "setUserName"
        }
        value { name }
    }

}
```

## API说明
### 装载订阅
 * [LiveDataViewModel.attachObserve]同步场景无状态，使用参考上面的例子
 * [LiveDataViewModel.attachEventObserve]异步场景有状态[EventValue.event]
 * [LiveDataViewModel.attachEventObserveForArrayList]异步场景有状态

### 设置值
 * [LiveDataViewModel.setValue]同步场景无状态，使用参考上面的例子
 * [LiveDataViewModel.setEventValue]异步场景有状态，具体使用可参考[LiveDataViewModel.observeAndTransformEventObserver]
 * [LiveDataViewModel.setEventValueForArrayList]异步场景ArrayList有状态，具体使用可参考[LiveDataViewModel.observeAndTransformEventObserverForArrayList]

### 获取值(参考设置值)
 * [LiveDataViewModel.getValue]同步场景无状态
 * [LiveDataViewModel.getEventValue]异步场景有状态
 * [LiveDataViewModel.getEventValueForArrayList]异步场景ArrayList有状态



## 例子
### 搜索音乐
![demo.png](./assets/demo.png)

#### 1.装载并订阅
 * attachObserve的目的是在对应的ViewModel生成对应的LiveData对象和订阅观察数据变动
 * LiveData会缓存在ViewModel中(有唯一的Key绑定。同步场景无状态LiveData的Key生成与method有关，异步场景有状态LiveData的key生成无需关注，其默认与type、kClass相关)
 * observe的目的是订阅，观察数据变动
```kotlin
viewModel<MusicViewModel> {

    // 结果总数量
    attachObserve<Int> {
    
        key{
            method = "setTotalCount"
        }

        // 订阅，观察数据变动
        observe {
            onChange {
                tv_total.text = "$this"
            }
        }
    }

    
    // 结果总数量
    attachObserve<String> {
    
        key{
            method = "setResultList"
        }

        // 订阅，观察数据变动
        observe {
            onChange {
                tv_result.text = "$this"
            }
        }
    }

    
    // 搜索音乐
    attachEventObserveForArrayList<SearchMusic.Response.Item> {
        
        // 订阅，观察状态和结果
        observe {
            onStart {}
            onCancel {}
            onSuccess {}
            onFailed {}
        }
        
    }

}
```

#### 2.点击按钮搜索音乐
```kotlin
// 搜索音乐
viewModel<MusicViewModel> {
    searchMusic(name)
}
```

### 需要实现的类
#### 1.Retrofit的Service接口
```kotlin
interface MusicApi {
    @GET("/searchMusic")
    fun searchMusic(@Query("name")name:String):Observable<CommonResponse<ArrayList<SearchMusic.Response.Item>>>
}
```

#### 2.音乐仓库
##### 音乐仓库接口
```kotlin
interface IMusicRepo {
    /**
     * 搜索音乐
     * @param name 要搜索的音乐名称
     * @param init 网络请求观察者构建器
     */
    fun searchMusic(name:String,init: EventObserverBuilder<MyResponse<ArrayList<SearchMusic.Response.Item>>>.()->Unit)
}
```
##### 实现音乐仓库接口（必须继承BaseRepository）
```kotlin
class MusicRepo : BaseRepository(), IMusicRepo {

    /**
     * 搜索音乐
     * @param name 要搜索的音乐名称
     * @param init 网络请求观察者构建器
     */
    override fun searchMusic(name: String, init: EventObserverBuilder<MyResponse<ArrayList<SearchMusic.Response.Item>>>.() -> Unit) {
        musicService {
            api { searchMusic(name) }.thenCall {
                lifecycleObserver = this@MusicRepo
                observer(init)
            }
        }
    }
    
}
```

#### 3.音乐ViewModel（必须继承LiveDataViewModel）
```kotlin
class MusicViewModel : LiveDataViewModel<MusicRepo>() {

    fun searchMusic(name: String) {
        // 打开仓库
        repository {
            
            // 在音乐仓库搜索音乐
            searchMusic(name) {
                /**
                 * 网络请求的观察器转换成EventValue通知[MusicActivity.attachEventObserveForArrayList<SearchMusic.Response.Item>]
                 * &&
                 * 在通知UI前观察数据（设置搜索结果总数和设置结果列表）
                 */
                observeAndTransformEventObserverForArrayList {
                    onSuccess {
                        // 设置搜索结果总数
                        this@MusicViewModel.setTotalCount(this)
                        // 设置结果列表
                        this@MusicViewModel.setResultList(this)
                    }
                }

            }
            

        }


    }

    
    // 设置搜索结果总数
    private fun setTotalCount(list: ArrayList<SearchMusic.Response.Item>?) {
        // 更新数据，通知订阅的位置
        setValue<Int> {
            key{
                method = "setTotalCount"
            }
            value { list?.size }
        }

    }
    
    // 设置结果列表
    private fun setResultList(list: ArrayList<SearchMusic.Response.Item>?) {
        var result = ""
        list?.apply {
            this.forEachIndexed { index, item ->
                result += "${index + 1}. ${item.title} - ${item.author}  播放地址:${item.url}\n"
            }
        }

        if (result.isEmpty()) result = "暂无结果"

        // 更新数据，通知订阅的位置
        setValue<String> {
            key{
                method = "setResultList"
            }
            value { result }
        }

    }
}
```
## 其他
### 注意
 * 有状态的不支持设置key（也就是说有状态的一个类型在同一个ViewModel只能存在一个LiveData实例）！
 * 装载订阅时LiveData的LifecycleOwner默认为创建ViewModel时的LifecycleOwner对象，详情请查看[FragmentActivity和Fragment的viewModel拓展函数](/lib_common/src/main/java/com/wongki/framework/mvvm/lifecycle/ViewModels.kt)中[LiveDataViewModelFactory工厂类](/lib_common/src/main/java/com/wongki/framework/mvvm/factory/LiveDataViewModelFactory.kt)生成ViewModel的过程，以及[ILiveDataViewModel.attachObserve](/lib_common/src/main/java/com/wongki/framework/mvvm/lifecycle/ILiveDataViewModel.kt)等装载订阅函数，如果你需要为LiveData提供其他的LifecycleOwner，那么需要在装载订阅时指定owner：
```
viewModel<XXViewModel> {
    attachObserve {
        key {...}
        observe {
            owner = LifecycleOwner
        }
    }
}
```
