package com.wongki.framework.http.lifecycle

/**
 * @author  wangqi
 * date:    2019/6/18
 * email:   wangqi7676@163.com
 * desc:    .
 */
interface IHttpRequestManagerFactory {
    fun createHttpRequesterManager(): HttpRequesterManager
}