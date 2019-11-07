package com.wongki.framework.http.retrofit.core

import android.util.Log
import com.wongki.framework.BuildConfig
import com.wongki.framework.http.interceptor.ErrorInterceptorNode
import com.wongki.framework.http.retrofit.IRetrofitRequester
import com.wongki.framework.http.retrofit.observer.HttpCommonObserver
import com.wongki.framework.http.retrofit.converter.GsonConverterFactory
import com.wongki.framework.http.retrofit.lifecycle.IHttpRetrofitLifecycleObserver
import com.wongki.framework.http.ssl.ISSL
import com.wongki.framework.http.ssl.SSLFactory
import com.wongki.framework.model.domain.CommonResponse
import com.wongki.framework.utils.RxSchedulers
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
 * email:   wangqi7676@163.com
 * desc:    retrofit网络请求框架核心类
 *
 */
abstract class RetrofitServiceCore<API> : AbsRetrofitServiceCore<API>(), ISSL {

    /**
     * 创建新的网络请求器
     * @param preRequest 请求服务器的api接口定义
     * @param init 初始化网络请求器
     */
    fun <RESPONSE_DATA> call(preRequest:API.()->Observable<CommonResponse<RESPONSE_DATA>>, init: RequesterBuilder<RESPONSE_DATA>.()-> RetrofitRequester<RESPONSE_DATA>):RetrofitRequester<RESPONSE_DATA>{
        return RequesterBuilder<RESPONSE_DATA>().let{
            it.api(preRequest)
            it.init()
        }
    }

    /**
     * 新的网络请求器
     * @param init 初始化网络请求器
     */
    fun <RESPONSE_DATA> call(init: RequesterBuilder<RESPONSE_DATA>.() -> RetrofitRequester<RESPONSE_DATA>): RetrofitRequester<RESPONSE_DATA> {
        return RequesterBuilder<RESPONSE_DATA>().init()
    }

    /**
     * 新的网络请求器
     * @param init 初始化网络请求器
     */
    fun <T> callArrayList(init: RequesterBuilder<ArrayList<T>>.() -> RetrofitRequester<ArrayList<T>>): RetrofitRequester<ArrayList<T>> = call(init)

    @RetrofitServiceDslMarker
    inner class RequesterBuilder<RESPONSE_DATA> {
        private var rxLifecycleObserver: WeakReference<IHttpRetrofitLifecycleObserver?>? = null
        private lateinit var preRequest: API.() -> Observable<CommonResponse<RESPONSE_DATA>>

        /**
         * 生命周期观察期
         */
        fun lifecycleObserver(lifecycleObserver: () -> IHttpRetrofitLifecycleObserver) {
            rxLifecycleObserver = WeakReference(lifecycleObserver())
        }

        /**
         * api请求
         */
        fun api(preRequest: API.() -> Observable<CommonResponse<RESPONSE_DATA>>) {
            this.preRequest = preRequest
        }

        /**
         * 观察此次的api请求
         */
        fun observer(init: RetrofitRequesterObserverBuilder<RESPONSE_DATA>.() -> Unit):RetrofitRequester<RESPONSE_DATA> {
            val requesterObserverBuilder = this@RetrofitServiceCore.RetrofitRequesterObserverBuilder<RESPONSE_DATA>()
            requesterObserverBuilder.init()

            val retrofitRequester = this@RetrofitServiceCore.RetrofitRequester<RESPONSE_DATA>()
            retrofitRequester.newRequest(preRequest)
            retrofitRequester.setRequesterObserver(requesterObserverBuilder)
            retrofitRequester.request()
            return retrofitRequester
        }
    }

