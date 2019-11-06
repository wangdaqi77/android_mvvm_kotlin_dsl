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
class LiveDataWrapperArrayListSetterValueBuilder<T : Any> : WrapperKeyBuilder<T>() {
    private var setValueCount = 0
    internal var value:ValueWrapper<ArrayList<T>>? = null
        set(value) {
            setValueCount++
            type = ValueWrapperType.ArrayList
            field = value
        }


    fun value(init: ValueWrapperBuilder<ArrayList<T>>.()->Unit) {
        val dataWrapperBuilder = ValueWrapperBuilder<ArrayList<T>>()
        dataWrapperBuilder.init()
        this.value = dataWrapperBuilder.build()
    }

    internal fun check() {
        if (setValueCount == 0) throw NoSetValueException(buildKey())
    }
}
