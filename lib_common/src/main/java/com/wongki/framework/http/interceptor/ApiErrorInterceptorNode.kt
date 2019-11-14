package com.wongki.framework.http.interceptor

/**
 * @author  wangqi
 * date:    2019/6/28
 * email:   wangqi7676@163.com
 * desc:    错误拦截器
 */
abstract class ApiErrorInterceptorNode : IApiErrorInterceptor {
    var tag: String = ""
    var next: ApiErrorInterceptorNode? = null


    companion object DEFAULT : ApiErrorInterceptorNode() {
        init {
            tag = "ApiErrorInterceptorNode.DEFAULT"
        }
        override fun onInterceptError(code: Int, message: String):Boolean{
            return false
        }
    }
}