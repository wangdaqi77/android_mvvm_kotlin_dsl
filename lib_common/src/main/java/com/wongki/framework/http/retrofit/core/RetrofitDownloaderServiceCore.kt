package com.wongki.framework.http.retrofit.core

import com.wongki.framework.http.HttpErrorCode
import com.wongki.framework.http.base.IRequester
import com.wongki.framework.http.interceptor.ApiErrorInterceptorNode
import com.wongki.framework.http.retrofit.converter.GsonConverterFactory
import com.wongki.framework.http.retrofit.lifecycle.IHttpDestroyedObserver
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import com.wongki.framework.utils.safeClose
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import okio.*
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.io.*
import java.lang.ref.WeakReference
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


/**
 * @author  wangqi
 * date:    2019/7/1
 * email:   wangqi7676@163.com
 * desc:    retrofit下载器
 *
 */
abstract class RetrofitDownloaderServiceCore<API> : AbsRetrofitServiceCore<API>() {


    /**
     * 用于构建网络请求器
     * @param rxLifecycleObserver tag生命周期感知者
     * @param api retrofit请求
     */
    fun newRequester(
        rxLifecycleObserver: IHttpDestroyedObserver? = null,
        filePath: String,
        api: API.() -> Observable<ResponseBody>
    ): RetrofitDownloadRequester {
        val retrofitRequester = RetrofitDownloadRequester()
        retrofitRequester.newRequester(rxLifecycleObserver, filePath, api)
        return retrofitRequester
    }


    /**
     * 每次请求都会构建一个retrofit请求器
     */
    open inner class RetrofitDownloadRequester : IRequester {
        private lateinit var api: API.() -> Observable<ResponseBody>
        /**
         * 开始
         */
        private var onStart: (() -> Unit)? = null

        /**
         * 取消
         */
        private var onCancel: (() -> Unit)? = null

        /**
         * 下载进度
         */
        private var onProgress: ((Float) -> Unit)? = null

        /**
         * 下载成功
         */
        private var onSuccess: ((String) -> Unit)? = null

        /**
         * 下载失败
         */
        private var onFailed: ((Int, String) -> Boolean)? = null

        private var lifecycleObserver: WeakReference<IHttpDestroyedObserver?>? = null
        private var mDisposable: WeakReference<Disposable?>? = null
        private lateinit var filePath: String
        fun newRequester(
            lifecycleObserver: IHttpDestroyedObserver?,
            filePath: String,
            request: (API) -> Observable<ResponseBody>
        ): RetrofitDownloadRequester {
            lifecycleObserver?.let { observer ->
                this.lifecycleObserver = WeakReference(observer)
            }
            this.filePath = filePath
            this.api = request
            return this
        }


        fun onStart(onStart: () -> Unit): RetrofitDownloadRequester {
            this.onStart = onStart
            return this
        }

        fun onProgress(onProgress: (Float) -> Unit): RetrofitDownloadRequester {
            this.onProgress = onProgress
            return this
        }

        fun onSuccess(onSuccess: (String) -> Unit): RetrofitDownloadRequester {
            this.onSuccess = onSuccess
            return this
        }

        fun onCancel(onCancel: () -> Unit): RetrofitDownloadRequester {
            this.onCancel = onCancel
            return this
        }

        /**
         * @param onFailed 业务层返回true是代表业务层处理了该错误码，否则该错误码交给框架层处理
         */
        fun onFailed(onFailed: (Int, String) -> Boolean): RetrofitDownloadRequester {
            this.onFailed = onFailed
            return this
        }

        /**
         * 被观察者在io，观察者在主线程
         */
        private fun applyDefaultSchedulers(filePath: String): ObservableTransformer<ResponseBody, InputStream> {
            return ObservableTransformer { observable ->
                observable.subscribeOn(Schedulers.io())
                    .unsubscribeOn(Schedulers.io())
                    .map { response ->
                        response.byteStream()
                    }
                    .observeOn(Schedulers.computation()) // 用于计算任务
                    .doOnNext { inputStream -> writeFile(inputStream, filePath) }
                    .observeOn(AndroidSchedulers.mainThread())
            }
        }

        override fun request(): RetrofitDownloadRequester {
            // 添加拦截器
            realRequestOnLifecycle(
                callApi = api,
                composer = applyDefaultSchedulers(filePath),
                errorInterceptor = this@RetrofitDownloaderServiceCore.defaultConfig.apiErrorInterceptorNode,
                onStart = { disposable ->
                    this.mDisposable = WeakReference(disposable)
                    //添加请求
                    lifecycleObserver?.get()?.let { tag ->
                        getHttpRequesterManager().addRequester(tag, this@RetrofitDownloadRequester)
                    }
                    DownloadInterceptor.syncRequest(this)
                    onStart?.invoke()
                },
                onSuccess = { onSuccess?.invoke(filePath) },

                onComplete = {
                    notifyRemoveRequester()
                },
                onFailed = onFailed@{ code, message ->
                    onFailed?.invoke(code, message)
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
                lifecycleObserver?.get()?.let { observer ->
                    getHttpRequesterManager().removeRequester(observer, this)
                }
                onCancel?.invoke()
            }
        }

        /**
         * 通知移除requester缓存
         */
        private fun notifyRemoveRequester() {
            lifecycleObserver?.get()?.let { tag ->
                getHttpRequesterManager().removeRequester(tag, this@RetrofitDownloadRequester)
            }
        }


        fun getProgressListener() = onProgress

        private fun getDisposable() = mDisposable?.get()

        /**
         * 将输入流写入文件
         *
         * @param inputString
         * @param filePath
         */
        private fun writeFile(inputString: InputStream, filePath: String) {

            val file = File(filePath)
            if (file.exists()) {
                file.delete()
            }

            var fos: FileOutputStream? = null
            try {
                fos = FileOutputStream(file)

                val b = ByteArray(1024 * 8)

                var len = 0
                while (inputString.read(b).apply { len = this } != -1) {
                    fos.write(b, 0, len)
                }

            } catch (e: FileNotFoundException) {
                onFailed?.invoke(HttpErrorCode.FILE_NOT_FOUND_FAILED, e.message ?: "")
            } catch (e: IOException) {
                onFailed?.invoke(HttpErrorCode.FILE_WRITE_FAILED, e.message ?: "")
            } finally {
                fos.safeClose()
                inputString.safeClose()
            }

        }
    }

    object DownloadInterceptor : Interceptor {
        private val mLock = java.lang.Object()
        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request()
            request.url()
            var response = chain.proceed(request)

            synchronized(mLock) {
                val requester = this.requester
                if (requester != null) {

                    if (response.isSuccessful) {
                        response = response.newBuilder()
                            .body(
                                DownloadResponseBody(
                                    response.body(),
                                    requester.getProgressListener()
                                )
                            )
                            .build()
                    }
                    this.requester = null
                    mLock.notify()
                }

            }

            return response

        }


        private var requester: RetrofitDownloaderServiceCore<*>.RetrofitDownloadRequester? = null
        private val executor = Executors.newSingleThreadExecutor()
        fun <SERVICE> syncRequest(retrofitRequester: RetrofitDownloaderServiceCore<SERVICE>.RetrofitDownloadRequester) {
            executor.execute {
                synchronized(mLock) {
                    if (requester != null) {
                        mLock.wait()
                    }
                    requester = retrofitRequester
                    mLock.wait()
                }
            }
        }

    }

