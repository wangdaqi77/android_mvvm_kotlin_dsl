package com.wongki.framework.mvvm.lifecycle.exception

import com.wongki.framework.mvvm.lifecycle.LiveDataKey

/**
 * @author  wangqi
 * date:    2019-11-05
 * email:   wangqi7676@163.com
 * desc:    .
 */
class NoSetLifecycleOwnerException(key: LiveDataKey) : Exception("observe时没有设置LifecycleOwner, key:${key.key}") {
}