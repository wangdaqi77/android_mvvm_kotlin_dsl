package com.wongki.framework.mvvm.lifecycle.exception

import com.wongki.framework.mvvm.lifecycle.LiveDataKey

/**
 * @author  wangqi
 * date:    2019-11-05
 * email:   wangqi7676@163.com
 * desc:    .
 */
class NoSetValueException(key: LiveDataKey) : Exception("没有设置value, key:${key.key}") {
}