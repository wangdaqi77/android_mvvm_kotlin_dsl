package com.wongki.framework.mvvm.lifecycle


/**
 * @author  wangqi
 * date:    2019-11-07
 * email:   wangqi7676@163.com
 * desc:    .
 */
open class DslLiveDataKeyBuilder {
    var keyBuilder: LiveDataKeyBuilder = LiveDataKeyBuilder()

    fun key(init: LiveDataKeyBuilder.() -> Unit) {
        keyBuilder.init()
    }

    fun getKey(): LiveDataKey {
        return keyBuilder.buildKey()
    }


}