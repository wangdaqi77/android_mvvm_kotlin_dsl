package com.wongki.framework.http.listener

import com.wongki.framework.http.exception.ApiException

/**
 * @author  wangqi
 * date:    2019-11-11
 * email:   wangqi7676@163.com
 * desc:    网络请求响应转换失败的监听
 */
interface OnResponseFailedConvertListener {
    /**
     * @param response 服务器返回的数据
     * @param mediaType 数据类型
     */
    fun onConvertFailed(
        response: String,
        mediaType: String
    ): ApiException?
}