package com.wongki.framework.http.interceptor


/**
 * @author  wangqi
 * date:    2019/6/28
 * email:   wangqi7676@163.com
 * desc:    错误拦截器
 */
interface IApiErrorInterceptor {
    /**
     *
     * @return 返回true代表拦截处理，错误会终止继续传递
     */
    fun onInterceptError(code: Int, message: String): Boolean

    companion object DEFAULT : IApiErrorInterceptor {
        override fun onInterceptError(code: Int, message: String):Boolean{
            return false
        }
    }

}