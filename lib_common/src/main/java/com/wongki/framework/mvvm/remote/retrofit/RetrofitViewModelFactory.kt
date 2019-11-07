package com.wongki.framework.mvvm.remote.retrofit

import com.wongki.framework.extensions.WeakDelegate
import com.wongki.framework.http.retrofit.lifecycle.IHttpDestroyedObserver
import com.wongki.framework.mvvm.factory.ViewModelFactoryForAdapter

/**
 * @author  wangqi
 * date:    2019/7/3
 * email:   wangqi7676@163.com
 * desc:    .
 */
class RetrofitViewModelFactory(initializer: () -> IHttpDestroyedObserver?) : ViewModelFactoryForAdapter<IRetrofitRepo>() {
    private val observer by WeakDelegate<IHttpDestroyedObserver>(initializer)
    override fun adapt(modelMethod: ViewModelMethod, args: Array<Any>): Any? {
        val method = modelMethod.method
        if (method.name == "getHttpRetrofitLifecycleObserver") {
            return observer
        }
        return null
    }
}