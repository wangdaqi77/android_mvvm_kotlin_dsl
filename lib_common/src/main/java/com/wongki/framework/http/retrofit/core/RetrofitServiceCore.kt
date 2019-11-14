package com.wongki.framework.http.retrofit.core

import com.wongki.framework.BuildConfig
import com.wongki.framework.http.HttpDsl
import com.wongki.framework.http.base.IRequester
import com.wongki.framework.http.config
import com.wongki.framework.http.config.HttpConfig
import com.wongki.framework.http.config.HttpConfigBuilder
import com.wongki.framework.http.config.IHttpConfig
import com.wongki.framework.http.interceptor.ApiErrorInterceptorNode
import com.wongki.framework.http.retrofit.IRetrofitRequester
import com.wongki.framework.http.retrofit.converter.GsonConverterFactory
import com.wongki.framework.http.retrofit.lifecycle.IHttpDestroyedObserver
import com.wongki.framework.http.ssl.ISSL
import com.wongki.framework.http.ssl.SSLFactory
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


@HttpDsl
abstract class RetrofitServiceCore<SERVICE> : AbsRetrofitServiceCore<SERVICE>(), ISSL {

    /**
     * 请求服务器的api接口定义，请继续调用[thenCall]发起网络请求
     * @param api
     */
    @HttpDsl
    fun <RESPONSE_DATA> api(api: SERVICE.() -> Observable<RESPONSE_DATA>): RequesterBuilderCreator<RESPONSE_DATA> {
        return RequesterBuilderCreator(api)
    }

    /**
     * 网络请求构建器生成器
     */
    @HttpDsl
    inner class RequesterBuilderCreator<RESPONSE_DATA>(private val api: SERVICE.() -> Observable<RESPONSE_DATA>) {
        internal fun create() = this@RetrofitServiceCore.RequesterBuilder<RESPONSE_DATA>().apply {
            this.api(this@RequesterBuilderCreator.api)
        }
    }

    /**
     * 网络请求构建器
     */
    @HttpDsl
    inner class RequesterBuilder<RESPONSE_DATA> {
        @HttpDsl
        var lifecycleObserver: IHttpDestroyedObserver? = null
        private lateinit var api: SERVICE.() -> Observable<RESPONSE_DATA>
        // 默认使用默认的配置
        private var config: HttpConfig = this@RetrofitServiceCore.defaultConfig
        private var observerBuilder: RetrofitRequesterObserverBuilder<RESPONSE_DATA>? = null

        /**
         * api请求
         */
        @HttpDsl
        internal fun api(api: SERVICE.() -> Observable<RESPONSE_DATA>): RequesterBuilder<RESPONSE_DATA> {
            this.api = api
            return this
        }

        /**
         * 在默认配置的基础上进行配置[defaultConfig]
         */
        @HttpDsl
        fun config(init: HttpConfigBuilder.() -> Unit): RequesterBuilder<RESPONSE_DATA> {
            this.config = this@RetrofitServiceCore.defaultConfig.config(init)
            return this
        }

        /**
         * 生成独立的配置
         */
        @HttpDsl
        fun newConfig(init: HttpConfigBuilder.() -> Unit): RequesterBuilder<RESPONSE_DATA> {
            this.config = this@RetrofitServiceCore.newConfig(init)
            return this
        }

        /**
         * 观察此次的api请求
         */
        @HttpDsl
        fun observer(init: RetrofitRequesterObserverBuilder<RESPONSE_DATA>.() -> Unit): RequesterBuilder<RESPONSE_DATA> {
            val requesterObserverBuilder = this@RetrofitServiceCore.RetrofitRequesterObserverBuilder<RESPONSE_DATA>()
            requesterObserverBuilder.init()
            this.observerBuilder = requesterObserverBuilder
            return this
        }

        internal fun build(): RetrofitRequester<RESPONSE_DATA> {
            val retrofitRequester = this@RetrofitServiceCore.RetrofitRequester<RESPONSE_DATA>()
            retrofitRequester.api(api)
            retrofitRequester.config(config)
            retrofitRequester.setRequesterObserver(observerBuilder)
            return retrofitRequester
        }
    }

