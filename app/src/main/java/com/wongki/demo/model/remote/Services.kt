package com.wongki.demo.model.remote

import com.wongki.framework.http.HttpDsl

/**
 * @author  wangqi
 * date:    2019/6/26
 * email:   wangqi7676@163.com
 * desc:    .
 */

@HttpDsl
fun musicService(init:MusicServiceCore.()->Unit){MusicServiceCore.init()}
