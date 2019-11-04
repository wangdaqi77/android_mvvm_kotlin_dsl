package com.wongki.demo.model.remote

import com.wongki.framework.http.retrofit.core.RetrofitServiceDslMarker

/**
 * @author  wangqi
 * date:    2019/6/26
 * email:   wangqi7676@163.com
 * desc:    .
 */

@RetrofitServiceDslMarker
fun musicService(init:MusicServiceCore.()->Unit){MusicServiceCore.init()}
