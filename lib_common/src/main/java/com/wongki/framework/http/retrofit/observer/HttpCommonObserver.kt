package com.wongki.framework.http.retrofit.observer

import com.wongki.framework.extensions.toast
import com.wongki.framework.http.exception.ApiException
import com.wongki.framework.http.HttpErrorCode
import com.wongki.framework.http.interceptor.IApiErrorInterceptor
import com.wongki.framework.http.exception.ParseResponseException
import com.wongki.framework.http.gInner
import com.wongki.framework.http.interceptor.ApiErrorInterceptorNode
import io.reactivex.Observer
import org.json.JSONException
import retrofit2.HttpException
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.text.ParseException

abstract class HttpCommonObserver<T>(
    private val errorInterceptor: ApiErrorInterceptorNode? = null,
    private val onFailed: (Int, String) -> Boolean,
    private val onSuccess: (T) -> Unit
) : Observer<T> {

    private val TAG = "HttpCommonObserver"
    override fun onNext(t: T) {
        onSuccess(t)
    }

    override fun onError(e: Throwable) {
        val wrapError: Pair<Int, String> = parseAndWrapError(e)
        val code: Int = wrapError.first
        val msg: String = wrapError.second
        gInner.logger?.log("api请求错误， code：$code, message：$msg")

        /**
         * 是否已处理该错误
         */
        var isProcessed = false
        /**
         *  处理错误拦截
         * [IApiErrorInterceptor.onInterceptError]
         */
        var errorInterceptor = this.errorInterceptor
        var times = 0
        while (errorInterceptor != null && !isProcessed) {
            isProcessed = errorInterceptor.onInterceptError(code, msg)
            gInner.logger?.log("\nTAG-${errorInterceptor.tag}-> code:$code,第${++times}次拦截状态：$isProcessed \n info:$msg")
            errorInterceptor = errorInterceptor.next
        }

        // 没有拦截
        if (!isProcessed) {
            // 业务层处理
            isProcessed = onFailed(code, msg)
            gInner.logger?.log("onFailed（请求的位置）， code：$code, message：$msg 处理状态：$isProcessed")
        }

        if (!isProcessed) {
            // 娄底处理
            "$msg ($code)".toast()
            gInner.logger?.log("api请求错误娄底处理， code：$code, message：$msg 处理状态：$isProcessed")

        }

    }

    /**
     * 解析包装错误信息
     */
    private fun parseAndWrapError(e: Throwable): Pair<Int, String> {
        val code: Int
        val msg: String
        val message = "${e.message}"
        when (e) {
            is ApiException -> {
                code = e.code
                msg = e.msg ?: ""
            }

            is IOException,  // 请求失败
            is HttpException // 非成功 !(code >= 200 && code < 300)
            -> {
                code = HttpErrorCode.REQUEST_FAILED
                msg = message
            }
            // 未知host
            is UnknownHostException -> {
                code = HttpErrorCode.HOST_UNKNOWN_FAILED
                msg = message
            }
            // 连接服务器超时,读写超时
            is SocketTimeoutException -> {
                code = HttpErrorCode.TIMEOUT_FAILED
                msg = message
            }
            // 网路连接失败
            is ConnectException -> {
                code = HttpErrorCode.CONNECTION_FAILED
                msg = message
            }
            // 解析异常
            is JSONException,
            is ParseResponseException,
            is ParseException
            -> {
                code = HttpErrorCode.PARSE_FAILED
                msg = message
            }
            // 未知错误
            else -> {
                code = HttpErrorCode.UNKNOWN_FAILED
                msg = message
            }
        }

        return code to msg

    }


}