package com.wongki.framework.mvvm.lifecycle


/**
 * @author  wangqi
 * date:    2019-11-07
 * email:   wangqi7676@163.com
 * desc:    .
 */
open class DslLiveDataKeyBuilder:ILiveDataKeyBuilder<LiveDataKey> {
    var keyBuilder: LiveDataKeyBuilder = LiveDataKeyBuilder()

    @LiveDataViewModelDslMarker
    fun key(init: LiveDataKeyBuilder.() -> Unit) {
        keyBuilder.init()
    }

    override fun buildKey(keyPrefix:String): LiveDataKey {
        return keyBuilder.buildKey(keyPrefix)
    }


}