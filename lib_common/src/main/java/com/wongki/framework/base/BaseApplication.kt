package com.wongki.framework.base

import android.app.Application

/**
 * @author  wangqi
 * date:    2019/6/19
 * email:   wangqi@feigeter.com
 * desc:    .
 */
open class BaseApplication : Application() {
    companion object {
        lateinit var instance: BaseApplication
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}