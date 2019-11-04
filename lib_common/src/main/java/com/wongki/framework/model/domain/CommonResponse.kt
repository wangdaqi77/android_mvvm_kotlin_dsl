package com.wongki.framework.model.domain

/**
 * @author  wangqi
 * date:    2019/6/12
 * email:   wangqi7676@163.com
 * desc:    .
 */
class CommonResponse<T> {
    var code: Int = -1
    var message: String? = null
    var result: T? = null
    override fun toString(): String {
        return "code = $code" +
                "，message = $message" +
                "，result = $result"
    }
}