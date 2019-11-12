package com.wongki.framework.http.interceptor

/**
 * @author  wangqi
 * date:    2019/6/28
 * email:   wangqi7676@163.com
 * desc:    全局的错误拦截器
 */
object GlobalHttpErrorInterceptor : IErrorInterceptor {
    override val tag: String = "全局的错误拦截器"
    private var onIntercept: ((Int, String) -> Boolean)? = null

    fun onIntercept(onIntercept: (Int, String) -> Boolean) {
        this.onIntercept = onIntercept
    }

    override fun onInterceptError(code: Int, message: String): Boolean {
        return onIntercept?.invoke(code, message) ?: false
    }
}