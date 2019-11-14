package com.wongki.framework.http

import com.wongki.framework.http.config.HttpConfig
import com.wongki.framework.http.config.HttpConfigBuilder
import com.wongki.framework.http.config.HttpGlobalConfigBuilder
import com.wongki.framework.http.config.IHttpConfig
import com.wongki.framework.http.retrofit.core.AbsRetrofitServiceCore
import okhttp3.MediaType
import java.lang.ref.WeakReference


/**
 * @author  wangqi
 * date:    2019-11-11
 * email:   wangqi7676@163.com
 */
@DslMarker
annotation class HttpDsl

internal var gConfigFunction: (HttpGlobalConfigBuilder.() -> Unit)? = null
/**
 * 全局的配置
 *
 * 在Application进行构建
 *      httpGlobalConfig {
 *          ...
 *      }
 */
internal var gConfig: HttpConfig? = null
    get() {
        if (field == null) {
            throw IllegalArgumentException("未初始化，推荐在application配置httpGlobalConfig{}")
        }
        return field
    }

/**
 * 内部的配置
 *
 * 优先使用全局配置的value
 */
internal val gInner
    get() = PriorityConfig { arrayOf(gConfig, IHttpConfig.DEFAULT) }

/**
 * 内核
 */
internal var gCores: ArrayList<WeakReference<AbsRetrofitServiceCore<*>?>?>? = null

val CONTENTTYPE_JSON = MediaType.parse("application/json; charset=UTF-8").toString()

/**
 * 全局配置
 */
@HttpDsl
fun httpGlobalConfig(init: HttpGlobalConfigBuilder.() -> Unit) {
    if (gConfigFunction != null) throw IllegalArgumentException("只能配置一次 httpGlobalConfig {}")
    gConfigFunction = init
    notifyHttpConfigChange()
}

/**
 * 通知http配置发生变动
 */
fun notifyHttpConfigChange() {
    createGlobalConfig()
    gCores?.iterator()?.run {
        while (hasNext()) {
            next()?.get()?.onConfigChange()
        }
    }

}

@HttpDsl
internal fun newConfig(init: HttpConfigBuilder.() -> Unit): HttpConfig {
    val builder = HttpConfigBuilder()
    builder.init()
    return builder.build()
}

@HttpDsl
internal fun HttpConfig.config(init: HttpConfigBuilder.() -> Unit): HttpConfig {
    val builder = newBuilder()
    builder.init()
    return builder.build()
}


internal fun AbsRetrofitServiceCore<*>.cacheSelf() {
    if (gCores == null) {
        gCores = ArrayList()
    }
    gCores?.add(WeakReference(this))
}

private fun IHttpConfig.checkGlobalConfig() {
    check(successfulCode,"successfulCode")
    check(responseClass,"responseClass")
    check(onResponseConvertFailedListener,"onResponseConvertFailedListener")
}

private fun <T> IHttpConfig.check(t: T?, filedName: String): T {
    return t ?: throw IllegalArgumentException(filedName.getGlobalConfigErrorMessage()
    )
}

internal fun String.getGlobalConfigErrorMessage(): String {
  return  "未配置$this，请配置 httpGlobalConfig {$this ...}"
}

/**
 * 创建全局配置
 */
private fun createGlobalConfig() {
    val builder = HttpGlobalConfigBuilder()
    gConfigFunction?.invoke(builder)
    gConfig = builder.build()
    gConfig!!.checkGlobalConfig()
}