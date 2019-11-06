package com.wongki.demo.vm

import com.wongki.demo.model.remote.musicService
import com.wongki.demo.model.bean.SearchMusic
import com.wongki.framework.mvvm.lifecycle.LiveDataViewModel
import com.wongki.framework.mvvm.lifecycle.LiveDataViewModelDslMarker

/**
 * @author  wangqi
 * date:    2019/7/8
 * email:   wangqi7676@163.com
 * desc:    .
 */

@LiveDataViewModelDslMarker
class MusicViewModel : LiveDataViewModel() {

    fun searchMusic(name: String) {
        // 请求服务器获取搜索结果
        musicService {

            callArrayList<SearchMusic.Item> {

                api { searchMusic(name = name) }

                // 真正发起网络请求&&通知UI前做一些事情（ex：设置结果总数和设置结果列表）
                observeWithBeforeNotifyUIForArrayList {

                    onSuccess{
                        // 设置结果总数
                        this@MusicViewModel.setTotalCount(this)
                        // 设置结果列表
                        this@MusicViewModel.setResultList(this)
                    }

                }
            }

        }

    }

    // 设置结果总数
    private fun setTotalCount(list: ArrayList<SearchMusic.Item>?) {

        // 通知订阅的地方
        setValue<Int> {
            kClass = Int::class
            key = "setTotalCount"
            value {
                list?.size
            }
        }

    }

    // 设置结果列表
    private fun setResultList(list: ArrayList<SearchMusic.Item>?) {
        var result = ""
        list?.apply {
            this.forEachIndexed { index, item ->
                result += "${index + 1}. ${item.title} - ${item.author}  播放地址:${item.url}\n"
            }
        }

        if (result.isEmpty()) result = "暂无结果"

        // 通知订阅的地方
        setValue<String> {
            kClass = String::class
            key = "setResultList"
            value { result }
        }

    }
}
