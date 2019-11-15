package com.wongki.framework.mvvm.lifecycle.wrap.event

import com.wongki.framework.EventObserverBuilder
import com.wongki.framework.http.retrofit.observer.HttpCommonObserver
import com.wongki.framework.mvvm.lifecycle.LiveDataViewModelDslMarker

/**
 * @author  wangqi
 * date:    2019-11-06
 * email:   wangqi7676@163.com
 * desc:    .
 */

@LiveDataViewModelDslMarker
open class EventValueObserverBuilder<T> : EventObserverBuilder<T>(){

    @LiveDataViewModelDslMarker
    override fun onStart(onStart: () -> Unit) {
        this.onStart = onStart
    }

    @LiveDataViewModelDslMarker
    override fun onCancel(onCancel: () -> Unit) {
        this.onCancel = onCancel
    }

    /**
     *  返回true代表上层处理，返回false代表框架处理
     *  目前框架层会弹Toast[HttpCommonObserver.onError]
     */
    @LiveDataViewModelDslMarker
    override fun onFailed(onFailed: ((Int, String) -> Boolean)) {
        this.onFailed = onFailed
    }

    @LiveDataViewModelDslMarker
    override fun onSuccess(onSuccess: T?.() -> Unit) {
        this.onSuccess = onSuccess
    }

}