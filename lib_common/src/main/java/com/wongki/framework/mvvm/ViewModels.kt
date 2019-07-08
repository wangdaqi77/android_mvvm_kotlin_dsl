package com.wongki.framework.mvvm

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import com.wongki.framework.http.retrofit.lifecycle.IHttpRetrofitLifecycleObserver
import com.wongki.framework.mvvm.retrofit.RetrofitLiveDataViewModel
import com.wongki.framework.mvvm.retrofit.RetrofitViewModelFactory
import java.lang.IllegalArgumentException

/**
 * @author  wangqi
 * date:    2019/7/3
 * email:   wangqi7676@163.com
 * desc:    .
 */

fun <T : ViewModel> FragmentActivity.getRetrofitLiveDataViewModel(clazz: Class<T>): T {
    clazz.checkViewModelType(RetrofitLiveDataViewModel::class.java)

    val dst = this
    val observer = if (dst is IHttpRetrofitLifecycleObserver) dst else null

    return ViewModelProviders.of(this, RetrofitViewModelFactory { observer }).get(clazz)
}

fun <T : ViewModel> Fragment.getRetrofitLiveDataViewModel(clazz: Class<T>): T {
    clazz.checkViewModelType(RetrofitLiveDataViewModel::class.java)

    val dst = this
    val observer = if (dst is IHttpRetrofitLifecycleObserver) dst else null
    return ViewModelProviders.of(this, RetrofitViewModelFactory { observer }).get(clazz)
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

