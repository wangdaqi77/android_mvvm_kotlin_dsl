package com.wongki.framework.mvvm.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.wongki.framework.mvvm.exception.ViewModelNoMatchedException
import com.wongki.framework.mvvm.lifecycle.LiveDataViewModel

/**
 * @author  wangqi
 * date:    2019/7/11
 * email:   wangqi7676@163.com
 * desc:    .
 */
object LiveDataViewModelFactory: ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        if (!LiveDataViewModel::class.java.isAssignableFrom(modelClass) ){
            throw ViewModelNoMatchedException(modelClass)
        }
        return super.create(modelClass)
    }

}