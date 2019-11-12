package com.wongki.framework.http.retrofit.core

import android.util.Log
import com.wongki.framework.BuildConfig
import com.wongki.framework.http.base.IRequester
import com.wongki.framework.http.interceptor.ErrorInterceptorNode
import com.wongki.framework.http.retrofit.IRetrofitRequester
import com.wongki.framework.http.retrofit.observer.HttpCommonObserver
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
@DslMarker
annotation class RetrofitServiceDslMarker

@RetrofitServiceDslMarker
abstract class RetrofitServiceCore<API> : AbsRetrofitServiceCore<API>(), ISSL {
    /**
     * 请求服务器的api接口定义，请继续调用[thenCall]发起网络请求
     * @param api
     */
    fun <RESPONSE_DATA> api(api: API.() -> Observable<RESPONSE_DATA>): RequesterBuilderCreator<RESPONSE_DATA> {
        return RequesterBuilderCreator(api)
    }

    /**
     * 网络请求构建器生成器
     */
    @RetrofitServiceDslMarker
    inner class RequesterBuilderCreator<RESPONSE_DATA>(private val api: API.() -> Observable<RESPONSE_DATA>) {
        internal fun create() = this@RetrofitServiceCore.RequesterBuilder<RESPONSE_DATA>().apply {
            this.api(this@RequesterBuilderCreator.api)
        }
    }

    /**
     * 网络请求构建器
     */
    @RetrofitServiceDslMarker
    inner class RequesterBuilder<RESPONSE_DATA> {
        var lifecycleObserver: IHttpDestroyedObserver? = null
        private lateinit var api: API.() -> Observable<RESPONSE_DATA>
        private var observerBuilder: RetrofitRequesterObserverBuilder<RESPONSE_DATA>? = null

        /**
         * api请求
         */
        internal fun api(api: API.() -> Observable<RESPONSE_DATA>): RequesterBuilder<RESPONSE_DATA> {
            this.api = api
            return this
        }

        /**
         * 观察此次的api请求
         */
        fun observer(init: RetrofitRequesterObserverBuilder<RESPONSE_DATA>.() -> Unit): RequesterBuilder<RESPONSE_DATA> {
            val requesterObserverBuilder =
                this@RetrofitServiceCore.RetrofitRequesterObserverBuilder<RESPONSE_DATA>()
            requesterObserverBuilder.init()
            this.observerBuilder = requesterObserverBuilder
            return this
        }


        internal fun build(): RetrofitRequester<RESPONSE_DATA> {
            val retrofitRequester = this@RetrofitServiceCore.RetrofitRequester<RESPONSE_DATA>()
            retrofitRequester.newRequest(api)
            retrofitRequester.setRequesterObserver(observerBuilder)
            return retrofitRequester
        }
    }

    @RetrofitServiceDslMarker
    inner class RetrofitRequesterObserverBuilder<RESPONSE_DATA> {
        internal var onStart: (() -> Unit)? = null
        internal var onFailed: ((Int, String) -> Boolean)? = null
        internal var onCancel: (() -> Unit)? = null
        internal var onSuccess: (RESPONSE_DATA.() -> Unit)? = null
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
        private lateinit var api: API.() -> Observable<RESPONSE_DATA>
        /**
         * 错误拦截器链表
         */
        private var errorInterceptorLinked: ErrorInterceptorNode? = null
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
        private var lifecycleObserver: WeakReference<IHttpDestroyedObserver?>? = null
        private var mDisposable: WeakReference<Disposable?>? = null

        override fun newRequest(request: API.() -> Observable<RESPONSE_DATA>): IRetrofitRequester<API, RESPONSE_DATA> {
            this.api = request
            return this
        }

        override fun lifecycleObserver(lifecycleObserver: () -> IHttpDestroyedObserver): IRetrofitRequester<API, RESPONSE_DATA> {
            this.lifecycleObserver = WeakReference(lifecycleObserver())
            return this
        }

        override fun compose(composer: ObservableTransformer<RESPONSE_DATA, RESPONSE_DATA>): IRetrofitRequester<API, RESPONSE_DATA> {
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

        override fun onCancel(onCancel: () -> Unit): IRetrofitRequester<API, RESPONSE_DATA> {
            this.onCancel = onCancel
            return this
        }

        override fun request(): IRetrofitRequester<API, RESPONSE_DATA> {
            core.realRequest(
                api = api,
                composer = composer ?: RxSchedulers.applyRetrofitHttpDefaultSchedulers(),
                errorInterceptor = errorInterceptorLinked ?: core.selfServiceApiErrorInterceptor,
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
        api: API.() -> Observable<RESPONSE_DATA>,
        composer: ObservableTransformer<RESPONSE_DATA, RESPONSE_DATA>,
        errorInterceptor: ErrorInterceptorNode? = null,
        onFailed: (Int, String) -> Boolean,
        onSuccess: (RESPONSE_DATA) -> Unit,
        onStart: (Disposable) -> Unit,
        onComplete: () -> Unit
    ) {
        super.request(api, composer)
            .subscribe(object : HttpCommonObserver<RESPONSE_DATA>(
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



/**
 * 设置完api，接下来你构建请求网络相关的
 */
fun <API,RESPONSE_DATA> RetrofitServiceCore<API>.RequesterBuilderCreator<RESPONSE_DATA>.thenCall(init: RetrofitServiceCore<API>.RequesterBuilder<RESPONSE_DATA>.() -> Unit): IRequester {
    val requesterBuilder = create()
    requesterBuilder.init()
    val requester = requesterBuilder.build()
    requester.request()
    return requester
}

