package com.wongki.framework


/**
 * @author  wangqi
 * date:    2019-11-15
 * email:   wangqi7676@163.com
 * desc:    .
 */

open class EventObserverBuilder<RESPONSE_DATA> {
    open var onStart: (() -> Unit)? = null
    open var onFailed: ((Int, String) -> Boolean)? = null
    open var onCancel: (() -> Unit)? = null
    open var onSuccess: (RESPONSE_DATA?.() -> Unit)? = null

    /**
     * 当开始发起请求
     */
    open fun onStart(onStart: () -> Unit) {
        this.onStart = onStart
    }

    /**
     * 当取消请求时
     */
    open fun onCancel(onCancel: () -> Unit) {
        this.onCancel = onCancel
    }

    /**
     * 当成功时
     */
    open fun onSuccess(onSuccess: RESPONSE_DATA?.() -> Unit) {
        this.onSuccess = onSuccess
    }

    /**
     * 当失败时
     */
    open fun onFailed(onFailed: (Int, String) -> Boolean) {
        this.onFailed = onFailed
    }


}