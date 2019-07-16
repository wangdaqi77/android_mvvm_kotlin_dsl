package com.wongki.framework.mvvm.remote

import com.wongki.framework.http.retrofit.core.RetrofitServiceCore
import com.wongki.framework.model.domain.CommonResponse
import com.wongki.framework.mvvm.AbsLiveDataViewModel
import com.wongki.framework.mvvm.action.EventAction
import com.wongki.framework.mvvm.lifecycle.ILiveDataViewModel
import io.reactivex.Observable
import kotlin.reflect.KClass

/**
 * @author  wangqi
 * date:    2019/7/11
 * email:   wangqi7676@163.com
 * desc:    .
 */


/**
 * @param service 服务内核
 * @return 请求器[RetrofitServiceCore.RetrofitRequester]
 */
inline fun <reified RESPONSE_DATA : Any, API, SERVICE : RetrofitServiceCore<API>> AbsLiveDataViewModel.launchRemoteResp(service: SERVICE, crossinline preRequest: API.() -> Observable<CommonResponse<RESPONSE_DATA>>): RetrofitServiceCore.RetrofitRequester<API, RESPONSE_DATA> {
    return service.newRequester(this) { api ->
        api.preRequest()
    }
}

/**
 * 用于一次点击，多次网络请求
 * @param setStartAction 是否发送Start事件  只有多次网络请求的第一次是需要发送Start事件
 * @param finalForkKClass 最终订阅的kClass，与fork一一对应 [ILiveDataViewModel.fork]
 * @param service 服务内核
 * @param onSuccess 成功的回调
 * @return 请求器[RetrofitServiceCore.RetrofitRequester]
 */
inline fun <reified T : Any, reified RESPONSE_DATA : Any, API, SERVICE : RetrofitServiceCore<API>> AbsLiveDataViewModel.launchRemoteRespForMultiAndCommit(setStartAction: Boolean, finalForkKClass: KClass<T>, service: SERVICE, crossinline preRequest: API.() -> Observable<CommonResponse<RESPONSE_DATA>>, crossinline onSuccess: (RESPONSE_DATA?) -> Unit): RetrofitServiceCore.RetrofitRequester<API, RESPONSE_DATA> {
    return service.newRequester(this) { api ->
        api.preRequest()
    }
        .onStart { if (setStartAction) finalForkKClass.setValueForAction(EventAction.START) }
        .onCancel { finalForkKClass.setValueForAction(EventAction.CANCEL) }
        .onSuccess { result ->
            onSuccess(result)
        }
        .onFailed { code, message ->
            val dataWrapper = setValue(finalForkKClass, EventAction.FAILED) {
                this.code = code
                this.message = message
            }
            return@onFailed dataWrapper.errorProcessed
        }.request()
}