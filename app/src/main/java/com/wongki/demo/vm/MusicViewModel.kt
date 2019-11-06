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

        musicService {

            call<ArrayList<SearchMusic.Item>> {

                api { searchMusic(name = name) }

                observeLiveDataWrapperForArrayList {
                    // 设置结果总数
                    setTotalCount(this)
                    // 设置结果
                    setResultList(this)
                }
            }

        }

    }

    private fun setTotalCount(list: ArrayList<SearchMusic.Item>?) {

        setValue<Int> {
            kClass = Int::class
            key = "setTotalCount"
            value {
                list?.size
            }
        }

    }

    private fun setResultList(list: ArrayList<SearchMusic.Item>?) {
        var result = ""
        list?.apply {
            this.forEachIndexed { index, item ->
                result += "${index + 1}. ${item.title} - ${item.author}  播放地址:${item.url}\n"
            }
        }

        if (result.isEmpty()) result = "暂无结果"

        setValue<String> {
            kClass = String::class
            key = "setResultList"
            value { result }
        }

    }
}
