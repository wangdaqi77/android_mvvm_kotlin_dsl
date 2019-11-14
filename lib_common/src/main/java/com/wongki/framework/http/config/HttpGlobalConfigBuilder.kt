package com.wongki.framework.http.config

import com.wongki.framework.http.HttpDsl
import com.wongki.framework.http.exception.ApiException
import com.wongki.framework.http.listener.OnResponseFailedConvertListener
import com.wongki.framework.http.retrofit.core.RetrofitServiceCore
import com.wongki.framework.model.domain.CommonResponse
import com.wongki.framework.http.config.HttpConfigBuilder as HttpConfigBuilder

/**
 * @author  wangqi
 * date:    2019-11-12
 * email:   wangqi7676@163.com
 * desc:    .
 */
@HttpDsl
class HttpGlobalConfigBuilder : HttpConfigBuilder {

    constructor(config: HttpGlobalConfig? = null) : super(config) {
        config ?: return
        this.successfulCode = config.successfulCode
        this.responseClass = config.responseClass
        this.onResponseConvertFailedListener = config.onResponseConvertFailedListener
    }
    @HttpDsl
    var successfulCode: Int? = null
    @HttpDsl
    var responseClass: Class<out CommonResponse<*>>? = null
    @HttpDsl
    internal var onResponseConvertFailedListener: OnResponseFailedConvertListener? = null

    /**
     * 响应体结构转换失败时被触发
     * @param convert  1.response(服务器返回的数据)  2.mediaType(数据类型)
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
    @HttpDsl
    fun onResponseConvertFailed(convert: (String, String) -> ApiException?) {
        onResponseConvertFailedListener =
            object : OnResponseFailedConvertListener {
                override fun onConvertFailed(response: String, mediaType: String): ApiException? {
                    @Suppress("UNCHECKED_CAST")
                    return convert.invoke(response, mediaType)
                }

            }
    }

    override fun build(): HttpConfig = HttpGlobalConfig(this)
}