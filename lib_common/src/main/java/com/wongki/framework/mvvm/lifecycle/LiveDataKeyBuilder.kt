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

    var method: String = DEFAULT

    override fun check():Boolean = method != DEFAULT
    /**
     * TODO 可以优化，key存在过就用之前的
     */
    override fun buildKey():LiveDataKey {
        return LiveDataKey().apply {
            key = this@LiveDataKeyBuilder.method
        }
    }

}