package com.wongki.framework.mvvm.lifecycle


/**
 * @author  wangqi
 * date:    2019-11-05
 * email:   wangqi7676@163.com
 * desc:    .
 */
@LiveDataViewModelDslMarker
open class LiveDataKeyBuilder : ILiveDataKeyBuilder<LiveDataKey> {
    companion object {
        internal const val DEFAULT = ""
    }

    @LiveDataViewModelDslMarker
    var method: String = DEFAULT

    fun check():Boolean = method != DEFAULT
    /**
     * TODO 可以优化，key存在过就用之前的
     */
    override fun buildKey(keyPrefix:String):LiveDataKey {
        return LiveDataKey().apply {
            key = "$keyPrefix:${this@LiveDataKeyBuilder.method}"
        }
    }

}