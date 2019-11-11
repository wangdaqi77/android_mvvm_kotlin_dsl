package com.wongki.demo.model.remote.api

import com.wongki.framework.model.domain.MyResponse
import com.wongki.demo.model.bean.SearchMusic
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * @author  wangqi
 * date:    2019/6/26
 * email:   wangqi7676@163.com
 * desc:    .
 */
interface MusicApi {
    // name = fffffsfffsaasa时，返回{"code":200,"message":"成功!","result":""}，不规范！
    @GET("/searchMusic")
    fun searchMusic(@Query("name")name:String):Observable<MyResponse<ArrayList<SearchMusic.Item>>>
}