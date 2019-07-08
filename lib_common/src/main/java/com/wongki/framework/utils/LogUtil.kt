package com.wongki.framework.utils

import com.orhanobut.logger.Logger

/**
 * 日志工具类
 */
object LogUtil {

    fun d(msg: Any) {
        Logger.d(msg)
    }

    fun v(msg: String) {
        Logger.v(msg)
    }

    fun d(msg: String) {
        Logger.d(msg)
    }

    fun e(msg: String, vararg args: Any) {
        Logger.e(msg, args)
    }

    fun w(msg: String, vararg args: Any) {
        Logger.w(msg, args)
    }

    fun i(msg: String, vararg args: Any) {
        Logger.i(msg, args)
    }

    fun json(json: String?) {
        Logger.json(json)
    }
}