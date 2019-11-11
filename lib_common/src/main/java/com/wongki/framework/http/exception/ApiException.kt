package com.wongki.framework.http.exception

/**
 * @author  wangqi
 * date:    2019/6/12
 * email:   wangqi7676@163.com
 * desc:
 * 请求的错误异常
 */
class ApiException(var code: Int, var msg: String?) : Exception(msg)