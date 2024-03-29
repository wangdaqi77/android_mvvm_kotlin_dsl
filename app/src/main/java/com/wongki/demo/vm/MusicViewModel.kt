package com.wongki.demo.vm

import com.wongki.demo.model.bean.SearchMusic
import com.wongki.demo.model.repo.MusicRepo
import com.wongki.framework.mvvm.lifecycle.LiveDataViewModel
import com.wongki.framework.mvvm.lifecycle.LiveDataViewModelDslMarker

/**
 * @author  wangqi
 * date:    2019/7/8
 * email:   wangqi7676@163.com
 * desc:    .
 */

@LiveDataViewModelDslMarker
class MusicViewModel : LiveDataViewModel<MusicRepo>() {

    fun searchMusic(name: String) {
        // 一：通过仓库搜索音乐 (推荐)
        repository {

            searchMusic(name) {

                /**
                 * 网络请求的观察器转换成EventValue通知[MusicActivity.attachEventObserveForArrayList<SearchMusic.Response.Item>]
                 * &&
                 * 在通知UI前观察数据（设置搜索结果总数和设置结果列表）
                 */
                observeAndTransformEventObserverForArrayList {
                    onSuccess {
                        // 设置搜索结果总数
                        this@MusicViewModel.setTotalCount(this)
                        // 设置结果列表
                        this@MusicViewModel.setResultList(this)
                    }
                }

            }

        }


        // 二：直接通过远端服务搜索音乐
//        musicService {
//
//            api { searchMusic(name = name) }.thenCall {
//
//                /**
//                 * 网络请求的观察器转换成EventValue通知[MusicActivity.attachEventObserveForArrayList<SearchMusic.Response.Item>]
//                 * &&
//                 * 在通知UI前观察数据（设置搜索结果总数和设置结果列表）
//                 */
//                observeAndTransformEventObserverForArrayList {
//                    onSuccess {
//                        // 设置搜索结果总数
//                        this@MusicViewModel.setTotalCount(this)
//                        // 设置结果列表
//                        this@MusicViewModel.setResultList(this)
//                    }
//                }
//            }
//
//        }
    }

    // 设置结果总数
    private fun setTotalCount(list: ArrayList<SearchMusic.Response.Item>?) {

        // 通知订阅的地方
        setValue<Int> {
            key {
                method = "setTotalCount"
            }

            value {
                list?.size ?: 0
            }
        }

    }

    // 设置结果列表
    private fun setResultList(list: ArrayList<SearchMusic.Response.Item>?) {
        var result = ""
        list?.apply {
            this.forEachIndexed { index, item ->
                result += "${index + 1}. ${item.title} - ${item.author}  播放地址:${item.url}\n"
            }
        }

        if (result.isEmpty()) result = "暂无结果"

        // 通知订阅的地方
        setValue<String> {
            key {
                method = "setResultList"
            }
            value { result }
        }

    }
}
