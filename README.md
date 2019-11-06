# mvvm_kolin

![mvvm_kolin.png](./assets/mvvm_kotlin.png)

## 例子
### 搜索音乐
#### 1.创建ViewModel
```kotlin
val musicViewModel by lazy { getLiveDataViewModel<MusicViewModel>() }
```
    

#### 2.订阅
```kotlin
// attach的目的是在musicViewModel生成对应的LiveData对象，observe的目的是订阅
        musicViewModel {
        
            // 结果总数量
            attach<Int> {
            
                kClass = Int::class
                key = "total"
                // 订阅
                observe {
                    owner = this@MainActivity
                    onChange {
                        tv_total.text = "total：$this"
                    }
                }
                
            }
            
            // 搜索音乐结果
            attachWrapperForArrayList<SearchMusic.Item> {
            
                kClass = SearchMusic.Item::class
                // 订阅
                observe {
                    owner = this@MainActivity
                    onSuccess {
                        // show...
                    }
                }
                
            }

        }
```

#### 3.view点击时获取数据
```kotlin
// 搜索音乐
musicViewModel.searchMusic(name)
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
class MusicViewModel : AbsLiveDataViewModel() {

    fun searchMusic(name: String) {

        musicService {

            call<ArrayList<SearchMusic.Item>> {

                api { searchMusic(name = name) }

                observeLiveDataWrapperForArrayList{
                    // 成功时,如果你需要修改数据...
                }
            }

        }
        
    }
}
```