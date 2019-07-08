package com.wongki.framework.http.retrofit

import com.wongki.framework.http.base.IRequester
import com.wongki.framework.http.retrofit.lifecycle.IHttpRetrofitLifecycleObserver
import com.wongki.framework.model.domain.CommonResponse
import io.reactivex.Observable
import io.reactivex.ObservableTransformer

/**
 * @author  wangqi
 * date:    2019/6/20
 * email:   wangqi@feigeter.com
 * desc:    请求器
 */
abstract class IRetrofitRequester<API, RESPONSE_DATA> : IRequester {
    abstract fun newRequester(rxLifecycleObserver: IHttpRetrofitLifecycleObserver?, request: (API) -> Observable<CommonResponse<RESPONSE_DATA>>): IRetrofitRequester<API, RESPONSE_DATA>

    abstract  fun compose(composer: ObservableTransformer<CommonResponse<RESPONSE_DATA>, CommonResponse<RESPONSE_DATA>>): IRetrofitRequester<API, RESPONSE_DATA>

    abstract  fun addErrorInterceptor(errorInterceptor: ErrorInterceptor): IRetrofitRequester<API, RESPONSE_DATA>

    abstract  fun onStart(onStart: () -> Unit): IRetrofitRequester<API, RESPONSE_DATA>

    abstract  fun onFailed(onFailed: (Int, String?) -> Boolean): IRetrofitRequester<API, RESPONSE_DATA>

    abstract  fun onSuccess(onSuccess: (RESPONSE_DATA?) -> Unit): IRetrofitRequester<API, RESPONSE_DATA>

    abstract fun onFullSuccess(onFullSuccess: (CommonResponse<RESPONSE_DATA>) -> Unit): IRetrofitRequester<API, RESPONSE_DATA>

    abstract  fun onCancel(onCancel: () -> Unit): IRetrofitRequester<API, RESPONSE_DATA>
}