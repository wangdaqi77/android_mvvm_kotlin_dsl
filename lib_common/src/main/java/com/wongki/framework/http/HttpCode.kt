package com.wongki.framework.http

object HttpCode {
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