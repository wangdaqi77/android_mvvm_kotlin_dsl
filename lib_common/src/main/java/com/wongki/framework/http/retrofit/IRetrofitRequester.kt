package com.wongki.framework.http.retrofit

import com.wongki.framework.http.base.IRequester
import com.wongki.framework.http.config.HttpConfig
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import com.wongki.framework.http.interceptor.ApiErrorInterceptorNode
import com.wongki.framework.http.retrofit.lifecycle.IHttpDestroyedObserver

/**
 * @author  wangqi
 * date:    2019/6/20
 * email:   wangqi7676@163.com
 * desc:    请求器
 */
abstract class IRetrofitRequester<SERVICE, RESPONSE_DATA> : IRequester {

    abstract fun lifecycleObserver(lifecycleObserver: () -> IHttpDestroyedObserver): IRetrofitRequester<SERVICE, RESPONSE_DATA>

    abstract fun api(api: SERVICE.() -> Observable<RESPONSE_DATA>): IRetrofitRequester<SERVICE, RESPONSE_DATA>

    abstract fun config(config: HttpConfig): IRetrofitRequester<SERVICE, RESPONSE_DATA>

    abstract fun compose(composer: ObservableTransformer<RESPONSE_DATA, RESPONSE_DATA>): IRetrofitRequester<SERVICE, RESPONSE_DATA>

    abstract fun onStart(onStart: () -> Unit): IRetrofitRequester<SERVICE, RESPONSE_DATA>

    abstract fun onFailed(onFailed: (Int, String) -> Boolean): IRetrofitRequester<SERVICE, RESPONSE_DATA>

    abstract fun onSuccess(onSuccess: RESPONSE_DATA?.() -> Unit): IRetrofitRequester<SERVICE, RESPONSE_DATA>

    abstract fun onCancel(onCancel: () -> Unit): IRetrofitRequester<SERVICE, RESPONSE_DATA>
}