    @RetrofitServiceDslMarker
    inner class RetrofitRequesterObserverBuilder<RESPONSE_DATA> {
        internal var onStart: (() -> Unit)? = null
        internal var onSuccess: ((RESPONSE_DATA?) -> Unit)? = null
        internal var onFailed: ((Int, String) -> Boolean)? = null
        internal var onCancel: (() -> Unit)? = null
        internal var onFullSuccess: (CommonResponse<RESPONSE_DATA>.() -> Unit)? = null
        internal var errorInterceptor: ErrorInterceptorNode? = null

        fun onErrorIntercept(onErrorIntercept: (Int, String?) -> Boolean) {
            this.errorInterceptor = object : ErrorInterceptorNode() {
                override val tag: String = "api调用处的错误拦截器"
                override fun onInterceptError(code: Int, message: String): Boolean {
                    return onErrorIntercept.invoke(code, message)
                }
            }
        }

        fun onStart(onStart: () -> Unit) {
            this.onStart = onStart
        }

        fun onFailed(onFailed: (Int, String) -> Boolean) {
            this.onFailed = onFailed
        }

        fun onSuccess(onSuccess: RESPONSE_DATA?.() -> Unit) {
            this.onSuccess = onSuccess
        }

        fun onFullSuccess(onFullSuccess: CommonResponse<RESPONSE_DATA>?.() -> Unit) {
            this.onFullSuccess = onFullSuccess
        }

        fun onCancel(onCancel: () -> Unit) {
            this.onCancel = onCancel
        }


    }

