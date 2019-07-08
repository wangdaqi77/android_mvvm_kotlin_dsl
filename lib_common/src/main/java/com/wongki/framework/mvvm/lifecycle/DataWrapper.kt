package com.wongki.framework.mvvm.lifecycle

import com.wongki.framework.mvvm.action.EventAction
import com.wongki.framework.mvvm.retrofit.RetrofitLiveDataViewModel

/**
 * @author  wangqi
 * date:    2019/7/4
 * email:   wangqi7676@163.com
 * desc:    .
 *          数据包装类
 */
open class DataWrapper<T> {
    /**
     * 当Action发生改变时postValue[RetrofitLiveDataViewModel.commit]
     */
    var action: EventAction = EventAction.DEFAULT
    var data: T? = null // 真正的数据
    var code: Int = -1 // 错误码
    var message: String? = null // 错误信息
}