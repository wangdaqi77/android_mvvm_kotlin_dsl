package com.wongki.framework.http.base

/**
 * @author  wangqi
 * date:    2019/6/6
 * email:   wangqi7676@163.com
 * desc:    .
 */
@Deprecated("过时")
interface IOldRequester {

    /**
     * 请求数据
     * @param onCallBack 第一个参数：是否请求成功  第二个参数：状态码  第三个参数：状态信息 第四个参数：返回数据
     */
    fun <T, R> requestReal(t: T, onCallBack: (Boolean, Int, String, R?) -> Boolean)
}