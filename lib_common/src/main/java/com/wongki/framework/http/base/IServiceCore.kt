package com.wongki.framework.http.base

/**
 * @author  wangqi
 * date:    2019/6/4
 * email:   wangqi7676@163.com
 * desc:
 */
interface IServiceCore {
    /**
     * host
     */
    val mHost: String

    /**
     * 读取超时时间
     */
    val mConnectTimeOut: Long
    /**
     * 读取超时时间
     */
    val mReadTimeOut: Long

    /**
     * 写入超时时间
     */
    val mWriteTimeOut: Long

    /**
     * 公共的请求头
     */
    fun getCommonRequestHeader(): MutableMap<String, String>

    /**
     * 公共的请求参数-url
     */
    fun getCommonUrlRequestParams(): MutableMap<String, String>

}