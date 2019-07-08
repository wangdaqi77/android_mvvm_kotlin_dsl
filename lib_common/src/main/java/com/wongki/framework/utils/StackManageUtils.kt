package com.wongki.framework.utils

import androidx.fragment.app.FragmentActivity

/**
 *  Created by yinzhengwei on 2018/7/31.
 *  @Function
 */
object StackManageUtils {

    private var cacheList = mutableListOf<FragmentActivity>()

    fun add(activity: FragmentActivity) {
        cacheList.add(activity)
    }

    fun remove(activity: FragmentActivity) {
        cacheList.remove(activity)
    }


    fun removeActivity(activity: FragmentActivity) {
        cacheList.forEachIndexed { index, it ->
            if (it::class.java == activity::class.java) {
                it.finish()
                //cacheList.removeAt(index)
                return@forEachIndexed
            }
        }
    }

    fun closeOtherOnlyActivity(activity: FragmentActivity) {
        var has = false
        cacheList.forEach {
            if (it::class.java != activity::class.java) {
                it.finish()
            } else has = true
        }
        if (has) {
            cacheList = mutableListOf()
            cacheList.add(activity)
        }
    }

    fun closeAll() {
        cacheList.forEachIndexed { index, fragmentActivity ->
            fragmentActivity.finish()
            //cacheList.removeAt(index)
            //android.os.Process.killProcess(android.os.Process.myPid())
        }
    }

}