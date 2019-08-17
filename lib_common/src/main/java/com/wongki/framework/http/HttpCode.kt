package com.wongki.framework.http

/**
 * @author  wangqi
 * date:    2019/6/17
 * email:   wangqi7676@163.com
 * desc:    .
 */
object HttpCode {
    //成功状态码
    const val SUCCESSFUL = 200
    const val JSON_REQUEST_SUCCESSFUL = 0
    //更新APP
    const val UPDATE = 999
    const val STOP_SERVER = 888
    const val TOKEN_INVIALD = 401
    //用户资质不符，去贷超标识
    const val GOTODAICHAO = 406


    // 未知失败
    const val UNKNOWN_FAILED = -100
    // 解析数据失败
    const val PARSE_FAILED = -101
    // 连接失败
    const val CONNECTION_FAILED = -102
    // host错误
    const val HOST_UNKNOWN_FAILED = -103
    // 请求超时
    const val TIMEOUT_FAILED = -104
    // 请求失败
    const val REQUEST_FAILED = -105
    // 文件未找到
    const val FILE_NOT_FOUND_FAILED = -106
    // 写入文件失败
    const val FILE_WRITE_FAILED = -107
}