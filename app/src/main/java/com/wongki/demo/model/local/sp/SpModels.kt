package com.wongki.demo.model.local.sp

import com.feigeter.common.sp.SharedPreferencesModel

/**
 * @author  wangqi
 * date:    2019/7/11
 * email:   wangqi7676@163.com
 * desc:    .
 */

//是否登录
var SharedPreferencesModel.isLogin
        by SharedPreferencesModel.boolean(false)


// 第一条音乐的标题
var SharedPreferencesModel.firstMusicTitle
        by SharedPreferencesModel.string()


// 第一条音乐的作者
var SharedPreferencesModel.firstMusicAuthor
        by SharedPreferencesModel.string()




