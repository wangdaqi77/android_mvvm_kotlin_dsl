package com.wongki.demo.vm

import com.wongki.demo.model.remote.musicService
import com.wongki.demo.model.bean.SearchMusic
import com.wongki.framework.mvvm.AbsLiveDataViewModel
import com.wongki.framework.mvvm.LiveDataViewModelDslMarker

/**
 * @author  wangqi
 * date:    2019/7/8
 * email:   wangqi7676@163.com
 * desc:    .
 */

@LiveDataViewModelDslMarker
class MusicViewModel : AbsLiveDataViewModel() {

    fun searchMusic(name: String) {

        musicService {

            api<ArrayList<SearchMusic.Item>> {

                call { searchMusic(name = name) }

                observeForArrayList{
                    // 成功时,如果你需要修改数据...
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
}
