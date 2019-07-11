# mvvm_kolin

![mvvm_kolin.png](./assets/mvvm_kotlin.png)

## 例子
### 1.创建ViewModel
```kotlin
val musicViewModel by lazy { getLiveDataViewModel(MusicViewModel::class.java) }
```
    

### 2.订阅
```kotlin
// fork的目的就是生成对应的MutableLiveData对象
musicViewModel.forkForArrayList(SearchMusic.Item::class.java)
    .observe(
        owner = this,
        onStart = {/*开始*/},
        onCancel = {/*取消，当activity销毁时，准确的说是musicViewModel的onCleared()触发时*/},
        onFailed = { _, message ->
            // 失败
            false
        }
        ,
        onSuccess = { result ->
            //成功
        }
     )
```

### 3.view点击时获取数据
```kotlin
// 搜索音乐
musicViewModel.searchMusic(name)
```

## 需要实现的类
### 1.编写Retrofit的Service接口
```kotlin
interface MusicApi {
    @GET("/searchMusic")
    fun searchMusic(@Query("name")name:String):Observable<CommonResponse<ArrayList<SearchMusic.Item>>>
}
```
### 2.编写ViewModel
```kotlin
class MusicViewModel : AbsLiveDataViewModel() {

    fun searchMusic(name: String) {
        launchRemoteResp(MusicServiceCore) {
            searchMusic(name)
        }.commitForArrayList()

    }
}
```
