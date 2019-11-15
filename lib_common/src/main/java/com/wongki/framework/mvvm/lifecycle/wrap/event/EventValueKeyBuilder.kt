package com.wongki.framework.mvvm.lifecycle.wrap.event

import com.wongki.framework.mvvm.lifecycle.ILiveDataKeyBuilder
import com.wongki.framework.mvvm.lifecycle.LiveDataViewModelDslMarker
import kotlin.reflect.KClass

/**
 * @author  wangqi
 * date:    2019-11-05
 * email:   wangqi7676@163.com
 * desc:    .
 */
@LiveDataViewModelDslMarker
open class EventValueKeyBuilder :
    ILiveDataKeyBuilder<EventValueKey> {
    companion object {
        internal val DEFAULT = EventValueKeyBuilder::class
    }

    internal lateinit var type: EventValueType
    var kClass: KClass<*> =
        DEFAULT

    fun check(): Boolean = kClass != DEFAULT

    override fun buildKey(keyPrefix: String) = EventValueKey().apply {
        key =
            "$keyPrefix:${this@EventValueKeyBuilder.type.name}<${this@EventValueKeyBuilder.kClass.qualifiedName}>"
    }

}