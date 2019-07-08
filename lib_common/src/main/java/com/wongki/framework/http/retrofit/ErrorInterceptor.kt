package com.wongki.framework.http.retrofit

/**
 * @author  wangqi
 * date:    2019/6/28
 * email:   wangqi7676@163.com
 * desc:    错误拦截器
 */
abstract class ErrorInterceptor {
    var next: ErrorInterceptor? = null

    abstract fun onInterceptErrorCode(code: Int, message: String): Boolean
}