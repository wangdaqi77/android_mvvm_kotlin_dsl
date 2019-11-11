package com.wongki.framework.model.domain

import com.google.gson.annotations.SerializedName

/**
 * @author  wangqi
 * date:    2019-11-11
 * email:   wangqi7676@163.com
 * desc:    .
 */
class MyResponse<T> : CommonResponse<T> {

    @SerializedName("code")
    override var code: Int = -1
    @SerializedName("message")
    override var message: String? = null
    @SerializedName("result")
    override var data: T? = null

    override fun toString(): String {
        return "code = $code" +
                "，message = $message" +
                "，result = $data"
    }
}