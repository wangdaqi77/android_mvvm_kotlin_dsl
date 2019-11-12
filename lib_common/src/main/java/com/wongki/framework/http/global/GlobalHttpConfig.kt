package com.wongki.framework.http.global

import com.wongki.framework.http.exception.ApiException
import com.wongki.framework.http.interceptor.IErrorInterceptor
import com.wongki.framework.http.listener.OnResponseFailedConvertListener
import com.wongki.framework.model.domain.CommonResponse

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
 * 全局配置http请求，可配置以下信息
 * [GlobalHttpConfig.CODE_API_SUCCESS] 与服务器约束的成功码（必须配置）
 * [GlobalHttpConfig.CODE_API_SUCCESS] 与服务器约束的成功码（必须配置）
 * [GlobalHttpConfig.onConvertFailed]  解析服务器的错误码（推荐配置，当框架解析失败时会触发）
 * [GlobalHttpConfig.onErrorIntercept] 请求失败的拦截器（推荐配置，例如登录失效的统一处理）
 *
 */
@HttpConfigDslMarker
fun globalHttpConfig(init: GlobalHttpConfig.() -> Unit) {
    GlobalHttpConfig.init()
}

@HttpConfigDslMarker
object GlobalHttpConfig {
    var onResponseConvertFailedListener: OnResponseFailedConvertListener? = null
    var globalHttpErrorInterceptor: IErrorInterceptor? = null
    var CODE_API_SUCCESS = 0 // 与服务器协商的成功码
    lateinit var RESPONSE_CLASS: Class<out CommonResponse<*>>

    /**
     * 当转换失败时被触发
     * 在这里你需要把服务器的错误码转换成ApiException，如果没有有效的错误信息可以返回null
     * @param convert  1.response  2.mediaType
     */
    fun onConvertFailed(convert: (String, String) -> ApiException?) {
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