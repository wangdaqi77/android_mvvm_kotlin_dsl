package com.wongki.framework.http.retrofit.lifecycle

import com.wongki.framework.http.lifecycle.HttpRequesterManager

/**
 * @author  wangqi
 * date:    2019/6/20
 * email:   wangqi7676@163.com
 * desc:    一个缓存、遍历所有的生命周期管理器的帮助类
 */
object HttpRequesterManagerHelper {
    private val mLifecycle by lazy { ArrayList<HttpRequesterManager>() }
    /**
     * 添加http生命周期管理器
     */
    fun addRequesterManager(lifecycle: HttpRequesterManager) {
        mLifecycle.add(lifecycle)
    }

    /**
     * 遍历http生命周期管理器
     */
    fun forEachLifecycle(forOnEach: (HttpRequesterManager) -> Unit) {
        mLifecycle.forEach(forOnEach)
    }
}