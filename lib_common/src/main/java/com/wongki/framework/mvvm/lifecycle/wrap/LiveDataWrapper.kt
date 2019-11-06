package com.wongki.framework.mvvm.lifecycle.wrap

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.wongki.framework.mvvm.action.EventAction
import com.wongki.framework.mvvm.lifecycle.LiveDataViewModelDslMarker
import java.lang.RuntimeException

/**
 * @author  wangqi
 * date:    2019/7/30
 * email:   wangqi7676@163.com
 * desc:    .
 */
@LiveDataViewModelDslMarker
class LiveDataWrapper<T> : MutableLiveData<ValueWrapper<T>>() {

    /**
     * 发送数据到接收者[observe]
     */
    @Suppress("UNCHECKED_CAST")
    internal fun <ITEM> setValueForArrayList(value: ValueWrapper<ArrayList<ITEM>>?): ValueWrapper<ArrayList<ITEM>> {
        // 发送到订阅的位置
        super.setValue(value as ValueWrapper<T>)
        return value
    }


    override fun setValue(value: ValueWrapper<T>?) {
        super.setValue(value)
    }

    override fun postValue(value: ValueWrapper<T>?) {
        throw RuntimeException("不支持此api")
    }

    override fun observe(owner: LifecycleOwner, observer: Observer<in ValueWrapper<T>>) {
        throw RuntimeException("不支持此api")
    }

    override fun observeForever(observer: Observer<in ValueWrapper<T>>) {
        throw RuntimeException("不支持此api")
    }

    /**
     * 订阅，接收数据变化的事件通知
     * 通知数据变化->[AbsLiveDataWrapperViewModel.observeLiveDataWrapper]
     * @param onFailed 返回true代表上层处理，返回false代表框架处理，目前框架层会弹Toast
     */
    fun observe(init: ObserveBuilder.() -> Unit) {
        val observeBuilder = ObserveBuilder()
        observeBuilder.init()
        super.observe(observeBuilder.owner, Observer<ValueWrapper<T>> { result ->
            if (result != null) {
                val action = result.action
                when (action) {
                    EventAction.START -> {
                        observeBuilder.onStart?.invoke()
                    }
                    EventAction.CANCEL -> {
                        observeBuilder.onCancel?.invoke()
                    }
                    EventAction.SUCCESS -> {
                        observeBuilder.onSuccess?.invoke(result.data)
                    }
                    EventAction.FAILED -> {
                        val errorProcessed =
                            observeBuilder.onFailed?.invoke(result.code, result.message)
                        result.errorProcessed = errorProcessed ?: false
                    }
                    else -> {
                        Log.e("LiveDataViewModel", "未处理的Action：${action.name}")
                    }
                }
            }

        })
    }


    @LiveDataViewModelDslMarker
    inner class ObserveBuilder {
        lateinit var owner: LifecycleOwner
        internal var onStart: (() -> Unit)? = null
        internal var onCancel: (() -> Unit)? = null
        internal var onFailed: ((Int, String) -> Boolean)? = null
        internal var onSuccess: (T?.() -> Unit)? = null

        fun onStart(onStart: () -> Unit) {
            this.onStart = onStart
        }

        fun onCancel(onCancel: () -> Unit) {
            this.onCancel = onCancel
        }

        fun onFailed(onFailed: ((Int, String) -> Boolean)) {
            this.onFailed = onFailed
        }

        fun onSuccess(onSuccess: T?.() -> Unit) {
            this.onSuccess = onSuccess
        }

    }


}