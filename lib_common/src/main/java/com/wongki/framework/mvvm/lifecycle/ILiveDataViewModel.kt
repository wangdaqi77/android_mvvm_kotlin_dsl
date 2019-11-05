package com.wongki.framework.mvvm.lifecycle

import androidx.lifecycle.MutableLiveData


/**
 * @author  wangqi
 * date:    2019/7/4
 * email:   wangqi7676@163.com
 * desc:    .
 */
interface ILiveDataViewModel {

    val mLiveDatas: HashMap<Key, MutableLiveData<*>?>

}