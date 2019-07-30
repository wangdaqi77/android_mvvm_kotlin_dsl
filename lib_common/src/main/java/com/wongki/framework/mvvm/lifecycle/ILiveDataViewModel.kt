package com.wongki.framework.mvvm.lifecycle

import java.lang.RuntimeException
import kotlin.reflect.KClass

/**
 * @author  wangqi
 * date:    2019/7/4
 * email:   wangqi7676@163.com
 * desc:    .
 *          ViewModel接口
 *          核心：管理多个子LiveData，管理多种类型数据变动
 */
interface ILiveDataViewModel {

    val mSystemLiveData: HashMap<String, WrapLiveData<*>?>

}