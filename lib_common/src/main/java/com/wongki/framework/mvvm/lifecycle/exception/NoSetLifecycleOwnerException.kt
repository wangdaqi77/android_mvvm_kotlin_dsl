package com.wongki.framework.mvvm.lifecycle.exception

/**
 * @author  wangqi
 * date:    2019-11-05
 * email:   wangqi7676@163.com
 * desc:    .
 */
class NoSetLifecycleOwnerException() : Exception("observe时没有设置LifecycleOwner") {
}