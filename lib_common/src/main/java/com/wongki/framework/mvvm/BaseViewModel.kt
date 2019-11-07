package com.wongki.framework.mvvm

import com.wongki.framework.mvvm.lifecycle.ILiveDataViewModel
import com.wongki.framework.mvvm.lifecycle.LiveDataViewModel
import com.wongki.framework.mvvm.lifecycle.LiveDataViewModelDslMarker
import com.wongki.framework.mvvm.lifecycle.wrap.IEventLiveDataViewModel
import com.wongki.framework.mvvm.remote.retrofit.IRetrofitViewModel

/**
 * @author  wangqi
 * date:    2019-11-05
 * email:   wangqi7676@163.com
 * desc:    .
 */
@LiveDataViewModelDslMarker
abstract class BaseViewModel private constructor(liveDataViewModel: LiveDataViewModel):
    IEventLiveDataViewModel,IRetrofitViewModel,ILiveDataViewModel by liveDataViewModel {
    constructor():this(LiveDataViewModel())

}