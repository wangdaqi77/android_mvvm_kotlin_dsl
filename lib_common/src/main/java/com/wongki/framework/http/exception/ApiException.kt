package com.wongki.framework.http.exception

/**
 * @author  wangqi
 * date:    2019/6/12
 * email:   wangqi@feigeter.com
 * desc:    .
 */
class ApiException(var code: Int, var msg: String?) : Exception(msg)