package com.wongki.framework.mvvm.lifecycle.wrap

import com.wongki.framework.mvvm.lifecycle.LiveDataViewModelDslMarker
import com.wongki.framework.mvvm.lifecycle.exception.NoSetValueException

/**
 * @author  wangqi
 * date:    2019-11-05
 * email:   wangqi7676@163.com
 * desc:    .
 */
@LiveDataViewModelDslMarker
class LiveDataWrapperSetterValueBuilder<T : Any> : WrapperKeyBuilder<T>() {

    private var setValueCount = 0
    internal var value:ValueWrapper<T>? = null
        set(value) {
            setValueCount++
            type = ValueWrapperType.Normal
            field = value
        }


    fun value(init: ValueWrapperBuilder<T>.()->Unit) {
        val dataWrapperBuilder = ValueWrapperBuilder<T>()
        dataWrapperBuilder.init()
        this.value = dataWrapperBuilder.build()
    }

    internal fun check() {
        if (setValueCount == 0) throw NoSetValueException(buildKey())
    }
}
