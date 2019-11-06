package com.wongki.framework.mvvm

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import com.wongki.framework.mvvm.factory.LiveDataViewModelFactory
import com.wongki.framework.mvvm.lifecycle.LiveDataViewModel
import java.lang.IllegalArgumentException

/**
 * @author  wangqi
 * date:    2019/7/3
 * email:   wangqi7676@163.com
 * desc:    .
 */

@DslMarker
annotation class ViewModelFactoryDslMarker

@ViewModelFactoryDslMarker
inline fun <reified T : ViewModel> FragmentActivity.viewModel(init: T.() -> Unit): T {
    val viewModelJavaClazz = T::class.java
    val viewModel = ViewModelProviders.of(this, LiveDataViewModelFactory).get(viewModelJavaClazz)
    viewModel.init()
    return viewModel
}

@ViewModelFactoryDslMarker
inline fun <reified T : ViewModel> Fragment.viewModel(init: T.() -> Unit): T {
    val viewModelJavaClazz = T::class.java
    val viewModel = ViewModelProviders.of(this, LiveDataViewModelFactory).get(viewModelJavaClazz)
    viewModel.init()
    return viewModel
}

@ViewModelFactoryDslMarker
class ViewModelBuilder {

}



