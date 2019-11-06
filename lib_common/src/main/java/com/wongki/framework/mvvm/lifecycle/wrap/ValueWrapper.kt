package com.wongki.framework.mvvm.lifecycle.wrap

import com.wongki.framework.mvvm.action.EventAction
import com.wongki.framework.mvvm.lifecycle.LiveDataViewModelDslMarker

/**
 * @author  wangqi
 * date:    2019/7/4
 * email:   wangqi7676@163.com
 * desc:    .
 *          数据包装类
 */
@LiveDataViewModelDslMarker
open class ValueWrapper<T> {
    var action: EventAction = EventAction.DEFAULT
    var data: T? = null // 真正的数据
    var code: Int = -1 // 错误码
    var message: String = "" // 错误信息
    var errorProcessed = false //失败已经处理
}