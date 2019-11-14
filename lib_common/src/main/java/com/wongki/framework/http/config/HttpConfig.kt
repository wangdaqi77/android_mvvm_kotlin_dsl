package com.wongki.framework.http.config

import com.wongki.framework.http.HttpDsl

/**
 * @author  wangqi
 * date:    2019-11-12
 * email:   wangqi7676@163.com
 * desc:    .
 */
@HttpDsl
open class HttpConfig(builder: HttpConfigBuilder) : IHttpConfig() {
    internal var addHeaderFunction: (() -> MutableMap<String, String?>)? = null
    internal var addUrlQueryParamsFunction: (() -> MutableMap<String, String?>)? = null
    internal var addBodyParamsFunction: (() -> MutableMap<String, String?>)? = null

    init {
        this.tag = builder.tag
        this.host = builder.host
        this.connectTimeOut = builder.connectTimeOut
        this.readTimeOut = builder.readTimeOut
        this.writeTimeOut = builder.writeTimeOut
        this.logger = builder.logger
        this.apiErrorInterceptorNode = builder.apiErrorInterceptorNode
        this.addHeaderFunction = builder.addHeaderFunction
        this.addUrlQueryParamsFunction = builder.addUrlQueryParamsFunction

        if (builder is HttpGlobalConfigBuilder) {
            this.successfulCode = builder.successfulCode
            this.responseClass = builder.responseClass
            this.onResponseConvertFailedListener =
                builder.onResponseConvertFailedListener
        }
    }

    override val commonHeader: MutableMap<String, String?>?
        get() = addHeaderFunction?.invoke()
    override val commonUrlQueryParams: MutableMap<String, String?>?
        get() = addUrlQueryParamsFunction?.invoke()
    override val commonBodyParams: MutableMap<String, String?>?
        get() = addBodyParamsFunction?.invoke()


    open fun newBuilder(): HttpConfigBuilder {
        return HttpConfigBuilder(this)
    }
}