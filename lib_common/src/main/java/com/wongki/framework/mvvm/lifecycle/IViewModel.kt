package com.wongki.framework.mvvm.lifecycle

/**
 * @author  wangqi
 * date:    2019-11-15
 * email:   wangqi7676@163.com
 * desc:    .
 */
interface IViewModel {
    fun getKeyPrefix(): String = this.javaClass.kotlin.qualifiedName?:"IViewModel"
}