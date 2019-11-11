package com.wongki.framework.model.domain

/**
 * @author  wangqi
 * date:    2019/6/12
 * email:   wangqi7676@163.com
 * desc:    .
 */
interface CommonResponse<T> {
    var code: Int
    var message: String?
    var data: T?
}