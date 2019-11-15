package com.wongki.demo.model.repo

import com.wongki.demo.model.bean.SearchMusic
import com.wongki.framework.EventObserverBuilder
import com.wongki.framework.model.domain.MyResponse

/**
 * @author  wangqi
 * date:    2019-11-15
 * email:   wangqi7676@163.com
 * 声明音乐仓库接口
 */
interface IMusicRepo {
    /**
     * 搜索音乐
     * @param name 要搜索的音乐名称
     * @param init 网络请求观察者构建器
     */
    fun searchMusic(name:String,init: EventObserverBuilder<MyResponse<ArrayList<SearchMusic.Response.Item>>>.()->Unit)
}