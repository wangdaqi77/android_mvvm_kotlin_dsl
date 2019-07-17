package com.wongki.framework.mvvm.local

import com.feigeter.common.sp.SharedPreferencesModel
/**
 * @author  wangqi
 * date:    2019/7/11
 * email:   wangqi7676@163.com
 * desc:    .
 */

inline fun Any.launchLocalSpRepo(apply:SharedPreferencesModel.()->Unit) {
    SharedPreferencesModel.apply()
}


