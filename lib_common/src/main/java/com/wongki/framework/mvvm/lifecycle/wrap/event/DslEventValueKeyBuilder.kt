package com.wongki.framework.mvvm.lifecycle.wrap.event

import com.wongki.framework.mvvm.lifecycle.ILiveDataKeyBuilder
import com.wongki.framework.mvvm.lifecycle.LiveDataViewModelDslMarker

/**
 * @author  wangqi
 * date:    2019-11-07
 * email:   wangqi7676@163.com
 * desc:    .
 */
@LiveDataViewModelDslMarker
open class DslEventValueKeyBuilder:ILiveDataKeyBuilder<EventValueKey> {
    protected lateinit var type : EventValueType
    var keyBuilder: EventValueKeyBuilder = EventValueKeyBuilder()

    @LiveDataViewModelDslMarker
    fun key(init: EventValueKeyBuilder.() -> Unit) {
        keyBuilder.init()
    }

    override fun buildKey(keyPrefix:String): EventValueKey {
        keyBuilder.type = type
        return keyBuilder.buildKey(keyPrefix)
    }


}