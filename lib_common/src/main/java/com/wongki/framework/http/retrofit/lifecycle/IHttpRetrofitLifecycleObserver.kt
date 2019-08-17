package com.wongki.framework.http.retrofit.lifecycle

import com.wongki.framework.http.lifecycle.IHttpLifecycleObserver
import com.wongki.framework.http.retrofit.core.RetrofitServiceCore

/**
 * @author  wangqi
 * date:    2019/6/17
 * email:   wangqi7676@163.com
 * desc:    网络请求时的tag [RetrofitServiceCore.newRequester]
 *          用于感知tag的生命周期
 */
interface IHttpRetrofitLifecycleObserver : IHttpLifecycleObserver {
    override fun onDestroy() {
        HttpRetrofitLifecycleHelper.forEachLifecycle { lifecycle ->
            lifecycle.cancelRequest(this@IHttpRetrofitLifecycleObserver)
        }
    }
}