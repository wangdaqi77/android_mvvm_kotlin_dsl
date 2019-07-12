package com.wongki.framework.mvvm.remote

import com.wongki.framework.http.retrofit.core.RetrofitServiceCore
import com.wongki.framework.model.domain.CommonResponse
import com.wongki.framework.mvvm.AbsLiveDataViewModel
import io.reactivex.Observable

/**
 * @author  wangqi
 * date:    2019/7/11
 * email:   wangqi7676@163.com
 * desc:    .
 */

inline fun <reified RESPONSE_DATA : Any, API, SERVICE : RetrofitServiceCore<API>> AbsLiveDataViewModel.launchRemoteResp(service: SERVICE, crossinline preRequest: API.() -> Observable<CommonResponse<RESPONSE_DATA>>): RetrofitServiceCore.RetrofitRequester<API, RESPONSE_DATA> {
    return service.newRequester(this) { api ->
        api.preRequest()
    }
}