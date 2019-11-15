package com.wongki.demo.model.repo.remote.service

import com.wongki.demo.model.bean.SearchMusic
import com.wongki.framework.http.retrofit.core.RetrofitServiceCore
import com.wongki.framework.model.domain.MyResponse
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

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

interface MusicApi {
    // name = fffffsfffsaasa时，返回{"code":200,"message":"成功!","result":""}，不规范！
    @GET("/searchMusic")
    fun searchMusic(@Query("name")name:String): Observable<MyResponse<ArrayList<SearchMusic.Response.Item>>>
}
