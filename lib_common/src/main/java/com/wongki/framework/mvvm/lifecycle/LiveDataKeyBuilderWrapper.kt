package com.wongki.framework.mvvm.lifecycle

/**
 * @author  wangqi
 * date:    2019-11-07
 * email:   wangqi7676@163.com
 * desc:    .
 */
open class LiveDataKeyBuilderWrapper<T : Any> {
    lateinit var keyBuilder: LiveDataKeyBuilder<T>

    fun key(init: LiveDataKeyBuilder<T>.() -> Unit) {
        val builder = LiveDataKeyBuilder<T>()
        builder.init()
        this.keyBuilder = builder
    }

    fun getKey(): LiveDataKey {
//        keyBuilder.checkKey()
        return keyBuilder.buildKey()
    }


}