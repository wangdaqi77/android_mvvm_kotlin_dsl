package com.wongki.framework.mvvm.lifecycle.wrap.event

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.wongki.framework.mvvm.event.Event
import com.wongki.framework.mvvm.lifecycle.LiveDataViewModelDslMarker
import java.lang.RuntimeException

/**
 * @author  wangqi
 * date:    2019/7/30
 * email:   wangqi7676@163.com
 * desc:    .
 */
@LiveDataViewModelDslMarker
class EventLiveData<T> : MutableLiveData<EventValue<T>>() {

    /**
     * 发送数据到接收者[observe]
     */
    @Suppress("UNCHECKED_CAST")
    internal fun <ITEM> setValueForArrayList(value: EventValue<ArrayList<ITEM>>?): EventValue<ArrayList<ITEM>> {
        // 发送到订阅的位置
        super.setValue(value as EventValue<T>)
        return value
    }


    override fun setValue(value: EventValue<T>?) {
        super.setValue(value)
    }

    override fun postValue(value: EventValue<T>?) {
        throw RuntimeException("不支持此api")
    }

    override fun observe(owner: LifecycleOwner, observer: Observer<in EventValue<T>>) {
        throw RuntimeException("不支持此api")
    }

    override fun observeForever(observer: Observer<in EventValue<T>>) {
        throw RuntimeException("不支持此api")
    }

    /**
     * 订阅，接收数据变化的事件通知
     */
    fun observe(owner: LifecycleOwner, builder: EventValueObserveBuilder<T>) {
        super.observe(owner, Observer<EventValue<T>> { result ->
            if (result != null) {
                val action = result.event
                when (action) {
                    Event.START -> {
                        builder.onStart?.invoke()
                    }
                    Event.CANCEL -> {
                        builder.onCancel?.invoke()
                    }
                    Event.SUCCESS -> {
                        builder.onSuccess?.invoke(result.value)
                    }
                    Event.FAILED -> {
                        val errorProcessed =
                            builder.onFailed?.invoke(result.code, result.message)
                        result.errorProcessed = errorProcessed ?: false
                    }
                    else -> {
                        Log.e("LiveDataViewModel", "未处理的Action：${action.name}")
                    }
                }
            }

        })
    }


}