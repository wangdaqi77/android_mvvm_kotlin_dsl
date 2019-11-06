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
                    // 成功时,如果你需要修改数据...
                    setTotalCount(this)
                }
            }

        }


//
//        musicService {
//
//            api({ searchMusic(name = name)}) {
//
//                observeForArrayList{
//                    // 成功时,如果你需要修改数据...
//                }
//            }
//
//        }

    }

    private fun setTotalCount(list: ArrayList<SearchMusic.Item>?) {

        setValue<Int> {
            kClass = Int::class
            key = "total"
            value {
                list?.size
            }
        }

    }
}
