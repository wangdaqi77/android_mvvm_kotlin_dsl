package com.wongki.framework.mvvm.lifecycle.wrap.event

import com.wongki.framework.mvvm.lifecycle.LiveDataViewModelDslMarker
import com.wongki.framework.mvvm.lifecycle.exception.NoSetValueException

/**
 * @author  wangqi
 * date:    2019-11-05
 * email:   wangqi7676@163.com
 * desc:    .
 */
@LiveDataViewModelDslMarker
class EventLiveDataArrayListSetterBuilder<T : Any> : EventValueKeyBuilder<T>() {
    private var setValueCount = 0
    internal var value: EventValue<ArrayList<T>>? = null
        set(value) {
            setValueCount++
            type = EventValueType.ArrayList
            field = value
        }


    fun value(init: EventValueBuilder<ArrayList<T>>.()->Unit) {
        val dataWrapperBuilder =
            EventValueBuilder<ArrayList<T>>()
        dataWrapperBuilder.init()
        this.value = dataWrapperBuilder.build()
    }

    internal fun check() {
        if (setValueCount == 0) throw NoSetValueException(buildKey())
    }
}
