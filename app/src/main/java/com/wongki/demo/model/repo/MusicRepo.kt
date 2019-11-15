package com.wongki.demo.model.repo

import com.wongki.demo.model.bean.SearchMusic
import com.wongki.demo.model.repo.remote.BaseRepository
import com.wongki.demo.model.repo.remote.musicService
import com.wongki.demo.model.repo.remote.service.MusicApi
import com.wongki.framework.EventObserverBuilder
import com.wongki.framework.http.retrofit.core.RetrofitServiceCore
import com.wongki.framework.http.retrofit.core.thenCall
import com.wongki.framework.model.domain.MyResponse

/**
 * @author  wangqi
 * date:    2019-11-15
 * email:   wangqi7676@163.com
 * 音乐仓库
 */
class MusicRepo : BaseRepository(), IMusicRepo {

    /**
     * 搜索音乐
     * @param name 要搜索的音乐名称
     * @param init 网络请求观察者构建器
     */
    override fun searchMusic(name: String, init: EventObserverBuilder<MyResponse<ArrayList<SearchMusic.Response.Item>>>.() -> Unit) {
        musicService {
            api { searchMusic(name) }.thenCall {
                lifecycleObserver = this@MusicRepo
                observer(init)
            }
        }
    }

}