    /**
     * 每次请求都会构建一个retrofit请求器
     */
    @RetrofitServiceDslMarker
    inner class RetrofitRequester<RESPONSE_DATA> : IRetrofitRequester<API, RESPONSE_DATA>() {
        private var core = this@RetrofitServiceCore
        private lateinit var preRequest: API.() -> Observable<CommonResponse<RESPONSE_DATA>>
        /**
         * 错误拦截器链表
         */
        private var errorInterceptorLinked: ErrorInterceptorNode? = null
        /**
         * 开始
         */
        private var onStart: (() -> Unit)? = null
        /**
         * 成功，返回data [CommonResponse.result]
         */
        private var onSuccess: ((RESPONSE_DATA?) -> Unit)? = null
        /**
         * 失败
         */
        private var onFailed: ((Int, String) -> Boolean)? = null
        /**
         * 取消
         */
        private var onCancel: (() -> Unit)? = null
        /**
         * 返回解析后完整的Response [CommonResponse]
         * 业务层同时设置[onSuccess]和[onFullSuccess]时，只会触发[onFullSuccess]
         */
        private var onFullSuccess: CommonResponse<RESPONSE_DATA>.() -> Unit = {
            onSuccess?.invoke(this.result)
        }
        private var composer: ObservableTransformer<CommonResponse<RESPONSE_DATA>, CommonResponse<RESPONSE_DATA>>? = null
        private var rxLifecycleObserver: WeakReference<IHttpRetrofitLifecycleObserver?>? = null
        private var mDisposable: WeakReference<Disposable?>? = null

        override fun newRequest(request: API.() -> Observable<CommonResponse<RESPONSE_DATA>>): IRetrofitRequester<API, RESPONSE_DATA> {
            this.preRequest = request
            return this
        }

        override fun lifecycleObserver(lifecycleObserver: () -> IHttpRetrofitLifecycleObserver): IRetrofitRequester<API, RESPONSE_DATA> {
            this.rxLifecycleObserver = WeakReference(lifecycleObserver())
            return this
        }

        override fun compose(composer: ObservableTransformer<CommonResponse<RESPONSE_DATA>, CommonResponse<RESPONSE_DATA>>): IRetrofitRequester<API, RESPONSE_DATA> {
            this.composer = composer
            return this
        }

        /**
         * 该方法适用于，如果该错误只想自己消费掉，
         * 并不想让全局的或者其他的拦截器拦截消费，
         * 那么[ErrorInterceptorNode.onInterceptError] Return true
         */
        override fun addErrorInterceptor(errorInterceptorNode: ErrorInterceptorNode): IRetrofitRequester<API, RESPONSE_DATA> {
            errorInterceptorNode.next = this.errorInterceptorLinked
                ?: core.selfServiceApiErrorInterceptor
            this.errorInterceptorLinked = errorInterceptorNode
            return this
        }

        override fun onStart(onStart: () -> Unit): IRetrofitRequester<API, RESPONSE_DATA> {
            this.onStart = onStart
            return this
        }

        /**
         * @param onFailed 业务层返回true是代表业务层处理了该错误码，否则该错误码交给框架层进行娄底处理
         */
        override fun onFailed(onFailed: (Int, String) -> Boolean): IRetrofitRequester<API, RESPONSE_DATA> {
            this.onFailed = onFailed
            return this
        }

        override fun onSuccess(onSuccess: RESPONSE_DATA?.() -> Unit): IRetrofitRequester<API, RESPONSE_DATA> {
            this.onSuccess = onSuccess
            return this
        }


        override fun onFullSuccess(onFullSuccess: CommonResponse<RESPONSE_DATA>?.() -> Unit): IRetrofitRequester<API, RESPONSE_DATA> {
            this.onFullSuccess = onFullSuccess
            return this
        }

        override fun onCancel(onCancel: () -> Unit): IRetrofitRequester<API, RESPONSE_DATA> {
            this.onCancel = onCancel
            return this
        }

        override fun request(): IRetrofitRequester<API, RESPONSE_DATA> {
            core.realRequest(
                preRequest = preRequest,
                composer = composer ?: RxSchedulers.applyRetrofitHttpDefaultSchedulers(),
                errorInterceptor = errorInterceptorLinked ?: core.selfServiceApiErrorInterceptor,
                onStart = { disposable ->
                    this.mDisposable = WeakReference(disposable)
                    //添加请求
                    rxLifecycleObserver?.get()?.let { tag ->
                        core.getLifecycle().addRequester(tag, this@RetrofitRequester)
                    }
                    onStart?.invoke()
                },
                onSuccess = onFullSuccess,
                onComplete = {
                    notifyRemoveRequester()
                },
                onFailed = onFailed@{ code, message ->
                    notifyRemoveRequester()
                    return@onFailed onFailed?.invoke(code, message) ?: false
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
                onCancel?.invoke()
            }
        }

        private fun notifyRemoveRequester() {
            // 完成remove请求
            rxLifecycleObserver?.get()?.let { tag ->
                core.getLifecycle().removeRequester(tag, this@RetrofitRequester)
            }
        }

        private fun getDisposable() = mDisposable?.get()

        internal fun setRequesterObserver(requesterObserver: RetrofitRequesterObserverBuilder<RESPONSE_DATA>) {
            this.onStart = requesterObserver.onStart
            this.onCancel = requesterObserver.onCancel
            this.onSuccess = requesterObserver.onSuccess
            this.onFailed = requesterObserver.onFailed

            requesterObserver.onFullSuccess?.apply {
                this@RetrofitRequester.onFullSuccess = this
            }

            errorInterceptorLinked = null
            requesterObserver.errorInterceptor?.apply {
                addErrorInterceptor(this)
            }
        }
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
        addCommonUrlParams(okHttpBuilder)
        addCommonHeaders(okHttpBuilder)
//        okHttpBuilder.addCommonPostParams(mCommonPostRequestParams)
        okHttpBuilder.addInterceptor(
            HttpLoggingInterceptor(CommonLogInterceptor).setLevel(
                HttpLoggingInterceptor.Level.BODY
            )
        )

        okHttpBuilder.connectTimeout(mConnectTimeOut, TimeUnit.MILLISECONDS)
        okHttpBuilder.writeTimeout(mWriteTimeOut, TimeUnit.MILLISECONDS)
        okHttpBuilder.readTimeout(mReadTimeOut, TimeUnit.MILLISECONDS)


        // 错误重连
        okHttpBuilder.retryOnConnectionFailure(true)

        // https手机无须安装代理软件的证书就可以明文查看数据
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
    private fun <RESPONSE_DATA> realRequest(
        preRequest: API.() -> Observable<CommonResponse<RESPONSE_DATA>>,
        composer: ObservableTransformer<CommonResponse<RESPONSE_DATA>, CommonResponse<RESPONSE_DATA>>,
        errorInterceptor: ErrorInterceptorNode? = null,
        onFailed: (Int, String) -> Boolean,
        onSuccess: (CommonResponse<RESPONSE_DATA>) -> Unit,
        onStart: (Disposable) -> Unit,
        onComplete: () -> Unit
    ) {
        super.request(preRequest, composer)
            .subscribe(object : HttpCommonObserver<CommonResponse<RESPONSE_DATA>>(
                errorInterceptor,
                onFailed,
                onSuccess
            ) {

                override fun onComplete() {
                    onComplete()
                }

                override fun onSubscribe(d: Disposable) {
                    onStart(d)
                }

            })
    }
}


