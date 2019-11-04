package com.wongki.framework.http.interceptor

/**
 * @author  wangqi
 * date:    2019/6/28
 * email:   wangqi7676@163.com
 * desc:    错误拦截器
 */
abstract class ErrorInterceptorNode : IErrorInterceptor {
    var next: ErrorInterceptorNode? = null
    var isLast = next == null
}