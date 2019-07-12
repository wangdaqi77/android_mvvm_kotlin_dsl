package com.wongki.framework.mvvm.remote.retrofit

import com.wongki.framework.http.retrofit.lifecycle.IHttpRetrofitLifecycleObserver
import java.lang.RuntimeException

/**
 * @author  wangqi
 * date:    2019/7/3
 * email:   wangqi7676@163.com
 * desc:    .
 */
interface IRetrofitViewModel : IHttpRetrofitLifecycleObserver {

    /**
     * 获取View层的[IHttpRetrofitLifecycleObserver]
     */
    fun getHttpRetrofitLifecycleObserver(): IHttpRetrofitLifecycleObserver? {
        throw RuntimeException("需要动态代理支持")
    }


}