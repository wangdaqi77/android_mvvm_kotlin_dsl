package com.wongki.framework.mvvm.lifecycle.wrap

import com.wongki.framework.mvvm.action.EventAction
import com.wongki.framework.mvvm.lifecycle.LiveDataViewModelDslMarker

/**
 * @author  wangqi
 * date:    2019/7/4
 * email:   wangqi7676@163.com
 * desc:
 */
@LiveDataViewModelDslMarker
open class ValueWrapperBuilder<T> {
    var action: EventAction = EventAction.DEFAULT
    var data: T? = null // 真正的数据
    var code: Int = -1 // 错误码
    var message: String = "" // 错误信息

    fun  build():ValueWrapper<T>{
        val dataWrapper = ValueWrapper<T>()
        dataWrapper.action = action
        dataWrapper.data = data
        dataWrapper.code = code
        dataWrapper.message = message
        return dataWrapper
    }
}