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
            false  // 返回true代表上层处理，返回false代表框架处理，目前框架层会弹Toast
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
        /**
         * 无需请求成功后的操作
         */
        //1. 启动远端仓库
        launchRemoteResp(MusicServiceCore) {
            // 2. 远端仓库的api请求
            searchMusic(name)

            //3. 提交远端仓库的api请求
        }.commitForArrayList()
    }
}
```


## 其他例子：一次操作多次网络请求
### 1.fork订阅
```kotlin
        /**
         * 申请借款
         */
        mLoanViewModel.fork(LoanApplyStart.Result::class)
                .observe(
                        owner = this,
                        onStart = {
                            activity?.showLoadingDialog(seqNo = 10)
                        },
                        onCancel = {
                            activity?.dialogDismiss(seqNo = 10)
                        },
                        onFailed = { _, _ ->
                            activity?.dialogDismiss(seqNo = 10)
                            false
                        },
                        onSuccess = { result ->
                            activity?.dialogDismiss(seqNo = 10)
                            result?.userState?.let { userState ->
                                loanApplyByUserState(userState)
                            }

                        }

                )
```
### 2.viewModel中的嵌套请求
```kotlin
    /**
     * 申请借款
     */
    override fun loanApplyStart(days: Int) {
        val finalForkKClass = LoanApplyStart.Result::class
        val finalResult = LoanApplyStart.Result()

        // 1. 申请借款前置条件
        launchRemoteResp(AppServiceCore) { loanApplyBefore(days) }
                .commitMulti(setStartAction = true, finalForkKClass = finalForkKClass) { result ->

                    // 2. 查询用户状态
                    launchRemoteResp(AppServiceCore) { queryUserState() }
                            .commitMulti(setStartAction = false, finalForkKClass = finalForkKClass) { userState ->
                                finalResult.userState = userState
                                setValue(finalForkKClass, EventAction.SUCCESS) {
                                    this.data = finalResult
                                }
                            }


                }
    }

```
