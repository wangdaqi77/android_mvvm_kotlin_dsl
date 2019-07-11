package com.wongki.demo.vm

import com.wongki.demo.http.MusicServiceCore
import com.wongki.framework.mvvm.AbsLiveDataViewModel
import com.wongki.framework.mvvm.retrofit.launchRemoteResp

/**
 * @author  wangqi
 * date:    2019/7/8
 * email:   wangqi7676@163.com
 * desc:    .
 */

class MusicViewModel : AbsLiveDataViewModel() {

    fun searchMusic(name: String) {
        launchRemoteResp(MusicServiceCore) {
            searchMusic(name)
        }.commitForArrayList()

    }
}