    @HttpDsl
    inner class RetrofitRequesterObserverBuilder<RESPONSE_DATA> {
        internal var onStart: (() -> Unit)? = null
        internal var onFailed: ((Int, String) -> Boolean)? = null
        internal var onCancel: (() -> Unit)? = null
        internal var onSuccess: (RESPONSE_DATA.() -> Unit)? = null

        /**
         * 当开始发起请求
         */
        @HttpDsl
        fun onStart(onStart: () -> Unit) {
            this.onStart = onStart
        }

        /**
         * 当取消请求时
         */
        @HttpDsl
        fun onCancel(onCancel: () -> Unit) {
            this.onCancel = onCancel
        }

        /**
         * 当成功时
         */
        @HttpDsl
        fun onSuccess(onSuccess: RESPONSE_DATA?.() -> Unit) {
            this.onSuccess = onSuccess
        }

        /**
         * 当失败时
         */
        @HttpDsl
        fun onFailed(onFailed: (Int, String) -> Boolean) {
            this.onFailed = onFailed
        }


    }

    /**
     * 每次请求都会构建一个retrofit请求器
     */
    @HttpDsl
    inner class RetrofitRequester<RESPONSE_DATA> : IRetrofitRequester<SERVICE, RESPONSE_DATA>() {
        private var core = this@RetrofitServiceCore
        private var lifecycleObserver: WeakReference<IHttpDestroyedObserver?>? = null
        private lateinit var callApi: SERVICE.() -> Observable<RESPONSE_DATA>
        private lateinit var config: HttpConfig
        /**
         * 开始
         */
        private var onStart: (() -> Unit)? = null
        /**
         * 失败
         */
        private var onFailed: ((Int, String) -> Boolean)? = null
        /**
         * 取消
         */
        private var onCancel: (() -> Unit)? = null
        /**
         * 成功
         */
        private var onSuccess: (RESPONSE_DATA.() -> Unit)? = null
        private var composer: ObservableTransformer<RESPONSE_DATA, RESPONSE_DATA>? = null
        private var mDisposable: WeakReference<Disposable?>? = null

        override fun lifecycleObserver(lifecycleObserver: () -> IHttpDestroyedObserver): IRetrofitRequester<SERVICE, RESPONSE_DATA> {
            this.lifecycleObserver = WeakReference(lifecycleObserver())
            return this
        }

       override fun api(api: SERVICE.() -> Observable<RESPONSE_DATA>): IRetrofitRequester<SERVICE, RESPONSE_DATA> {
            this.callApi = api
            return this
        }

        override fun config(config: HttpConfig): IRetrofitRequester<SERVICE, RESPONSE_DATA> {
            this.config = config
            return this
        }

        override fun compose(composer: ObservableTransformer<RESPONSE_DATA, RESPONSE_DATA>): IRetrofitRequester<SERVICE, RESPONSE_DATA> {
            this.composer = composer
            return this
        }


        override fun onStart(onStart: () -> Unit): IRetrofitRequester<SERVICE, RESPONSE_DATA> {
            this.onStart = onStart
            return this
        }

        /**
         * @param onFailed 业务层返回true是代表业务层处理了该错误码，否则该错误码交给框架层进行娄底处理
         */
        override fun onFailed(onFailed: (Int, String) -> Boolean): IRetrofitRequester<SERVICE, RESPONSE_DATA> {
            this.onFailed = onFailed
            return this
        }

        override fun onSuccess(onSuccess: RESPONSE_DATA?.() -> Unit): IRetrofitRequester<SERVICE, RESPONSE_DATA> {
            this.onSuccess = onSuccess
            return this
        }

        override fun onCancel(onCancel: () -> Unit): IRetrofitRequester<SERVICE, RESPONSE_DATA> {
            this.onCancel = onCancel
            return this
        }

        override fun request(): IRetrofitRequester<SERVICE, RESPONSE_DATA> {
            core.realRequest(
                retrofit = if (config == this@RetrofitServiceCore.defaultConfig) {
                    this@RetrofitServiceCore.defaultRetrofit
                } else {
                    this@RetrofitServiceCore.generateRetrofit(config)
                },
                callApi = callApi,
                errorInterceptor = config.apiErrorInterceptorNode,
                composer = composer ?: RxSchedulers.applyRetrofitHttpDefaultSchedulers(),
                onStart = { disposable ->
                    addRequester2Manager(disposable)
                    onStart?.invoke()
                },
                onSuccess = { response ->
                    onSuccess?.invoke(response)
                },
                onComplete = {
                    removeRequesterFromManager()
                },
                onFailed = onFailed@{ code, message ->
                    removeRequesterFromManager()
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
                lifecycleObserver?.get()?.let { observer ->
                    core.getHttpRequesterManager().removeRequester(observer, this)
                }
                onCancel?.invoke()
            }
        }


        /**
         * 添加请求到请求管理器
         */
        private fun addRequester2Manager(disposable: Disposable) {
            this.mDisposable = WeakReference(disposable)
            //添加请求
            lifecycleObserver?.get()?.let { tag ->
                core.getHttpRequesterManager().addRequester(tag, this@RetrofitRequester)
            }
        }

        /**
         * 从管理器移除请求
         */
        private fun removeRequesterFromManager() {
            // 完成remove请求
            lifecycleObserver?.get()?.let { tag ->
                core.getHttpRequesterManager().removeRequester(tag, this@RetrofitRequester)
            }
        }

        private fun getDisposable() = mDisposable?.get()

        internal fun setRequesterObserver(requesterObserver: RetrofitRequesterObserverBuilder<RESPONSE_DATA>?) {
            requesterObserver ?: return
            this.onStart = requesterObserver.onStart
            this.onCancel = requesterObserver.onCancel
            this.onSuccess = requesterObserver.onSuccess
            this.onFailed = requesterObserver.onFailed
        }
    }

    /*****SSL相关******/
    override fun getSSLSocketFactory() = SSLFactory.DEFAULT.getSSLSocketFactory()

    /*****SSL相关******/
    override fun getHostnameVerifier() = SSLFactory.DEFAULT.getHostnameVerifier()


    /**
     * 生成retrofit
     */
    override fun generateDefaultRetrofit(): Retrofit {
        return generateRetrofit(defaultConfig)
    }

    private fun generateRetrofit(config: IHttpConfig): Retrofit {
        val host = config.check(config.host, "host")
        val connectTimeOut = config.check(config.connectTimeOut, "connectTimeOut")
        val writeTimeOut = config.check(config.writeTimeOut, "writeTimeOut")
        val readTimeOut = config.check(config.readTimeOut, "readTimeOut")


        val okHttpBuilder = OkHttpClient.Builder()
        okHttpBuilder.connectTimeout(connectTimeOut, TimeUnit.MILLISECONDS)
        okHttpBuilder.writeTimeout(writeTimeOut, TimeUnit.MILLISECONDS)
        okHttpBuilder.readTimeout(readTimeOut, TimeUnit.MILLISECONDS)

        // 错误重连
        okHttpBuilder.retryOnConnectionFailure(true)

        addCommonHeaders(okHttpBuilder, config.commonHeader)
        addCommonUrlParams(okHttpBuilder, config.commonUrlQueryParams)

        okHttpBuilder.addInterceptor(
            HttpLoggingInterceptor(HttpLoggingInterceptor.Logger { message ->
                config.logger?.log(message)
            }).setLevel(HttpLoggingInterceptor.Level.BODY)
        )

        // https手机无须安装代理软件的证书就可以明文查看数据
        if (BuildConfig.DEBUG) {
            getSSLSocketFactory()?.let {
                okHttpBuilder.sslSocketFactory(it)
            }
            getHostnameVerifier()?.let {
                okHttpBuilder.hostnameVerifier(it)
            }
        }

        return Retrofit.Builder()
            .baseUrl(host)
            .client(okHttpBuilder.build())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    /**
     * 发送网络请求
     */
    private fun <RESPONSE_DATA> realRequest(
        retrofit: Retrofit,
        callApi: SERVICE.() -> Observable<RESPONSE_DATA>,
        composer: ObservableTransformer<RESPONSE_DATA, RESPONSE_DATA>,
        errorInterceptor: ApiErrorInterceptorNode? = null,
        onFailed: (Int, String) -> Boolean,
        onSuccess: (RESPONSE_DATA) -> Unit,
        onStart: (Disposable) -> Unit,
        onComplete: () -> Unit
    ) {
        super.request(
            retrofit,
            callApi,
            composer,
            errorInterceptor,
            onFailed,
            onSuccess,
            onStart,
            onComplete
        )

    }

}


/**
 * 设置完api，接下来你构建请求网络相关的
 */
@HttpDsl
fun <API, RESPONSE_DATA> RetrofitServiceCore<API>.RequesterBuilderCreator<RESPONSE_DATA>.thenCall(
    init: RetrofitServiceCore<API>.RequesterBuilder<RESPONSE_DATA>.() -> Unit
): IRequester {
    val requesterBuilder = create()
    requesterBuilder.init()
    val requester = requesterBuilder.build()
    requester.request()
    return requester
}

