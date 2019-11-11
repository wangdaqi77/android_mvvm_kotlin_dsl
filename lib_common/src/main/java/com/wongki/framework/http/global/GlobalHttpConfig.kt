package com.wongki.framework.http.global

import com.wongki.framework.http.exception.ApiException
import com.wongki.framework.http.interceptor.IErrorInterceptor
import com.wongki.framework.http.listener.OnResponseFailedConvertListener

/**
 * @author  wangqi
 * date:    2019-11-11
 * email:   wangqi7676@163.com
 * 配置：
 * 1.response转换成对象
 * 2.全局的错误拦截
 */

@DslMarker
annotation class HttpConfigDslMarker

/**
 * 配置http
 */
@HttpConfigDslMarker
fun globalHttpConfig(init: GlobalHttpConfig.() -> Unit) {
    GlobalHttpConfig.init()
}

@HttpConfigDslMarker
object GlobalHttpConfig {
    private var inner = false // 内部标记
    var onResponseConvertFailedListener: OnResponseFailedConvertListener? = null
    var globalHttpErrorInterceptor: IErrorInterceptor? = null

    /**
     * 当转换失败时被触发
     * 在这里你需要把服务器的错误码转换成ApiException，如果没有有效的错误信息可以返回null
     * @param convert  1.response  2.mediaType
     */
    fun onConvertFailed(convert: (String, String) -> ApiException?) {
        inner = true
        onResponseConvertFailedListener = object : OnResponseFailedConvertListener {
            override fun onConvertFailed(response: String, mediaType: String): ApiException? {
                @Suppress("UNCHECKED_CAST")
                return convert.invoke(response, mediaType)
            }

        }
    }


    /**
     * 当请求失败时被触发
     * 当返回true表示当前拦截处理
     * @param onIntercept  1.code 2.message
     */
    fun onErrorIntercept(onIntercept: (Int, String) -> Boolean) {
        val globalHttpErrorInterceptor = object : IErrorInterceptor {
            override val tag: String = "全局的错误拦截器"
            override fun onInterceptError(code: Int, message: String): Boolean {
                return onIntercept.invoke(code, message)
            }
        }
        this.globalHttpErrorInterceptor = globalHttpErrorInterceptor
    }

}