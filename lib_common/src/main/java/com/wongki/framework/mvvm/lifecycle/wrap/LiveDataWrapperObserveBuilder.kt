package com.wongki.framework.mvvm.lifecycle.wrap

import androidx.lifecycle.LifecycleOwner
import com.wongki.framework.mvvm.lifecycle.LiveDataViewModelDslMarker
import com.wongki.framework.mvvm.lifecycle.ObserveBuilder

/**
 * @author  wangqi
 * date:    2019-11-06
 * email:   wangqi7676@163.com
 * desc:    .
 */

@LiveDataViewModelDslMarker
class LiveDataWrapperObserveBuilder<T>: ObserveBuilder<T>() {
    lateinit var owner: LifecycleOwner
}