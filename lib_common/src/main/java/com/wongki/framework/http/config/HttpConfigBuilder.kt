package com.wongki.framework.http.config

import com.wongki.framework.http.HttpDsl
import com.wongki.framework.http.interceptor.ApiErrorInterceptorNode
import com.wongki.framework.http.log.ILog

/**
 * @author  wangqi
 * date:    2019-11-12
 * email:   wangqi7676@163.com
 * desc:    .
 */
@HttpDsl
open class HttpConfigBuilder {
    constructor(config: HttpConfig? = null) {
        config ?: return
        this.tag = config.tag // 覆盖
        this.host = config.host // 覆盖
        this.connectTimeOut = config.connectTimeOut // 覆盖
        this.readTimeOut = config.readTimeOut // 覆盖
        this.writeTimeOut = config.writeTimeOut // 覆盖
        this.logger = config.logger // 覆盖
        this.apiErrorInterceptorNode = config.apiErrorInterceptorNode // 链表，追加放到第一位
        this.addHeaderFunction = config.addHeaderFunction // 追加
        this.addUrlQueryParamsFunction = config.addUrlQueryParamsFunction // 追加
    }

    // tag
    @HttpDsl
    var tag: String = ""
    // 域名
    @HttpDsl
    var host: String? = null
    // 连接超时时间
    @HttpDsl
    var connectTimeOut: Long? = null
    // 读取超时时间
    @HttpDsl
    var readTimeOut: Long? = null
    // 写入超时时间
    @HttpDsl
    var writeTimeOut: Long? = null
    // log
    @HttpDsl
    internal var logger: ILog? = null
    // api请求错误拦截器
    @HttpDsl
    internal var apiErrorInterceptorNode: ApiErrorInterceptorNode? = null
    // 添加header
    @HttpDsl
    internal var addHeaderFunction: (() -> MutableMap<String, String?>)? = null
    // 添加url参数
    @HttpDsl
    internal var addUrlQueryParamsFunction: (() -> MutableMap<String, String?>)? = null

    /**
     * log
     */
    @HttpDsl
    fun log(init: (String) -> Unit) {
        this.logger = object : ILog {
            override fun log(message: String) {
                init.invoke(message)
            }
        }
    }

    /**
     * api请求错误拦截
     * 当请求失败时被触发
     * @param onIntercept  1.code 2.message
     * 返回true表示当前拦截处理
     */
    @HttpDsl
    fun addApiErrorInterceptor2FirstNode(onIntercept: (Int, String) -> Boolean) {
        val interceptorNode = object : ApiErrorInterceptorNode() {

            override fun onInterceptError(code: Int, message: String): Boolean {
                return onIntercept.invoke(code, message)
            }
        }

        interceptorNode.tag = tag
        if (apiErrorInterceptorNode != null) {
            interceptorNode.next = this.apiErrorInterceptorNode
        }
        this.apiErrorInterceptorNode = interceptorNode
    }


    /**
     * 公共的请求头
     * 发起请求时被触发
     */
    @HttpDsl
    fun addHeaders(init: () -> MutableMap<String, String?>) {
        val function = this.addHeaderFunction
        this.addHeaderFunction =
            if (function != null) {
                {
                    mutableMapOf<String, String?>().apply {
                        putAll(function())
                        putAll(init())
                    }
                }

            } else {
                init
            }
    }

    /**
     * 公共的url query参数（追加）
     * 发起请求时被触发
     */
    @HttpDsl
    fun addUrlQueryParams(init: () -> MutableMap<String, String?>) {
        val function = this.addUrlQueryParamsFunction
        this.addUrlQueryParamsFunction =
            if (function != null) {
                {
                    mutableMapOf<String, String?>().apply {
                        putAll(function())
                        putAll(init())
                    }
                }

            } else {
                init
            }
    }

    open fun build(): HttpConfig = HttpConfig(this)
}