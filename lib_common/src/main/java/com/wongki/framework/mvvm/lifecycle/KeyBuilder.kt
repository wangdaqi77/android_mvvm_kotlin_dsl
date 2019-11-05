package com.wongki.framework.mvvm.lifecycle

/**
 * @author  wangqi
 * date:    2019-11-05
 * email:   wangqi7676@163.com
 * desc:    .
 */
@LiveDataViewModelDslMarker
open class KeyBuilder {
    lateinit var key: String
    fun buildKey() = Key().apply { key }
}