package com.wongki.framework.mvvm.lifecycle

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.wongki.framework.mvvm.AbsLiveDataViewModel
import com.wongki.framework.mvvm.action.EventAction

/**
 * @author  wangqi
 * date:    2019/7/30
 * email:   wangqi7676@163.com
 * desc:    .
 */
class WrapLiveData<T> : MutableLiveData<DataWrapper<T>>() {


    fun setValue(action: EventAction, apply: DataWrapper<T>.() -> Unit): DataWrapper<T> {
        val dataWrapper = DataWrapper<T>()
        dataWrapper.action = action
        apply(dataWrapper)
        // 发送到订阅的位置
        super.setValue(dataWrapper)
        return dataWrapper
    }


    /**
     * 发送数据到接收者[observe]
     */
    fun setValueForAction(action: EventAction) {
        setValue(action) {}
    }


    /**
     * 发送数据到接收者[observe]
     */
    @Suppress("UNCHECKED_CAST")
    fun <ITEM> setValueForArrayList(
        action: EventAction,
        apply: DataWrapper<ArrayList<ITEM>>.() -> Unit
    ): DataWrapper<ArrayList<ITEM>> {
        val dataWrapper = DataWrapper<ArrayList<ITEM>>()
        dataWrapper.action = action
        apply(dataWrapper)
        // 发送到订阅的位置
        super.setValue(dataWrapper as DataWrapper<T>)
        return dataWrapper
    }


    /**
     * 发送数据到接收者[observe]
     */
    fun <ITEM : Any> setValueForArrayListForAction(action: EventAction) {
        setValueForArrayList<ITEM>(action) {}
    }


    @Deprecated("过时")
    override fun setValue(value: DataWrapper<T>?) {
    }

    @Deprecated("过时")
    override fun postValue(value: DataWrapper<T>?) {
    }

    @Deprecated("过时")
    override fun observe(owner: LifecycleOwner, observer: Observer<in DataWrapper<T>>) {
    }

    @Deprecated("过时")
    override fun observeForever(observer: Observer<in DataWrapper<T>>) {
    }

    /**
     * 订阅，接收数据变化的事件通知
     * 通知数据变化->[AbsLiveDataViewModel.observe]
     */
    fun observeSimple(
        owner: LifecycleOwner,
        onSuccess: (T?) -> Unit
    ) {

        super.observe(owner, Observer<DataWrapper<T>> { result ->
            if (result != null) {
                val action = result.action
                when (action) {
                    EventAction.SUCCESS -> {
                        onSuccess(result.data)
                    }
                    else -> {
                        Log.w("LiveDataViewModel", "未处理的Action：${action.name}")
                    }
                }
            }

        })
    }


    /**
     * 订阅，接收数据变化的事件通知
     * 通知数据变化->[AbsLiveDataViewModel.observe]
     * @param onFailed 返回true代表上层处理，返回false代表框架处理，目前框架层会弹Toast
     */
    fun observe(
        owner: LifecycleOwner,
        onStart: () -> Unit,
        onCancel: () -> Unit,
        onFailed: (Int, String?) -> Boolean,
        onSuccess: (T?) -> Unit
    ) {
        super.observe(owner, Observer<DataWrapper<T>> { result ->
            if (result != null) {
                val action = result.action
                when (action) {
                    EventAction.START -> {
                        onStart()
                    }
                    EventAction.CANCEL -> {
                        onCancel()
                    }
                    EventAction.SUCCESS -> {
                        onSuccess(result.data)
                    }
                    EventAction.FAILED -> {
                        val errorProcessed = onFailed(result.code, result.message)
                        result.errorProcessed = errorProcessed
                    }
                    else -> {
                        Log.e("LiveDataViewModel", "未处理的Action：${action.name}")
                    }
                }
            }

        })
    }
}