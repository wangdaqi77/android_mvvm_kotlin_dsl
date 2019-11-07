package com.wongki.framework.mvvm.lifecycle.wrap

import androidx.lifecycle.LifecycleOwner
import com.wongki.framework.mvvm.lifecycle.LiveDataKey
import com.wongki.framework.mvvm.lifecycle.LiveDataViewModelDslMarker
import com.wongki.framework.mvvm.lifecycle.exception.AttachedException
import com.wongki.framework.mvvm.lifecycle.exception.NoAttachException
import com.wongki.framework.mvvm.lifecycle.wrap.event.*


/**
 * @author  wangqi
 * date:    2019/7/4
 * email:   wangqi7676@163.com
 * desc:    .
 */
interface IEventLiveDataViewModel {

    val mLiveDataWrappers: HashMap<LiveDataKey, EventLiveData<*>?>


    fun <T : Any> EventValueKeyBuilder<T>.transformKeyBuilderFunction(): EventValueKeyBuilder<T>.() -> Unit =
        {
            this.kClass = this@transformKeyBuilderFunction.kClass
            this.key = this@transformKeyBuilderFunction.key
            this.type = this@transformKeyBuilderFunction.type
        }

    /**
     * 获取key
     */
    private fun <T : Any> getKey(init: EventValueKeyBuilder<T>. () -> Unit): EventValueKey {
        val builder = EventValueKeyBuilder<T>()
        builder.init()
        builder.checkKey()
        return builder.buildKey()
    }

    /**
     * 生成对应的LiveData
     */
    @Suppress("UNCHECKED_CAST")
    fun <T : Any> attachWrapper(init: LiveDataWrapperBuilder<T>.() -> Unit): EventLiveData<T> {
        val builder =
            LiveDataWrapperBuilder<T>()
        builder.type = EventValueType.Normal
        builder.init()
        val key = getKey<T>(builder.transformKeyBuilderFunction())
        if (!mLiveDataWrappers.containsKey(key)) {
            mLiveDataWrappers[key] = builder.build() as EventLiveData<*>
        } else {
            throw AttachedException(key)
        }
        return mLiveDataWrappers[key] as EventLiveData<T>
    }

    /**
     * 生成对应的LiveData
     */
    @Suppress("UNCHECKED_CAST")
    fun <T : Any> attachWrapperForArrayList(
        init: LiveDataWrapperArrayListBuilder<T>.() -> Unit
    ): EventLiveData<ArrayList<T>> {
        val builder =
            LiveDataWrapperArrayListBuilder<T>()
        builder.type = EventValueType.ArrayList
        builder.init()
        val key = getKey(
            builder.transformKeyBuilderFunction()
        )

        if (!mLiveDataWrappers.containsKey(key)) {
            mLiveDataWrappers[key] = builder.build() as EventLiveData<*>
        } else {
            throw AttachedException(key)
        }
        return mLiveDataWrappers[key] as EventLiveData<ArrayList<T>>
    }

    /**
     * 获取子live data
     */
    @Suppress("UNCHECKED_CAST")
    private fun <T : Any> getLiveDataWrapper(init: EventValueKeyBuilder<T>.() -> Unit): EventLiveData<T> {
        val builder = EventValueKeyBuilder<T>()
        builder.type = EventValueType.Normal
        builder.init()
        val key = getKey(builder.transformKeyBuilderFunction())
        if (!mLiveDataWrappers.containsKey(key)) {
            throw NoAttachException(key)
        }
        return mLiveDataWrappers[key] as EventLiveData<T>
    }

    /**
     * 获取子live data
     */
    @Suppress("UNCHECKED_CAST")
    private fun <T : Any> getLiveDataWrapperForArrayList(init: EventValueKeyBuilder<T>.() -> Unit): EventLiveData<ArrayList<T>> {
        val builder = EventValueKeyBuilder<T>()
        builder.type = EventValueType.ArrayList
        builder.init()
        val key = getKey(builder.transformKeyBuilderFunction())
        if (!mLiveDataWrappers.containsKey(key)) {
            throw NoAttachException(key)
        }
        return mLiveDataWrappers[key] as EventLiveData<ArrayList<T>>
    }


    /**
     * 获取key
     */
    fun <T : Any> setWrapperValue(init: EventLiveDataSetterBuilder<T>.() -> Unit) {
        val builder =
            EventLiveDataSetterBuilder<T>()
        builder.type = EventValueType.Normal
        builder.init()
        getLiveDataWrapper<T>(builder.transformKeyBuilderFunction()).value =
            builder.value
    }

    /**
     * 获取key
     */
    fun <T : Any> setWrapperArrayListValue(init: EventLiveDataArrayListSetterBuilder<T>.() -> Unit) {
        val builder =
            EventLiveDataArrayListSetterBuilder<T>()
        builder.type = EventValueType.ArrayList
        builder.init()
        getLiveDataWrapperForArrayList<T>(builder.transformKeyBuilderFunction()).setValueForArrayList<T>(
            builder.value
        )
    }

    /**
     * 获取值
     */
    fun <T : Any> getWrapperValue(init: EventLiveDataGetterBuilder<T>.() -> Unit): EventValue<T>? {
        val builder =
            EventLiveDataGetterBuilder<T>()
        builder.type = EventValueType.Normal
        builder.init()
        return getLiveDataWrapper<T>(builder.transformKeyBuilderFunction()).value
    }

    /**
     * 获取值
     */
    fun <T : Any> getWrapperArrayListValue(init: EventLiveDataArrayListGetterBuilder<T>.() -> Unit): EventValue<ArrayList<T>>? {
        val builder =
            EventLiveDataArrayListGetterBuilder<T>()
        builder.type = EventValueType.ArrayList
        builder.init()
        return getLiveDataWrapperForArrayList<T>(builder.transformKeyBuilderFunction()).value
    }


    @LiveDataViewModelDslMarker
    class LiveDataWrapperBuilder<T : Any> : EventValueKeyBuilder<T>() {
        lateinit var owner: LifecycleOwner
        private var initLiveDataWrapper: (EventLiveDataObserveBuilder<T>.() -> Unit)? = null

        fun observe(initLiveDataWrapper: EventLiveDataObserveBuilder<T>.() -> Unit) {
            this.initLiveDataWrapper = initLiveDataWrapper
        }

        fun build(): EventLiveData<T> {
            val mutableLiveData =
                EventLiveData<T>()
            initLiveDataWrapper?.apply {
                mutableLiveData.observe(this)
            }
            return mutableLiveData
        }
    }

    @LiveDataViewModelDslMarker
    class LiveDataWrapperArrayListBuilder<T : Any> : EventValueKeyBuilder<T>() {
        private lateinit var initLiveDataWrapper: EventLiveDataObserveBuilder<ArrayList<T>>.() -> Unit

        fun observe(initLiveDataWrapper: EventLiveDataObserveBuilder<ArrayList<T>>.() -> Unit) {
            this.initLiveDataWrapper = initLiveDataWrapper
        }

        fun build(): EventLiveData<ArrayList<T>> {
            val mutableLiveData =
                EventLiveData<ArrayList<T>>()
            mutableLiveData.observe(initLiveDataWrapper)
            return mutableLiveData
        }
    }
}