package com.wongki.framework.http.lifecycle

/**
 * @author  wangqi
 * date:    2019/6/17
 * email:   wangqi7676@163.com
 * desc:    生命周期观察者
 */
interface IHttpLifecycleObserver{
    /**
     * 应该做一些取消网络请求的操作
     */
    fun onDestroy()
}