package com.wongki.framework.http.retrofit.lifecycle

import com.wongki.framework.http.lifecycle.IHttpLifecycleObserver

/**
 * @author  wangqi
 * date:    2019/6/17
 * email:   wangqi7676@163.com
 * desc:
 */
interface IHttpDestroyedObserver : IHttpLifecycleObserver {
    override fun clearRequest() {
        HttpRequesterManagerHelper.forEachLifecycle { lifecycle ->
            lifecycle.cancelRequest(this@IHttpDestroyedObserver)
        }
    }
}