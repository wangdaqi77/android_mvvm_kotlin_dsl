package com.wongki.framework.base

import com.wongki.framework.http.retrofit.lifecycle.IHttpRetrofitLifecycleObserver


/**
 * @author  wangqi
 * date:    2019/6/26
 * email:   wangqi7676@163.com
 * desc:    .
 */
open class BaseHttpLifecycleActivity:BaseActivity(), IHttpRetrofitLifecycleObserver {
    override fun onDestroy() {
        super<IHttpRetrofitLifecycleObserver>.onDestroy()
        super<BaseActivity>.onDestroy()
    }
}