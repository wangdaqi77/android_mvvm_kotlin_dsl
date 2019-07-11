package com.wongki.demo.http

import com.wongki.demo.model.remote.api.MusicApi
import com.wongki.framework.http.retrofit.lifecycle.IHttpRetrofitLifecycleObserver
import com.wongki.framework.model.domain.CommonResponse
import io.reactivex.Observable

/**
 * @author  wangqi
 * date:    2019/6/26
 * email:   wangqi7676@163.com
 * desc:    .
 */

fun <R> Any.newMusicRequester(
    lifecycleObserver: IHttpRetrofitLifecycleObserver? = null,
    preRequest: (MusicApi) -> Observable<CommonResponse<R>>
) = MusicServiceCore.newRequester(lifecycleObserver, preRequest)
