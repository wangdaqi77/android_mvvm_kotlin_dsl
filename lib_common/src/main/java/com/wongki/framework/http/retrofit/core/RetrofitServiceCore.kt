package com.wongki.framework.http.retrofit.core

import android.util.Log
import com.wongki.framework.BuildConfig
import com.wongki.framework.http.retrofit.ErrorInterceptor
import com.wongki.framework.http.retrofit.IRetrofitRequester
import com.wongki.framework.http.retrofit.observer.HttpCommonObserver
import com.wongki.framework.http.retrofit.converter.GsonConverterFactory
import com.wongki.framework.http.retrofit.lifecycle.IHttpRetrofitLifecycleObserver
import com.wongki.framework.http.ssl.ISSL
import com.wongki.framework.http.ssl.SSLFactory
import com.wongki.framework.model.domain.CommonResponse
import com.wongki.framework.rx.RxSchedulers
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.disposables.Disposable
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.lang.ref.WeakReference
import java.util.concurrent.TimeUnit


/**
 * @author  wangqi
 * date:    2019/6/6
 * email:   wangqi@feigeter.com
 * desc:    retrofit网络请求框架核心类
 *
 */
abstract class RetrofitServiceCore<API> : AbsRetrofitServiceCore<API>(),ISSL {


    /**
     * 用于构建网络请求器
     * @param rxLifecycleObserver tag生命周期感知者
     * @param preRequest retrofit请求
     */
    fun <RESPONSE_DATA> newRequester(rxLifecycleObserver: IHttpRetrofitLifecycleObserver? = null, preRequest: (API) -> Observable<CommonResponse<RESPONSE_DATA>>): RetrofitRequester<API, RESPONSE_DATA> {
        val retrofitRequester = RetrofitRequester<API, RESPONSE_DATA>(this)
        retrofitRequester.newRequester(rxLifecycleObserver, preRequest)
        return retrofitRequester
    }


    /**
     * 每次请求都会构建一个retrofit请求器
     */
    class RetrofitRequester<API, RESPONSE_DATA>(private val core: RetrofitServiceCore<API>) : IRetrofitRequester<API, RESPONSE_DATA>() {


        companion object {
            val DEFAULT_onStart: () -> Unit = {}
            val DAFAULT_onFailed: (Int, String) -> Boolean = { _, _ -> false }
            val DEFAULT_onCancel: () -> Unit = {}
        }

        private lateinit var preRequest: (API) -> Observable<CommonResponse<RESPONSE_DATA>>
        /**
         * 拦截处理错误码
         * 优先级：[addErrorInterceptor] > [RetrofitServiceCore.errorInterceptor] > [HttpCommonObserver.onError]
         */
        private var errorInterceptor: ErrorInterceptor? = null
        /**
         * 开始
         */
        private var onStart: () -> Unit =
                DEFAULT_onStart
        /**
         * 取消
         */
        private var onCancel: () -> Unit =
                DEFAULT_onCancel

        /**
         * 返回解析后完整的Response [CommonResponse]
         * 业务层同时设置[onSuccess]和[onFullSuccess]时，只会触发[onFullSuccess]
         */
        private var onFullSuccess: (CommonResponse<RESPONSE_DATA>) -> Unit = { result -> onSuccess(result.result) }
        /**
         * 返回data [CommonResponse.result]
         */
        private var onSuccess: (RESPONSE_DATA?) -> Unit = { _ -> }
        private var onFailed: (Int, String) -> Boolean = DAFAULT_onFailed
        private var rxLifecycleObserver: WeakReference<IHttpRetrofitLifecycleObserver?>? = null
        private var composer: ObservableTransformer<CommonResponse<RESPONSE_DATA>, CommonResponse<RESPONSE_DATA>>? = null
        private var mDisposable: WeakReference<Disposable?>? = null
        override fun newRequester(rxLifecycleObserver: IHttpRetrofitLifecycleObserver?, request: (API) -> Observable<CommonResponse<RESPONSE_DATA>>): RetrofitRequester<API, RESPONSE_DATA> {
            rxLifecycleObserver?.let { observer ->
                this.rxLifecycleObserver = WeakReference(observer)
            }
            this.preRequest = request
            return this
        }


        override fun compose(composer: ObservableTransformer<CommonResponse<RESPONSE_DATA>, CommonResponse<RESPONSE_DATA>>): RetrofitRequester<API, RESPONSE_DATA> {
            this.composer = composer
            return this
        }

        override fun addErrorInterceptor(errorInterceptor: ErrorInterceptor): RetrofitRequester<API, RESPONSE_DATA> {
            errorInterceptor.next = this.errorInterceptor
                    ?: core.errorInterceptor
            this.errorInterceptor = errorInterceptor
            return this
        }

        override fun onStart(onStart: () -> Unit): RetrofitRequester<API, RESPONSE_DATA> {
            this.onStart = onStart
            return this
        }

        /**
         * @param onFailed 业务层返回true是代表业务层处理了该错误码，否则该错误码交给框架层处理
         */
        override fun onFailed(onFailed: (Int, String?) -> Boolean): RetrofitRequester<API, RESPONSE_DATA> {
            this.onFailed = onFailed
            return this
        }

        override fun onSuccess(onSuccess: (RESPONSE_DATA?) -> Unit): RetrofitRequester<API, RESPONSE_DATA> {
            this.onSuccess = onSuccess
            return this
        }


