package com.wongki.framework.http.base

/**
 * @author  wangqi
 * date:    2019/6/6
 * email:   wangqi7676@163.com
 * desc:    兼容老版本定义的接口
 */
@Deprecated("过时")
interface IOld {
    /**
     * 默认的封装请求器
     */
    fun getDefaultRequester(): IOldRequester


    /**
     * 封装请求-默认
     */
    fun <T, R> request(t: T, onCallBack: (Boolean, Int, String, R?) -> Boolean) {
        request(getDefaultRequester(), t, onCallBack)
    }

    /**
     * 封装请求
     */
    fun <T, R> request(request: IOldRequester, t: T, onCallBack: (Boolean, Int, String, R?) -> Boolean) {
        request.requestReal(t, onCallBack)
    }

}