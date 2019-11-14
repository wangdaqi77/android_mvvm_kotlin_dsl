package com.wongki.framework.http.config

import com.wongki.framework.http.HttpDsl
import com.wongki.framework.http.interceptor.ApiErrorInterceptorNode
import com.wongki.framework.http.listener.OnResponseFailedConvertListener
import com.wongki.framework.http.log.ILog
import com.wongki.framework.model.domain.CommonResponse

/**
 * @author  wangqi
 * date:    2019-11-12
 * email:   wangqi7676@163.com
 * desc:    .
 */


/**
 * 配置读取的先后顺序
 * 单次请求->service->global
 */
@HttpDsl
abstract class IHttpConfig {
    internal open var tag: String = "请设置tag"
    // 域名
    open var host: String? = null
    // 与服务器协商的成功码  只能全局设置
    open var successfulCode: Int? = null
    // 响应的class 只能全局设置
    open var responseClass: Class<out CommonResponse<*>>? = null
    // 连接超时
    open var connectTimeOut: Long? = null
    // 读取超时
    open var readTimeOut: Long? = null
    // 写入超时
    open var writeTimeOut: Long? = null
    // 打印
    open var logger: ILog? = null
    // 当框架层解析失败  只能全局设置
    open var onResponseConvertFailedListener: OnResponseFailedConvertListener? = null
    /**
     * api错误处理拦截器
     * 基于config构建新的config时，新的拦截器节点放在链表头部
     * [HttpConfigBuilder.addApiErrorInterceptor2FirstNode]
     */
    open var apiErrorInterceptorNode: ApiErrorInterceptorNode? = null

    // 公共的header
    open val commonHeader: MutableMap<String, String?>? = null

    // 公共的query param  (https://xx.com?name=哈哈&version=1)
    open val commonUrlQueryParams: MutableMap<String, String?>? = null

    // 公共的body param
    @Deprecated("暂不支持")
    open val commonBodyParams: MutableMap<String, String?>? = null


    companion object DEFAULT : IHttpConfig() {
        override var tag: String = "默认的配置"
        override var host: String? = null
        override var successfulCode: Int? = 0
        override var responseClass: Class<out CommonResponse<*>>? = null
        override var connectTimeOut: Long? = null
        override var readTimeOut: Long? = null
        override var writeTimeOut: Long? = null
        override var logger: ILog? = ILog.DEFAULT
        override var onResponseConvertFailedListener: OnResponseFailedConvertListener? =
            OnResponseFailedConvertListener.DEFAULT
        override var apiErrorInterceptorNode: ApiErrorInterceptorNode? = ApiErrorInterceptorNode.DEFAULT
        override var commonHeader: MutableMap<String, String?>? = null
        override var commonUrlQueryParams: MutableMap<String, String?>? = null
        override var commonBodyParams: MutableMap<String, String?>? = null
    }
}