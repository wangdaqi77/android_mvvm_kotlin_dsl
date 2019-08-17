package com.wongki.framework.base

import androidx.multidex.MultiDexApplication

/**
 * @author  wangqi
 * date:    2019/6/19
 * email:   wangqi7676@163.com
 * desc:    .
 */
open class BaseApplication : MultiDexApplication() {
    companion object {
        lateinit var instance: BaseApplication
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}