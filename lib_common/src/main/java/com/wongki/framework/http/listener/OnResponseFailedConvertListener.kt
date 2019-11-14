package com.wongki.framework.http.listener

import com.wongki.framework.http.HttpErrorCode
import com.wongki.framework.http.config.HttpConfigBuilder
import com.wongki.framework.http.exception.ApiException
import com.wongki.framework.http.gInner
import com.wongki.framework.http.getGlobalConfigErrorMessage
import com.wongki.framework.http.retrofit.core.RetrofitServiceCore

/**
 * @author  wangqi
 * date:    2019-11-11
 * email:   wangqi7676@163.com
 * desc:    api响应体转换失败的监听
 */
interface OnResponseFailedConvertListener {
    /**
     * 当转换失败时被触发
     * @param response 服务器返回的数据
     * @param mediaType 数据类型
     *
     * ex：定义result为User，实际返回{"code":200,"message":"成功!","result":""}
     * 那么此方法必定被执行，因为result为字符串被解析成User是不允许的。
     * @return
     *      1.当你能理解这个错误时需要返回[ApiException]，能不能理解的判定在于你是否可以在观察错误的函数体中正确的处理该错误code。
     *      2.当你能理解这个错误时返回null，当返回null时，你会在观察错误的函数体中接收到code:[HttpErrorCode.PARSE_FAILED]
     *
     * 注：观察错误的函数体
     * 1.[RetrofitServiceCore.RetrofitRequesterObserverBuilder.onFailed]
     * service{
     *      api{...}.thenCall{
     *          observe{
     *              onFailed{ code, message->
     *                  // 处理错误...
     *              }
     *          }
     *      }
     * }
     * 2.[HttpConfigBuilder.addApiErrorInterceptor2FirstNode]
     * config{
     *      addApiErrorInterceptor2FirstNode{
     *          // 处理错误...
     *      }
     * }
     *
     */
    fun onConvertFailed(response: String, mediaType: String): ApiException?

    companion object DEFAULT : OnResponseFailedConvertListener {
        override fun onConvertFailed(response: String, mediaType: String): ApiException? {
            gInner.logger?.log("DEFAULT.onConvertFailed-> response:$response, mediaType:$mediaType")
            return ApiException(-1, "onResponseConvertFailedListener".getGlobalConfigErrorMessage())
        }
    }
}
