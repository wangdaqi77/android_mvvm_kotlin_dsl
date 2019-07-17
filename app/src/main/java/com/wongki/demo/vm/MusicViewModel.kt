package com.wongki.demo.vm

import com.wongki.demo.http.MusicServiceCore
import com.wongki.framework.mvvm.AbsLiveDataViewModel
import com.wongki.framework.mvvm.remote.launchRemoteRepo

/**
 * @author  wangqi
 * date:    2019/7/8
 * email:   wangqi7676@163.com
 * desc:    .
 */

class MusicViewModel : AbsLiveDataViewModel() {

    fun searchMusic(name: String) {
        /**
         * 无需请求成功后的操作
         */
        //1. 启动远端仓库
        launchRemoteRepo(MusicServiceCore) {
            // 2. 远端仓库的api请求
            searchMusic(name)

            //3. 提交远端仓库的api请求
        }.commitForArrayList()


        /**
         * 如果需要保存数据则，需要请求成功后的操作
         */
//        // 1. 启动远端仓库
//        launchRemoteRepo(MusicServiceCore) {
//            // 2. 远端仓库的api请求
//            searchMusic(name = name)
//
//            //3. 提交远端仓库的api请求
//        }.commitForArrayList()
//
//        // 4. 异步请求远端仓库的成功的回调
//        { result ->
//
//            if (!result.isNullOrEmpty()) {
//                // 5. 启动本地SP仓库
//                launchLocalSpRepo {
//                    // 6. 保存第一条数据到SP
//                    val firstMusic = result.first()
//                    this.firstMusicAuthor = firstMusic.author
//                    this.firstMusicTitle = firstMusic.title
//                }
//            }
//
//        }
    }
}
