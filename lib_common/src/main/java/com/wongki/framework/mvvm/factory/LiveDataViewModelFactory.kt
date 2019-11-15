package com.wongki.framework.mvvm.factory

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.wongki.framework.mvvm.exception.ViewModelNoMatchedException
import com.wongki.framework.mvvm.lifecycle.LiveDataViewModel
import com.wongki.framework.mvvm.model.repo.IRepository
import java.lang.reflect.ParameterizedType
import kotlin.reflect.jvm.internal.impl.metadata.ProtoBuf

/**
 * @author  wangqi
 * date:    2019/7/11
 * email:   wangqi7676@163.com
 * desc:    .
 */
class LiveDataViewModelFactory(private val owner: LifecycleOwner) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val viewModel = super.create(modelClass)
        if (viewModel !is LiveDataViewModel<*>) {
            throw ViewModelNoMatchedException(modelClass)
        }

        viewModel.createRepo(findRepoClass(viewModel))

        viewModel.setLifecycleOwner(owner)
        return viewModel
    }

    private fun findRepoClass(viewModel: Any): Class<*> {
        val viewModelJavaClass = viewModel.javaClass
        var lastJavaClass: Class<*>? = viewModelJavaClass
        var javaClass = viewModelJavaClass.superclass
        val liveDataViewModelJavaClass = LiveDataViewModel::class.java
        while (javaClass != null && javaClass.name != liveDataViewModelJavaClass.name) {
            lastJavaClass = javaClass
            javaClass = javaClass.superclass
        }

        if (javaClass == null || lastJavaClass == null) {
            throw ViewModelNoMatchedException(viewModelJavaClass)
        }

        val typeClass = lastJavaClass.genericSuperclass as ParameterizedType
        return typeClass.actualTypeArguments[0] as Class<*>
    }

}