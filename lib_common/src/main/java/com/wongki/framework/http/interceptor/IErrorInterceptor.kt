package com.wongki.framework.http.interceptor

import com.wongki.framework.http.retrofit.core.RetrofitServiceCore
import com.wongki.framework.http.global.GlobalHttpConfig

/**
 * @author  wangqi
 * date:    2019/6/28
 * email:   wangqi7676@163.com
 * desc:    错误拦截器
 */
interface IErrorInterceptor {
    val tag: String
    /**
     * 错误拦截处理的优先顺序依次是
     * 【
     * Api调用处的错误拦截器
     * [RetrofitServiceCore.RetrofitRequester.addErrorInterceptor]->
     * 服务内核的错误拦截器
     * [RetrofitServiceCore.selfServiceApiErrorInterceptor]->
     * 全局的错误拦截器
     * [GlobalHttpConfig.onErrorIntercept]
     * 】
     *
     * @return 返回true代表拦截处理，错误会终止继续传递
     */
    fun onInterceptError(code: Int, message: String): Boolean
}