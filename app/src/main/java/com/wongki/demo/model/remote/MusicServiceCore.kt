package com.wongki.demo.model.remote

import com.wongki.demo.model.remote.api.MusicApi
import com.wongki.framework.http.retrofit.core.RetrofitServiceCore

/**
 *
 * @author  wangqi
 * date:    2019/6/26
 * email:   wangqi7676@163.com
 * desc:    音乐服务核心类 关联api接口[MusicApi]
 *          可以配置：
 *          1.域名 [mHost]、
 *          2.超时时间[mConnectTimeOut]、
 *          3.请求头[getCommonRequestHeader]、
 *          4.请求参数[getCommonUrlRequestParams]、
 *          5.错误拦截(需要重写)[selfServiceApiErrorInterceptor]
 *
 */
object MusicServiceCore : RetrofitServiceCore<MusicApi>() {
    override val mHost = "https://api.apiopen.top"


    /**
     * 公共请求头
     */
    override fun getCommonRequestHeader(): MutableMap<String, String> = mutableMapOf()

    /**
     * 公共Url参数
     * ex: &sex=1&age=18
     */
    override fun getCommonUrlRequestParams(): MutableMap<String, String> = mutableMapOf()
}