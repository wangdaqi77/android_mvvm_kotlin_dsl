package com.wongki.framework.mvvm.exception

import com.wongki.framework.mvvm.lifecycle.LiveDataViewModel

/**
 * @author  wangqi
 * date:    2019-11-06
 * email:   wangqi7676@163.com
 * desc:    .
 */

class ViewModelNoMatchedException(clazz: Class<*>) :Exception("ViewModel的类型不匹配，${clazz.name}请继承${LiveDataViewModel::class.qualifiedName}")