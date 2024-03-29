package com.wongki.framework.mvvm.lifecycle

/**
 * @author  wangqi
 * date:    2019-11-05
 * email:   wangqi7676@163.com
 * desc:    .
 */
@LiveDataViewModelDslMarker
interface ILiveDataKeyBuilder<KEY : LiveDataKey> {
    fun buildKey(keyPrefix:String):KEY
}