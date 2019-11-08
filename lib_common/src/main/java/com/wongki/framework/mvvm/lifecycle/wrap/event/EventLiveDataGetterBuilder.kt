package com.wongki.framework.mvvm.lifecycle.wrap.event

import com.wongki.framework.mvvm.lifecycle.LiveDataViewModelDslMarker

/**
 * @author  wangqi
 * date:    2019-11-05
 * email:   wangqi7676@163.com
 * desc:    .
 */


@LiveDataViewModelDslMarker
class EventLiveDataGetterBuilder<T : Any> : DslEventValueKeyBuilder() {
    init {
        type = EventValueType.Normal
    }
}