package com.wongki.framework.rx

import io.reactivex.ObservableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * @author  wangqi
 * date:    2019/6/10
 * email:   wangqi7676@163.com
 * desc:    .
 */


object RxSchedulers {

    /**
     * 默认的调度
     * 被观察者在io，观察者在主线程
     */
    fun <T> applyRetrofitHttpDefaultSchedulers(): ObservableTransformer<T, T> {
        return ObservableTransformer { observable ->
            observable.subscribeOn(Schedulers.io())
                    .unsubscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
        }
    }
}