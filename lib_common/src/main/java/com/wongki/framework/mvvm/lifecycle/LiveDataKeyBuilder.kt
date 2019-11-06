package com.wongki.framework.mvvm.lifecycle

import com.wongki.framework.mvvm.lifecycle.exception.RejectSetException
import kotlin.reflect.KClass

/**
 * @author  wangqi
 * date:    2019-11-05
 * email:   wangqi7676@163.com
 * desc:    .
 */
@LiveDataViewModelDslMarker
open class LiveDataKeyBuilder<T : Any> {
    companion object {
        internal const val EMPTY_KEY = ""
    }

    var key: String = EMPTY_KEY
    lateinit var kClass: KClass<T>

    internal fun checkKey(){
        if (key != EMPTY_KEY){
            throw RejectSetException("LiveDataKeyBuilder\$key")
        }
    }

    /**
     * TODO 可以优化，key存在过就用之前的
     */
    open fun buildKey() = LiveDataKey().apply {
        key =
            "${this@LiveDataKeyBuilder.kClass.qualifiedName}-${this@LiveDataKeyBuilder.key}"
    }

}