package com.wongki.framework.mvvm.lifecycle.wrap

import com.wongki.framework.mvvm.lifecycle.LiveDataKeyBuilder
import com.wongki.framework.mvvm.lifecycle.LiveDataViewModelDslMarker

/**
 * @author  wangqi
 * date:    2019-11-05
 * email:   wangqi7676@163.com
 * desc:    .
 */
@LiveDataViewModelDslMarker
open class WrapperKeyBuilder<T : Any> : LiveDataKeyBuilder<T>() {
    internal lateinit var type: ValueWrapperType

    /**
     * TODO 可以优化，key存在过就用之前的
     */
    override fun buildKey() = WrapperKey().apply {
        key =
            super.buildKey().key +
                    "-${this@WrapperKeyBuilder.type.name}"
    }

}