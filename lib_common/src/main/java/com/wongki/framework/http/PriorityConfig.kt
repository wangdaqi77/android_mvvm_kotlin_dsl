package com.wongki.framework.http

import com.wongki.framework.http.config.IHttpConfig
import com.wongki.framework.http.config.HttpConfigValueGetter
import com.wongki.framework.http.interceptor.ApiErrorInterceptorNode
import com.wongki.framework.http.listener.OnResponseFailedConvertListener
import com.wongki.framework.http.log.ILog
import com.wongki.framework.model.domain.CommonResponse

/**
 * @author  wangqi
 * date:    2019-11-12
 * email:   wangqi7676@163.com
 */
internal class PriorityConfig(initializer: () -> Array<IHttpConfig?>) : IHttpConfig() {
    override var tag: String = ""

    private val objArray = initializer.invoke()

    override var host: String?
            by HttpConfigValueGetter { objArray }
    override var successfulCode: Int?
            by HttpConfigValueGetter { objArray }
    override var responseClass: Class<out CommonResponse<*>>?
            by HttpConfigValueGetter { objArray }
    override var connectTimeOut: Long?
            by HttpConfigValueGetter { objArray }
    override var readTimeOut: Long?
            by HttpConfigValueGetter { objArray }
    override var writeTimeOut: Long?
            by HttpConfigValueGetter { objArray }
    override var logger: ILog?
            by HttpConfigValueGetter { objArray }
    override var onResponseConvertFailedListener: OnResponseFailedConvertListener?
            by HttpConfigValueGetter { objArray }
    override var apiErrorInterceptorNode: ApiErrorInterceptorNode?
            by HttpConfigValueGetter { objArray }
    override var commonHeader: MutableMap<String, String?>?
            by HttpConfigValueGetter { objArray }
    override var commonUrlQueryParams: MutableMap<String, String?>?
            by HttpConfigValueGetter { objArray }
    override var commonBodyParams: MutableMap<String, String?>?
            by HttpConfigValueGetter { objArray }
}