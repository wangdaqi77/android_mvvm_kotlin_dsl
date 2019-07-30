package com.wongki.framework.mvvm

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import com.wongki.framework.mvvm.factory.ViewModelFactory
import java.lang.IllegalArgumentException

/**
 * @author  wangqi
 * date:    2019/7/3
 * email:   wangqi7676@163.com
 * desc:    .
 */

inline fun <reified T: ViewModel> FragmentActivity.getLiveDataViewModel(): T {
    val viewModelJavaClazz = T::class.java
    viewModelJavaClazz.checkViewModelType(AbsLiveDataViewModel::class.java)

    return ViewModelProviders.of(this, ViewModelFactory()).get(viewModelJavaClazz)
}

inline fun <reified T: ViewModel> Fragment.getLiveDataViewModel(): T {
    val viewModelJavaClazz = T::class.java
    viewModelJavaClazz.checkViewModelType(AbsLiveDataViewModel::class.java)

    return ViewModelProviders.of(this, ViewModelFactory()).get(viewModelJavaClazz)
}

/**
 * 检查ViewModel的数据类型是否匹配
 */
@Throws(IllegalArgumentException::class)
fun <T : ViewModel> Class<T>.checkViewModelType(clazz: Class<*>) {
    if (!clazz.isAssignableFrom(this)) {
        throw IllegalArgumentException("${this.name}必须继承${clazz.name}")
    }
//
//    if (!ViewModel::class.java.isAssignableFrom(this)) {
//        throw IllegalArgumentException("${this.name}必须继承${ViewModel::class.java.name}")
//    }
}

