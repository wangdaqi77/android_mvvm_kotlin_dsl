package com.wongki.framework.mvvm.lifecycle.wrap.event

import com.wongki.framework.mvvm.event.Event
import com.wongki.framework.mvvm.lifecycle.LiveDataViewModelDslMarker

/**
 * @author  wangqi
 * date:    2019/7/4
 * email:   wangqi7676@163.com
 * desc:
 */
@LiveDataViewModelDslMarker
open class EventValueBuilder<T> {
    var event: Event = Event.DEFAULT
    var data: T? = null // 真正的数据
    var code: Int = -1 // 错误码
    var message: String = "" // 错误信息

    fun  build(): EventValue<T> {
        val dataWrapper = EventValue<T>()
        dataWrapper.event = event
        dataWrapper.data = data
        dataWrapper.code = code
        dataWrapper.message = message
        return dataWrapper
    }
}