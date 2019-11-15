package com.wongki.framework.mvvm.model.repo

import androidx.lifecycle.LifecycleOwner
import com.wongki.framework.mvvm.model.repo.remote.retrofit.IRetrofitRepo

/**
 * @author  wangqi
 * date:    2019-11-15
 * email:   wangqi7676@163.com
 * desc:    .
 */
interface IRepository : IRetrofitRepo {
    fun setLifecycleOwner(lifecycleOwner: LifecycleOwner)
    fun getLifecycleOwner(): LifecycleOwner?
}