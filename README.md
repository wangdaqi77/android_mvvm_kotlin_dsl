# mvvm_kolin

![mvvm_kolin.png](./assets/mvvm_kotlin.png)

## 例子
### 1.声明
```kotlin
val musicViewModel by lazy { getRetrofitLiveDataViewModel(MusicViewModel::class.java) }
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
### 1.声明
```kotlin
class MusicViewModel : ViewModel(), RetrofitLiveDataViewModel {

    override val mSystemLiveData: HashMap<String, MutableLiveData<DataWrapper<*>>?> = HashMap()

    fun searchMusic(name: String) {
        newMusicRequester(this) { api -> api.searchMusic(name) }
            .commitForArrayList(SearchMusic.Item::class.java)

    }

    override fun onCleared() {
        super<RetrofitLiveDataViewModel>.onCleared()
    }
}
```
