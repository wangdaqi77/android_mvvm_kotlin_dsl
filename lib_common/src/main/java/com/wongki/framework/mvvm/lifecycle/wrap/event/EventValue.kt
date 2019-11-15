package com.wongki.framework.mvvm.lifecycle.wrap.event

import com.wongki.framework.mvvm.event.Event
import com.wongki.framework.mvvm.lifecycle.LiveDataViewModelDslMarker

/**
 * @author  wangqi
 * date:    2019/7/4
 * email:   wangqi7676@163.com
 * desc:    .
 *          数据包装类
 */
@LiveDataViewModelDslMarker
open class EventValue<T> {
    var event: Event = Event.DEFAULT
    var realValue: T? = null // 真正的数据
    var code: Int = -1 // 错误码
    var message: String = "" // 错误信息
    var errorProcessed = false //失败是否处理

    override fun toString(): String {
        return "event:${event.name}, " +
                "realValue:${realValue}, " +
                "code:${code}, " +
                "message:${message}, " +
                "errorProcessed:${errorProcessed}"
    }
}