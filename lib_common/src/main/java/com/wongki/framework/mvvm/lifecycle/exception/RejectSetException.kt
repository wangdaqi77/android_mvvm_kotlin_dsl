package com.wongki.framework.mvvm.lifecycle.exception


/**
 * @author  wangqi
 * date:    2019-11-05
 * email:   wangqi7676@163.com
 * desc:    .
 */
class RejectSetException(field: String) : Exception("拒绝设置 field:${field}") {
}