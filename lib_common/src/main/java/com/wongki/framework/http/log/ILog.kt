package com.wongki.framework.http.log

import android.util.Log

/**
 * @author  wangqi
 * date:    2019-11-12
 * email:   wangqi7676@163.com
 * desc:    .
 */
interface ILog {
    fun log(message: String)

    companion object DEFAULT : ILog {
        override fun log(message: String) {
            Log.d("ILog.DEFAULT", message)
        }
    }
}