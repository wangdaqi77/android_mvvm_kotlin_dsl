package com.wongki.framework.http.base

/**
 * @author  wangqi
 * date:    2019/6/20
 * email:   wangqi7676@163.com
 * desc:    请求器
 */
interface IRequester {
    /**
     * 发送网络请求
     */
    fun request(): IRequester

    /**
     * 是否取消
     */
    fun isCancel(): Boolean

    /**
     * 主动触发取消！
     */
    fun cancel()
}