        override fun onFullSuccess(onFullSuccess: (CommonResponse<RESPONSE_DATA>) -> Unit): RetrofitRequester<API, RESPONSE_DATA> {
            this.onFullSuccess = onFullSuccess
            return this
        }

        override fun onCancel(onCancel: () -> Unit): RetrofitRequester<API, RESPONSE_DATA> {
            this.onCancel = onCancel
            return this
        }

        override fun request(): RetrofitRequester<API, RESPONSE_DATA> {
            core.realRequestOnLifecycle(
                    preRequest = preRequest,
                    composer = composer ?: RxSchedulers.applyRetrofitHttpDefaultSchedulers(),
                    errorInterceptor = errorInterceptor,
                    onStart = { disposable ->
                        this.mDisposable = WeakReference(disposable)
                        //添加请求
                        rxLifecycleObserver?.get()?.let { tag ->
                            core.getLifecycle().addRequester(tag, this@RetrofitRequester)
                        }
                        onStart()
                    },
                    onSuccess = onFullSuccess,
                    onComplete = {
                        notifyRemoveRequester()
                    },
                    onFailed = onFailed@{ code, message ->
                        notifyRemoveRequester()
                        return@onFailed onFailed(code, message)
                    }
            )
            return this
        }

        override fun isCancel(): Boolean {
            return getDisposable()?.isDisposed ?: false
        }

        override fun cancel() {
            if (!isCancel()) {
                getDisposable()?.dispose()
                rxLifecycleObserver?.get()?.let { tag ->
                    core.getLifecycle().removeRequester(tag, this)
                }
                onCancel()
            }
        }

        private fun notifyRemoveRequester() {
            // 完成remove请求
            rxLifecycleObserver?.get()?.let { tag ->
                core.getLifecycle().removeRequester(tag, this@RetrofitRequester)
            }
        }

        private fun getDisposable() = mDisposable?.get()
    }

    /**
     * Log拦截器
     */
    protected object CommonLogInterceptor : HttpLoggingInterceptor.Logger {

        override fun log(message: String) {
            Log.i(javaClass.simpleName, message)
        }
    }
    /*****SSL相关******/
    override fun getSSLSocketFactory() = SSLFactory.DEFAULT.getSSLSocketFactory()

    /*****SSL相关******/
    override fun getHostnameVerifier() = SSLFactory.DEFAULT.getHostnameVerifier()

    /**
     * 生成retrofit
     */
    override fun generateRetrofit(): Retrofit {
        val okHttpBuilder = OkHttpClient.Builder()
        //builder.cookieJar(cookieJar);
        okHttpBuilder.addCommonUrlParams(mCommonUrlRequestParams)
        okHttpBuilder.addCommonHeaders(mCommonRequestHeader)
//        okHttpBuilder.addCommonPostParams(mCommonPostRequestParams)
        okHttpBuilder.addInterceptor(HttpLoggingInterceptor(CommonLogInterceptor).setLevel(HttpLoggingInterceptor.Level.BODY))

        okHttpBuilder.connectTimeout(mConnectTimeOut, TimeUnit.MILLISECONDS)
        okHttpBuilder.writeTimeout(mWriteTimeOut, TimeUnit.MILLISECONDS)
        okHttpBuilder.readTimeout(mReadTimeOut, TimeUnit.MILLISECONDS)


        // 错误重连
        okHttpBuilder.retryOnConnectionFailure(true)

        if (BuildConfig.DEBUG) {
            getSSLSocketFactory()?.let {
                okHttpBuilder.sslSocketFactory(it)
            }
            getHostnameVerifier()?.let {
                okHttpBuilder.hostnameVerifier(it)
            }
        }

        /*int[] certificates = {R.raw.myssl};//cer文件
        String hosts[] = {HConst.BASE_DEBUG_URL, HConst.BASE_PREVIEW_URL, HConst.BASE_RELEASE_URL, HConst.BASE_RELEASE_SHARE_URL};
        builder.socketFactory(HttpsFactroy.getSSLSocketFactory(context, certificates));
        builder.hostnameVerifier(HttpsFactroy.getHostnameVerifier(hosts));*/
        val retrofit: Retrofit = Retrofit.Builder()
                .client(okHttpBuilder.build())
                .baseUrl(mHost)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        return retrofit
    }

    /**
     * 发送网络请求
     */
    private fun <RESPONSE_DATA> realRequestOnLifecycle(preRequest: (API) -> Observable<CommonResponse<RESPONSE_DATA>>, composer: ObservableTransformer<CommonResponse<RESPONSE_DATA>, CommonResponse<RESPONSE_DATA>>, errorInterceptor: ErrorInterceptor? = null, onFailed: (Int, String) -> Boolean, onSuccess: (CommonResponse<RESPONSE_DATA>) -> Unit, onStart: (Disposable) -> Unit, onComplete: () -> Unit) {
        request(preRequest, composer)
                .subscribe(object : HttpCommonObserver<CommonResponse<RESPONSE_DATA>>(errorInterceptor, onFailed, onSuccess) {

                    override fun onComplete() {
                        onComplete()
                    }

                    override fun onSubscribe(d: Disposable) {
                        onStart(d)
                    }

                })
    }
}


