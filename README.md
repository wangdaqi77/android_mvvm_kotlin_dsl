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


        /**
         * 如果需要保存数据则，需要请求成功后的操作
         */
        // 1. 启动远端仓库
        launchRemoteResp(MusicServiceCore) {
            // 2. 远端仓库的api请求
            searchMusic(name = name)

            //3. 提交远端仓库的api请求
        }.commitForArrayList()

        // 4. 异步请求远端仓库的成功的回调
        { result ->

            if (!result.isNullOrEmpty()) {
                // 5. 启动本地SP仓库
                launchLocalSpResp {
                    // 6. 保存第一条数据到SP
                    val firstMusic = result.first()
                    this.firstMusicAuthor = firstMusic.author
                    this.firstMusicTitle = firstMusic.title
                }
            }

        }
    }
}
```

## 一次操作多次请求
### 1.订阅
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
        // 保存当前选择产品的天数
        launchLocalSpResp {
            this.currentProductDays = "$days"
        }

        val finalForkKClass = LoanApplyStart.Result::class

        // 1. 申请借款前置
        launchRemoteRespForMultiAndCommit(setStartAction = true, finalForkKClass = finalForkKClass, service = AppServiceCore, preRequest = { loanApplyStart(days) }) {result->
            val finalResult = result ?: LoanApplyStart.Result()
            // 2. 查询用户状态
            launchRemoteRespForMultiAndCommit(setStartAction = false, finalForkKClass = finalForkKClass, service = AppServiceCore, preRequest = { queryUserState() }) { userState ->
                finalResult.userState = userState
                setValue(finalForkKClass, EventAction.SUCCESS) { 
                    this.data = finalResult
                }

            }
        }
    }
```
