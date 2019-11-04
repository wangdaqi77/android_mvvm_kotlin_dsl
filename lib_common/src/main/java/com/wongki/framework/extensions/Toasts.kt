package com.wongki.framework.extensions

import android.content.Context
import android.widget.Toast

/**
 * @author  wangqi
 * date:    2019/6/26
 * email:   wangqi7676@163.com
 * desc:    toast拓展方法
 */


fun Any.toast(context: Context = getApp(), duration: Int = Toast.LENGTH_LONG) {
    Toast.makeText(context, this.toString(), duration).show()
}