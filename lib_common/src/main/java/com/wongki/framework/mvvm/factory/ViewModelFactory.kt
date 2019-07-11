package com.wongki.framework.mvvm.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * @author  wangqi
 * date:    2019/7/11
 * email:   wangqi7676@163.com
 * desc:    .
 */
class ViewModelFactory: ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T  = modelClass.newInstance()
}