    class DownloadResponseBody(
        private val responseBody: ResponseBody?,
        private val onProgress: ((Float) -> Unit)?
    ) :
        ResponseBody() {

        // BufferedSource 是okio库中的输入流，这里就当作inputStream来使用。
        private var bufferedSource: BufferedSource? = null

        override fun contentLength(): Long = responseBody?.contentLength() ?: 0

        override fun contentType(): MediaType? = responseBody?.contentType()

        override fun source(): BufferedSource? {
            if (bufferedSource == null) {
                bufferedSource = Okio.buffer(source(responseBody?.source()!!))
            }
            return bufferedSource

        }

        private fun source(source: Source): Source {

            return object : ForwardingSource(source) {

                var totalBytesRead = 0L
                override fun read(sink: Buffer, byteCount: Long): Long {
                    val bytesRead = super.read(sink, byteCount)
                    totalBytesRead +=
                        if (bytesRead != -1L) {
                            bytesRead
                        } else {
                            0
                        }
                    val progress = totalBytesRead * 100F / contentLength()
                    if (bytesRead != -1L) {
                        onProgress?.invoke(progress)
                    }
                    return bytesRead
                }

            }
        }

    }

    /**
     * 生成retrofit
     */
    override fun generateDefaultRetrofit(): Retrofit {
        val config = defaultConfig

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

        okHttpBuilder.addInterceptor(DownloadInterceptor)


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
    private fun realRequestOnLifecycle(
        callApi: API.() -> Observable<ResponseBody>,
        composer: ObservableTransformer<ResponseBody, InputStream>,
        errorInterceptor: ApiErrorInterceptorNode? = null,
        onFailed: (Int, String) -> Boolean,
        onSuccess: (InputStream) -> Unit,
        onStart: (Disposable) -> Unit,
        onComplete: () -> Unit
    ) {
        request(
            defaultRetrofit,
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