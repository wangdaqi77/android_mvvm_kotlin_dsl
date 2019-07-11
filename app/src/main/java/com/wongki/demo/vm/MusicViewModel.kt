package com.wongki.demo.vm

import androidx.lifecycle.MutableLiveData
import com.wongki.demo.http.newMusicRequester
import com.wongki.framework.mvvm.lifecycle.DataWrapper
import com.wongki.framework.mvvm.AbsLiveDataViewModel

/**
 * @author  wangqi
 * date:    2019/7/8
 * email:   wangqi7676@163.com
 * desc:    .
 */

class MusicViewModel :  AbsLiveDataViewModel() {

    override val mSystemLiveData: HashMap<String, MutableLiveData<DataWrapper<*>>?> = HashMap()

    fun searchMusic(name: String) {
        newMusicRequester(this) { api -> api.searchMusic(name) }
            .commitForArrayList()

    }
}
