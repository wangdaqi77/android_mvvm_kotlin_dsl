package com.wongki.framework.http.retrofit.observer


import android.util.Log
import com.wongki.framework.extensions.toast
import com.wongki.framework.http.exception.ApiException
import com.wongki.framework.http.HttpCode
import com.wongki.framework.http.interceptor.IErrorInterceptor
import com.wongki.framework.http.exception.ParseResponseException
import com.wongki.framework.http.global.GlobalHttpConfig
import com.wongki.framework.http.interceptor.ErrorInterceptorNode
import io.reactivex.Observer
import org.json.JSONException
import retrofit2.HttpException
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.text.ParseException


abstract class HttpCommonObserver<T>(
    private val errorInterceptor: ErrorInterceptorNode? = null,
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

        /**
         * 是否已处理该错误
         */
        var isProcessed = false
        /**
         *  处理错误拦截
         * [IErrorInterceptor.onInterceptError]
         */
        var errorInterceptor = this.errorInterceptor
        while (errorInterceptor != null && !isProcessed) {
            isProcessed = errorInterceptor.onInterceptError(code, msg)
            Log.e(TAG,"${errorInterceptor.tag} code：$code, message：$msg 拦截状态：$isProcessed")
            errorInterceptor = errorInterceptor.next
        }

        val globalHttpErrorInterceptor = GlobalHttpConfig.globalHttpErrorInterceptor
        if (!isProcessed&&globalHttpErrorInterceptor != null) {
            // 交给全局处理
            isProcessed = globalHttpErrorInterceptor.onInterceptError(code, msg)
            Log.e(TAG,"${globalHttpErrorInterceptor.tag} code：$code, message：$msg 拦截状态：$isProcessed")
        }

        // 没有拦截
        if (!isProcessed) {
            // 业务层处理
            isProcessed = onFailed(code, msg)
            Log.e(TAG,"api请求错误处理， code：$code, message：$msg 处理状态：$isProcessed")
        }

        if (!isProcessed) {
            // 娄底处理
            "$msg ($code)".toast()
            Log.e(TAG,"api请求错误娄底处理， code：$code, message：$msg 处理状态：$isProcessed")

        }

    }

    /**
     * 解析包装错误信息
     */
    private fun parseAndWrapError(e: Throwable): Pair<Int, String> {
        val code: Int
        val msg: String
        val message = e::class.java.name + ": ${e.message}"
        when (e) {
            is ApiException -> {
                code = e.code
                msg = e.msg ?: ""
            }

            is IOException,  // 请求失败
            is HttpException // 非成功 !(code >= 200 && code < 300)
            -> {
                code = HttpCode.REQUEST_FAILED
                msg = message
            }
            // 未知host
            is UnknownHostException -> {
                code = HttpCode.HOST_UNKNOWN_FAILED
                msg = message
            }
            // 连接服务器超时,读写超时
            is SocketTimeoutException -> {
                code = HttpCode.TIMEOUT_FAILED
                msg = message
            }
            // 网路连接失败
            is ConnectException -> {
                code = HttpCode.CONNECTION_FAILED
                msg = message
            }
            // 解析异常
            is JSONException,
            is ParseResponseException,
            is ParseException
            -> {
                code = HttpCode.PARSE_FAILED
                msg = message
            }
            // 未知错误
            else -> {
                code = HttpCode.UNKNOWN_FAILED
                msg = message
            }
        }

        return code to msg

    }


}