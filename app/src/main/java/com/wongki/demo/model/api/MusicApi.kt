package com.wongki.demo.model.api

import com.wongki.demo.model.bean.SearchMusic
import com.wongki.framework.model.domain.CommonResponse
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
    @GET("/searchMusic")
    fun searchMusic(@Query("name")name:String):Observable<CommonResponse<ArrayList<SearchMusic.Item>>>
}