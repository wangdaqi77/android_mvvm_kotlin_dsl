package com.wongki.framework.http.config

import com.wongki.framework.http.HttpDsl

/**
 * @author  wangqi
 * date:    2019-11-13
 * email:   wangqi7676@163.com
 * desc:    .
 */
@HttpDsl
class HttpGlobalConfig(builder: HttpGlobalConfigBuilder):HttpConfig(builder) {
    override fun newBuilder(): HttpConfigBuilder {
        return HttpGlobalConfigBuilder(this)
    }
}