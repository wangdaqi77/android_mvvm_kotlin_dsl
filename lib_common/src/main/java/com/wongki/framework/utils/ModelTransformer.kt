package com.wongki.framework.utils

import org.json.JSONObject

/**
 * @author  wangqi
 * date:    2019-10-29
 * email:   wangqi7676@163.com
 * desc:    .
 */

fun <T> String.transform(clazz: Class<T>, onTransform: JSONObject.(T) ->Unit): T {
    return JSONObject(this).transform(clazz, onTransform)
}

fun <T> JSONObject.transform(clazz: Class<T>, onTransform: JSONObject.(T) -> Unit): T {
    return transform(clazz.newInstance(), onTransform)
}

inline fun <reified T> JSONObject.transform(noinline onTransform: JSONObject.(T) -> Unit): T {
    val clazz = T::class.java
    val newInstance = clazz.newInstance()
    return transform(newInstance, onTransform)
}

fun <T> JSONObject.transform(instance: T, onTransform: JSONObject.(T) -> Unit): T {
    onTransform(instance)
    return instance
}