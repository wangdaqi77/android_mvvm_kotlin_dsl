package com.wongki.demo.model.repo.remote

import androidx.lifecycle.LifecycleOwner
import com.wongki.framework.mvvm.model.repo.IRepository
import com.wongki.framework.mvvm.model.repo.remote.retrofit.IRetrofitRepo
import java.lang.ref.WeakReference

/**
 * @author  wangqi
 * date:    2019-11-15
 * email:   wangqi7676@163.com
 * desc:    .
 */
abstract class BaseRepository : IRepository, IRetrofitRepo {
    internal lateinit var lifecycleOwnerRef: WeakReference<LifecycleOwner?>

    override fun setLifecycleOwner(lifecycleOwner: LifecycleOwner) {
        this.lifecycleOwnerRef = WeakReference(lifecycleOwner)
    }

    override fun getLifecycleOwner(): LifecycleOwner? = lifecycleOwnerRef.get()
}