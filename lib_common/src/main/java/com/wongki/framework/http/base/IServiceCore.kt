package com.wongki.framework.http.base

/**
 * @author  wangqi
 * date:    2019/6/4
 * email:   wangqi@feigeter.com
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
    val mCommonRequestHeader: MutableMap<String, String>

    /**
     * 公共的请求参数-url
     */
    val mCommonUrlRequestParams: MutableMap<String, String>
//    /**
//     * 公共的请求参数-请求体
//     */
//    val mCommonPostRequestParams: MutableMap<String, String>

}