# mvvm_kolin

![mvvm_kolin.png](./assets/mvvm_kotlin.png)

## 例子
### 搜索音乐

#### 1.生成LiveData并订阅
```kotlin
// 1.attach的目的是在对应的ViewModel生成对应的LiveData对象
// 2.LiveData会缓存在ViewModel中(有唯一的Key绑定，Key的生成与kClass、key相关)
// 3.observe的目的是订阅
viewModel<MusicViewModel> {

    // 结果总数量
    attach<Int> {
        kClass = Int::class
        key = "setTotalCount"

        // 订阅，观察数据变动
        observe {
            owner = this@MusicActivity
            onChange {
                tv_total.text = "$this"
            }
        }
    }

    
    // 结果总数量
    attach<String> {
        kClass = String::class
        key = "setResultList"

        // 订阅，观察数据变动
        observe {
            owner = this@MusicActivity
            onChange {
                tv_result.text = "$this"
            }
        }
    }

    
    // 搜索音乐
    attachWrapperForArrayList<SearchMusic.Item> {
    
        kClass = SearchMusic.Item::class
        // 订阅，观察网络请求状态和结果
        observe {
            owner = this@MainActivity
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
#### 1.编写Retrofit的Service接口
```kotlin
interface MusicApi {
    @GET("/searchMusic")
    fun searchMusic(@Query("name")name:String):Observable<CommonResponse<ArrayList<SearchMusic.Item>>>
}
```
#### 2.编写ViewModel
```kotlin
class MusicViewModel : LiveDataViewModel() {

    fun searchMusic(name: String) {
        // 请求服务器获取搜索结果
        musicService {

            callArrayList<SearchMusic.Item> {
                api { searchMusic(name = name) }
                // 真正发起网络请求&&通知UI前做一些事情（ex：设置搜索结果总数和设置结果列表）
                observeWithBeforeNotifyUIForArrayList {
                    onSuccess{
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
    private fun setTotalCount(list: ArrayList<SearchMusic.Item>?) {
        // 通知订阅的地方
        setValue<Int> {
            kClass = Int::class
            key = "setTotalCount"
            value { list?.size }
        }

    }
    
    // 设置结果列表
    private fun setResultList(list: ArrayList<SearchMusic.Item>?) {
        var result = ""
        list?.apply {
            this.forEachIndexed { index, item ->
                result += "${index + 1}. ${item.title} - ${item.author}  播放地址:${item.url}\n"
            }
        }

        if (result.isEmpty()) result = "暂无结果"

        // 通知订阅的地方
        setValue<String> {
            kClass = String::class
            key = "setResultList"
            value { result }
        }

    }
}
```