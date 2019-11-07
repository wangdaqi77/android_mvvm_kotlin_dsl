package com.wongki.framework.mvvm.lifecycle

import androidx.lifecycle.LifecycleOwner

/**
 * @author  wangqi
 * date:    2019-11-07
 * email:   wangqi7676@163.com
 * desc:    .
 */
interface ILifecycleOwnerWrapper {
    fun getLifecycleOwner():LifecycleOwner?
}