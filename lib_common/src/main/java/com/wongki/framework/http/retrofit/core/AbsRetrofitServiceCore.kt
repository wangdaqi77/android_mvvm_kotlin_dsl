package com.wongki.framework.http.retrofit.core

import com.wongki.framework.http.*
import com.wongki.framework.http.cacheSelf
import com.wongki.framework.http.config
import com.wongki.framework.http.config.HttpConfig
import com.wongki.framework.http.config.HttpConfigBuilder
import com.wongki.framework.http.config.IHttpConfig
import com.wongki.framework.http.gConfig
import com.wongki.framework.http.gInner
import com.wongki.framework.http.lifecycle.HttpLifecycleManager
import com.wongki.framework.http.lifecycle.IHttpLifecycleManagerFactory
import com.wongki.framework.http.lifecycle.IHttpRequesterManagerOwner
import com.wongki.framework.http.interceptor.ApiErrorInterceptorNode
import com.wongki.framework.http.newConfig
import com.wongki.framework.http.retrofit.IRetrofit
import com.wongki.framework.http.retrofit.lifecycle.HttpRequesterManagerHelper
import com.wongki.framework.http.retrofit.observer.HttpCommonObserver
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.disposables.Disposable
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType


/**
 * @author  wangqi
 * date:    2019/6/6
 * email:   wangqi7676@163.com
 *
 * config 发生变动之后，
 */
abstract class AbsRetrofitServiceCore<SERVICE> : IRetrofit<SERVICE>, IHttpRequesterManagerOwner {
    init {
        requireNotNull(gConfig) { "未配置http全局配置，推荐在你的Application.onCreate()进行初始化\"httpGlobal{ ... }\"" }
        updateConfigAndRetrofit()
        cacheSelf()
    }

    protected lateinit var defaultConfig: HttpConfig
    protected lateinit var defaultRetrofit: Retrofit
    private var lifecycleManager: HttpLifecycleManager? = null

    /**
     * 配置变动
     */
    fun onConfigChange() {
        gInner.logger?.log("配置发生变动，onConfigChange（）")
        updateConfigAndRetrofit()
    }

    private fun updateConfigAndRetrofit() {
        defaultConfig = this.generateConfig()
        defaultRetrofit = this.generateDefaultRetrofit()
    }

    /**
     * 生成默认的配置
     */
    abstract fun generateConfig(): HttpConfig

    /**
     * 生成默认的retrofit
     */
    abstract fun generateDefaultRetrofit(): Retrofit

    /**
     * 发起请求
     * @param callApi 默认的服务对象中具体的api请求方法
     * @param composer 线程调度
     */
    protected fun <RESPONSE, RESPONSE_MAP> request(
        retrofit: Retrofit,
        callApi: SERVICE.() -> Observable<RESPONSE>,
        composer: ObservableTransformer<RESPONSE, RESPONSE_MAP>,
        errorInterceptor: ApiErrorInterceptorNode? = null,
        onFailed: (Int, String) -> Boolean,
        onSuccess: (RESPONSE_MAP) -> Unit,
        onStart: (Disposable) -> Unit,
        onComplete: () -> Unit
    ) {
        return retrofit
            .createServiceProxy()
            .callApi()
            .compose(composer)
            .subscribe(object :
                HttpCommonObserver<RESPONSE_MAP>(errorInterceptor, onFailed, onSuccess) {

                override fun onComplete() {
                    onComplete()
                }

                override fun onSubscribe(d: Disposable) {
                    onStart(d)
                }

            })
    }

    /**
     * 生成独立的配置
     */
    @HttpDsl
    protected fun newConfig(init: HttpConfigBuilder.() -> Unit) =
        com.wongki.framework.http.newConfig(init)

    /**
     * 在全局配置的基础上配置
     */
    @HttpDsl
    protected fun config(init: HttpConfigBuilder.() -> Unit) =
        gConfig!!.config(init)

    /**
     * 创建网络请求生命周期管理器
     */
    private object HttpLifecycleFactory : IHttpLifecycleManagerFactory {
        override fun createHttpLifecycleManager(): HttpLifecycleManager {
            val httpRequesterManager = HttpLifecycleManager()
            // 将生命周期管理器添加到缓存中
            HttpRequesterManagerHelper.addRequesterManager(httpRequesterManager)
            return httpRequesterManager
        }
    }

    /**
     * 获取生命周期管理器
     */
    final override fun getHttpRequesterManager(): HttpLifecycleManager {
        if (lifecycleManager == null) {
            synchronized(RetrofitServiceCore::class.java) {
                if (lifecycleManager == null) {
                    val lifecycle = HttpLifecycleFactory.createHttpLifecycleManager()
                    lifecycleManager = lifecycle
                }
            }
        }

        return lifecycleManager!!
    }

    private fun Retrofit.createServiceProxy(): SERVICE {
        val javaClass = this@AbsRetrofitServiceCore.javaClass
        val genericSuperclass = javaClass.genericSuperclass
        val paramType = genericSuperclass as ParameterizedType
        val serviceClazz = paramType.actualTypeArguments[0] as Class<*>
        @Suppress("UNCHECKED_CAST")
        return create(serviceClazz) as SERVICE
    }

    protected fun addCommonHeaders(
        okHttp: OkHttpClient.Builder,
        params: MutableMap<String, String?>?
    ) {
        if (params.isNullOrEmpty()) return
        okHttp.addInterceptor { chain ->
            val builder = chain.request().newBuilder()
            for (entry in params) {
                val key = entry.key
                val value = entry.value
                if (value != null) {
                    builder.addHeader(key, value)
                }
            }
            return@addInterceptor chain.proceed(builder.build())
        }
    }


    protected fun addCommonUrlParams(
        okHttp: OkHttpClient.Builder,
        params: MutableMap<String, String?>?
    ) {
        if (params.isNullOrEmpty()) return
        okHttp.addInterceptor { chain ->
            val builder = chain.request().url().newBuilder()
            for (entry in params) {
                builder.addQueryParameter(entry.key, entry.value)
            }
            val requestBuilder = chain.request().newBuilder().url(builder.build())
            return@addInterceptor chain.proceed(requestBuilder.build())
        }
    }

    protected fun <T> IHttpConfig.check(t: T?, filedName: String): T {
        return t ?: throw IllegalArgumentException(
            "未配置$filedName，请重写defaultConfig：\n" +
                    "override var defaultConfig = config { $filedName \"...\" }"
        )
    }

}



