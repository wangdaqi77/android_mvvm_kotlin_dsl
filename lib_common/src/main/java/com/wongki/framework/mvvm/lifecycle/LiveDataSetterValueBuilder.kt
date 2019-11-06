package com.wongki.framework.mvvm.lifecycle

import com.wongki.framework.mvvm.lifecycle.exception.NoSetValueException

/**
 * @author  wangqi
 * date:    2019-11-05
 * email:   wangqi7676@163.com
 * desc:    .
 */
@LiveDataViewModelDslMarker
open class LiveDataSetterValueBuilder<T : Any> : LiveDataKeyBuilder<T>() {
    private var setValueCount = 0
    internal var value: T? = null
        set(value) {
            setValueCount++
            field = value
        }


    fun value(init: () -> T?) {
        this.value = init()
    }


    internal fun check() {
        if (setValueCount == 0) throw NoSetValueException(buildKey())
    }
}
