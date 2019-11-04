package com.wongki.framework.utils

import io.reactivex.ObservableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


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
