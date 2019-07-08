package com.wongki.framework.model.domain

/**
 * @author  wangqi
 * date:    2019/6/12
 * email:   wangqi@feigeter.com
 * desc:    .
 */
data class CommonResponse<T>(
        var code: Int = -1,
        var count: Int = -1,
        var message: String?,
        var result: T?
)