package com.wongki.demo.http

import com.wongki.demo.model.api.MusicApi
import com.wongki.framework.http.retrofit.core.RetrofitServiceCore

/**
 * @author  wangqi
 * date:    2019/6/26
 * email:   wangqi7676@163.com
 * desc:    .
 */
object MusicServiceCore : RetrofitServiceCore<MusicApi>() {
    override val mHost = "https://api.apiopen.top"

    /**
     * 公共请求头
     */
    override var mCommonRequestHeader: MutableMap<String, String> = mutableMapOf()

    /**
     * 公共Url参数
     * ex: &sex=1&age=18
     */
    override var mCommonUrlRequestParams: MutableMap<String, String> = mutableMapOf()

}