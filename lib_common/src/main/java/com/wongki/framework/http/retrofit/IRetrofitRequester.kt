package com.wongki.framework.http.retrofit

import com.wongki.framework.http.base.IRequester
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import com.wongki.framework.http.interceptor.ErrorInterceptorNode
import com.wongki.framework.http.retrofit.lifecycle.IHttpDestroyedObserver

/**
 * @author  wangqi
 * date:    2019/6/20
 * email:   wangqi7676@163.com
 * desc:    请求器
 */
abstract class IRetrofitRequester<API, RESPONSE_DATA> : IRequester {
    abstract fun newRequest(request: (API) -> Observable<RESPONSE_DATA>): IRetrofitRequester<API, RESPONSE_DATA>

    abstract fun lifecycleObserver(lifecycleObserver: ()-> IHttpDestroyedObserver): IRetrofitRequester<API, RESPONSE_DATA>

    abstract  fun compose(composer: ObservableTransformer<RESPONSE_DATA, RESPONSE_DATA>): IRetrofitRequester<API, RESPONSE_DATA>

    abstract  fun addErrorInterceptor(errorInterceptorNode: ErrorInterceptorNode): IRetrofitRequester<API, RESPONSE_DATA>

    abstract  fun onStart(onStart: () -> Unit): IRetrofitRequester<API, RESPONSE_DATA>

    abstract  fun onFailed(onFailed: (Int, String) -> Boolean): IRetrofitRequester<API, RESPONSE_DATA>

    abstract fun onSuccess(onSuccess: RESPONSE_DATA?.() -> Unit): IRetrofitRequester<API, RESPONSE_DATA>

    abstract  fun onCancel(onCancel: () -> Unit): IRetrofitRequester<API, RESPONSE_DATA>
}
