package com.wongki.framework.mvvm.lifecycle.wrap.event

import androidx.lifecycle.LifecycleOwner
import com.wongki.framework.mvvm.lifecycle.LiveDataViewModelDslMarker

/**
 * @author  wangqi
 * date:    2019-11-06
 * email:   wangqi7676@163.com
 * desc:    .
 */

@LiveDataViewModelDslMarker
class EventLiveDataObserveBuilder<T> : EventValueObserverBuilder<T>() {
    @LiveDataViewModelDslMarker
    var owner: LifecycleOwner? = null
}