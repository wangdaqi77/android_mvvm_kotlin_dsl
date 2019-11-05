package com.wongki.framework.mvvm.lifecycle


/**
 * @author  wangqi
 * date:    2019-11-05
 * email:   wangqi7676@163.com
 * desc:    .
 */
@LiveDataViewModelDslMarker
open class Key {
    lateinit var key: String
    override fun toString() = key

    override fun hashCode(): Int = key.hashCode()

    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (other !is Key) return false
        return key == other.key
    }
}