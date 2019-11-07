package com.wongki.framework.mvvm.lifecycle.wrap.event

/**
 * @author  wangqi
 * date:    2019-11-07
 * email:   wangqi7676@163.com
 * desc:    .
 */
open class EventValueKeyBuilderWrapper<T : Any> {
    protected lateinit var type : EventValueType
    lateinit var keyBuilder: EventValueKeyBuilder<T>

    fun key(init: EventValueKeyBuilder<T>.() -> Unit) {
        val builder = EventValueKeyBuilder<T>()
        builder.type = type
        builder.init()
        this.keyBuilder = builder
    }

    fun getKey(): EventValueKey {
        keyBuilder.checkKey()
        return keyBuilder.buildKey()
    }


}