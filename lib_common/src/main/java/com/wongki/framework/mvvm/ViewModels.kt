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

fun <T : ViewModel> FragmentActivity.getLiveDataViewModel(clazz: Class<T>): T {
    clazz.checkViewModelType(AbsLiveDataViewModel::class.java)

    val dst = this

    return ViewModelProviders.of(this, ViewModelFactory()).get(clazz)
}

fun <T : ViewModel> Fragment.getLiveDataViewModel(clazz: Class<T>): T {
    clazz.checkViewModelType(AbsLiveDataViewModel::class.java)

    val dst = this
    return ViewModelProviders.of(this, ViewModelFactory()).get(clazz)
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

