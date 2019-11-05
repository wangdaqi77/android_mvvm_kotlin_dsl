package com.wongki.framework.mvvm.lifecycle.wrap


/**
 * @author  wangqi
 * date:    2019/7/4
 * email:   wangqi7676@163.com
 * desc:    .
 */
interface ILiveDataWrapperViewModel {

    val mLiveDataWrappers: HashMap<WrapperKey, LiveDataWrapper<*>?>

}