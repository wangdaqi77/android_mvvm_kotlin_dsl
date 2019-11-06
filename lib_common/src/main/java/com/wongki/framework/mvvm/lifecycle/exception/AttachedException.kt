package com.wongki.framework.mvvm.lifecycle.exception

import com.wongki.framework.mvvm.lifecycle.LiveDataKey

/**
 * @author  wangqi
 * date:    2019-11-05
 * email:   wangqi7676@163.com
 * desc:    .
 */
class AttachedException(key: LiveDataKey) : Exception("已经装载过, fullKey:${key.key}") {
}