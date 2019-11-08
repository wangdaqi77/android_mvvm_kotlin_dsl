package com.wongki.framework.mvvm.lifecycle.wrap.event

/**
 * @author  wangqi
 * date:    2019-11-07
 * email:   wangqi7676@163.com
 * desc:    .
 */
open class DslEventValueKeyBuilder {
    protected lateinit var type : EventValueType
    var keyBuilder: EventValueKeyBuilder = EventValueKeyBuilder()

    fun key(init: EventValueKeyBuilder.() -> Unit) {
        keyBuilder.init()
    }

    fun getKey(): EventValueKey {
        keyBuilder.type = type
        return keyBuilder.buildKey()
    }


}