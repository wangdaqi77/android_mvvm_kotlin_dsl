package com.wongki.framework.http.lifecycle

/**
 * @author  wangqi
 * date:    2019/6/18
 * email:   wangqi@feigeter.com
 * desc:    .
 */
interface IHttpLifecycleOwner {
    fun getLifecycle(): HttpLifecycle
}