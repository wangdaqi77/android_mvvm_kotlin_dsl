# mvvm_kolin

![mvvm_kolin.png](./assets/mvvm_kotlin.png)

## 例子
### 搜索音乐

#### 1.生成LiveData并订阅
```kotlin
// attach的目的是在MusicViewModel生成对应的LiveData对象，observe的目的是订阅
viewModel<MusicViewModel> {

    // 结果总数量
    attach<Int> {
        kClass = Int::class
        key = "setTotalCount"

        // 订阅
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

        // 订阅
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
        // 订阅网络请求结果
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

        musicService {

            call<ArrayList<SearchMusic.Item>> {

                api { searchMusic(name = name) }

                observeLiveDataWrapperForArrayList{
                    // 设置结果总数
                    setTotalCount(this)
                    // 设置结果
                    setResultList(this)
                }
            }

        }
        
    }
    
    
    private fun setTotalCount(list: ArrayList<SearchMusic.Item>?) {

        setValue<Int> {
            kClass = Int::class
            key = "setTotalCount"
            value { list?.size }
        }

    }

    private fun setResultList(list: ArrayList<SearchMusic.Item>?) {
        var result = ""
        list?.apply {
            this.forEachIndexed { index, item ->
                result += "${index + 1}. ${item.title} - ${item.author}  播放地址:${item.url}\n"
            }
        }

        if (result.isEmpty()) result = "暂无结果"

        setValue<String> {
            kClass = String::class
            key = "setResultList"
            value { result }
        }

    }
}
```