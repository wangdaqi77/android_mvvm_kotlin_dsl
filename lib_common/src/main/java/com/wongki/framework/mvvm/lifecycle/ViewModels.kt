package com.wongki.framework.mvvm.lifecycle

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProviders
import com.wongki.framework.mvvm.factory.LiveDataViewModelFactory

/**
 * @author  wangqi
 * date:    2019/7/3
 * email:   wangqi7676@163.com
 * desc:    .
 */

@LiveDataViewModelDslMarker
inline fun <reified T : LiveDataViewModel<*>> FragmentActivity.viewModel(init: T.() -> Unit): T {
    val viewModelJavaClazz = T::class.java
    val viewModel = ViewModelProviders.of(this, LiveDataViewModelFactory(this)).get(viewModelJavaClazz)
    viewModel.init()
    return viewModel
}

@LiveDataViewModelDslMarker
inline fun <reified T : LiveDataViewModel<*>> Fragment.viewModel(init: T.() -> Unit): T {
    val viewModelJavaClazz = T::class.java
    val viewModel = ViewModelProviders.of(this, LiveDataViewModelFactory(this)).get(viewModelJavaClazz)
    viewModel.init()
    return viewModel
}

@LiveDataViewModelDslMarker
class ViewModelBuilder {

}



