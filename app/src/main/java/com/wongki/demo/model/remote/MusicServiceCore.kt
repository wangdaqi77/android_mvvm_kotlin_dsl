package com.wongki.demo.model.remote

import com.wongki.demo.model.remote.api.MusicApi
import com.wongki.framework.http.config.HttpConfig
import com.wongki.framework.http.retrofit.core.RetrofitServiceCore

/**
 *
 * @author  wangqi
 * date:    2019/6/26
 * email:   wangqi7676@163.com
 * desc:    音乐服务核心类 关联api接口[MusicApi]
 *
 */
object MusicServiceCore : RetrofitServiceCore<MusicApi>() {
    override fun generateDefaultConfig() = config {
        host = "https://api.apiopen.top"
    }
}