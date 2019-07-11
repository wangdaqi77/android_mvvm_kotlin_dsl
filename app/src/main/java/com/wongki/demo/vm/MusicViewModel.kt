package com.wongki.demo.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.wongki.demo.http.newMusicRequester
import com.wongki.demo.model.bean.SearchMusic
import com.wongki.framework.mvvm.lifecycle.DataWrapper
import com.wongki.framework.mvvm.retrofit.RetrofitLiveDataViewModel

/**
 * @author  wangqi
 * date:    2019/7/8
 * email:   wangqi7676@163.com
 * desc:    .
 */

class MusicViewModel : ViewModel(), RetrofitLiveDataViewModel {

    override val mSystemLiveData: HashMap<String, MutableLiveData<DataWrapper<*>>?> = HashMap()

    fun searchMusic(name: String) {
        newMusicRequester(this) { api -> api.searchMusic(name) }
            .commitForArrayList(SearchMusic.Item::class.java)

    }

    override fun onCleared() {
        super<RetrofitLiveDataViewModel>.onCleared()
    }
}
