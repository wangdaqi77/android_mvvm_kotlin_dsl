package com.wongki.framework.mvvm.lifecycle.exception

import com.wongki.framework.mvvm.lifecycle.Key

/**
 * @author  wangqi
 * date:    2019-11-05
 * email:   wangqi7676@163.com
 * desc:    .
 */
class AttachedException(key: Key) : Exception("已经装载过, key:${key.key}") {
}