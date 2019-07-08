package com.wongki.framework.http.retrofit.core

import android.util.Log
import com.wongki.framework.http.base.IServiceCore
import com.wongki.framework.http.lifecycle.HttpLifecycle
import com.wongki.framework.http.lifecycle.IHttpLifecycleFactory
import com.wongki.framework.http.lifecycle.IHttpLifecycleOwner
import com.wongki.framework.http.retrofit.ErrorInterceptor
import com.wongki.framework.http.retrofit.IRetrofit
import com.wongki.framework.http.retrofit.lifecycle.HttpRetrofitLifecycleHelper
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType


/**
 * @author  wangqi
 * date:    2019/6/6
 * email:   wangqi7676@163.com
 *
 */
abstract class AbsRetrofitServiceCore<API> : IServiceCore, IRetrofit<API>, IHttpLifecycleOwner {

    override val mConnectTimeOut: Long = 15_000
    override val mReadTimeOut: Long = 15_000
    override val mWriteTimeOut: Long = 15_000
    private val mRetrofit by lazy { generateRetrofit() }
    protected open var errorInterceptor: ErrorInterceptor? = null

    /**
     * 默认的服务对象
     */
    protected val mDefaultApi: API by lazy {
        val paramType = this.javaClass.genericSuperclass as ParameterizedType
        val serviceClazz = paramType.actualTypeArguments[0] as Class<API>
        mRetrofit.newBuilder().build()
        mRetrofit.create(serviceClazz)
    }

    /**
     * 创建其他的服务对象
     */
    fun <T> createOther(serviceClazz: Class<T>): T {
        return mRetrofit.create(serviceClazz)
    }
    /**
     * 发起请求
     * @param request 默认的服务对象中具体的api请求方法
     * @param composer 线程调度
     */
    protected fun <RESPONSE,RESPONSE_MAP> request(
        request: (API) -> Observable<RESPONSE>,
        composer: ObservableTransformer<RESPONSE, RESPONSE_MAP>
    ): Observable<RESPONSE_MAP> {
        return request(mDefaultApi).compose(composer)
    }

    /**
     * 创建网络请求生命周期管理器
     */
    private object HttpRetrofitLifecycleFactory : IHttpLifecycleFactory {

        override fun createLifecycle(): HttpLifecycle {
            val lifecycle = HttpLifecycle()
            // 将生命周期管理器添加到缓存中
            HttpRetrofitLifecycleHelper.addLifecycle(lifecycle)
            return lifecycle
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

    /**
     * 生成retrofit
     */
    abstract fun generateRetrofit(): Retrofit

    protected fun getRetrofitOkHttpClient() = mRetrofit.callFactory() as OkHttpClient
//    protected fun addInterceptor(interceptor: Interceptor) = getRetrofitOkHttpClient().interceptors().add(interceptor)
//    protected fun removeInterceptor(interceptor: Interceptor) = getRetrofitOkHttpClient().interceptors().remove(interceptor)
    /**
     * 生命周期管理器
     */
    private var mLifecycle: HttpLifecycle? = null


    /**
     * 获取生命周期管理器
     */
    final override fun getLifecycle(): HttpLifecycle {
        if (mLifecycle == null) {
            synchronized(RetrofitServiceCore::class.java) {
                if (mLifecycle == null) {
                    val lifecycle = HttpRetrofitLifecycleFactory.createLifecycle()
                    mLifecycle = lifecycle
                }
            }
        }

        return mLifecycle!!
    }


    protected fun OkHttpClient.Builder.addCommonHeaders(map: MutableMap<String, String>) {
        if (map.isEmpty()) return
        addInterceptor { chain ->
            val requestBuilder = chain.request().newBuilder()
            for (entry in map) {
                requestBuilder.addHeader(entry.key, entry.value)
            }
            return@addInterceptor chain.proceed(requestBuilder.build())
        }
    }


    protected fun OkHttpClient.Builder.addCommonUrlParams(map: MutableMap<String, String>) {
        if (map.isEmpty()) return
        addInterceptor { chain ->
            val urlBuilder = chain.request().url().newBuilder()
            for (entry in map) {
                urlBuilder.addQueryParameter(entry.key, entry.value)
            }
            val requestBuilder = chain.request().newBuilder().url(urlBuilder.build())
            return@addInterceptor chain.proceed(requestBuilder.build())
        }
    }

}


