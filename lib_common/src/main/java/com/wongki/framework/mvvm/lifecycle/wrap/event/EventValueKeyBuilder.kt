package com.wongki.framework.mvvm.lifecycle.wrap.event

import com.wongki.framework.mvvm.lifecycle.ILiveDataKeyBuilder
import com.wongki.framework.mvvm.lifecycle.LiveDataViewModelDslMarker
import com.wongki.framework.mvvm.lifecycle.exception.DslRejectedException
import kotlin.reflect.KClass

/**
 * @author  wangqi
 * date:    2019-11-05
 * email:   wangqi7676@163.com
 * desc:    .
 */
@LiveDataViewModelDslMarker
open class EventValueKeyBuilder : ILiveDataKeyBuilder<EventValueKey> {
    companion object {
        internal val DEFAULT = EventValueKeyBuilder::class
    }

    internal lateinit var type: EventValueType
    var kClass: KClass<*> = DEFAULT

    override fun check(): Boolean = kClass != DEFAULT

    override fun buildKey() = EventValueKey().apply {
        key =
            "${this@EventValueKeyBuilder.type.name}-${this@EventValueKeyBuilder.kClass.qualifiedName}"
    }

}