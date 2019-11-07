package com.wongki.framework.mvvm.lifecycle.wrap

import androidx.lifecycle.LifecycleOwner
import com.wongki.framework.mvvm.lifecycle.ILifecycleOwnerWrapper
import com.wongki.framework.mvvm.lifecycle.LiveDataKey
import com.wongki.framework.mvvm.lifecycle.LiveDataViewModelDslMarker
import com.wongki.framework.mvvm.lifecycle.exception.NoAttachException
import com.wongki.framework.mvvm.lifecycle.exception.NoSetLifecycleOwnerException
import com.wongki.framework.mvvm.lifecycle.wrap.event.*


/**
 * @author  wangqi
 * date:    2019/7/4
 * email:   wangqi7676@163.com
 * desc:    .
 */
interface IEventLiveDataViewModel : ILifecycleOwnerWrapper {

    val mEventLiveDatas: HashMap<LiveDataKey, EventLiveData<*>?>

    /**
     * 生成对应的LiveData
     * owner默认使用创建LiveData时的LifecycleOwner
     */
    @Suppress("UNCHECKED_CAST")
    fun <T : Any> attachEventObserve(init: EventLiveDataBuilder<T>.() -> Unit): EventLiveData<T> {
        val builder = EventLiveDataBuilder<T>()
        builder.init()
        val key = builder.getKey()
        if (!mEventLiveDatas.containsKey(key)) {
            mEventLiveDatas[key] = builder.build() as EventLiveData<*>
        }
        val liveData = mEventLiveDatas[key] as EventLiveData<T>

        with(builder) {
            defaultOwner = getLifecycleOwner()
            liveData.observe()
        }
        return liveData
    }

    /**
     * 生成对应的LiveData
     * owner默认使用创建LiveData时的LifecycleOwner
     */
    @Suppress("UNCHECKED_CAST")
    fun <T : Any> attachEventObserveForArrayList(init: EventLiveDataArrayListBuilder<T>.() -> Unit): EventLiveData<ArrayList<T>> {
        val builder = EventLiveDataArrayListBuilder<T>()
        builder.init()
        val key = builder.getKey()
        if (!mEventLiveDatas.containsKey(key)) {
            mEventLiveDatas[key] = builder.build() as EventLiveData<*>
        }
        val liveData = mEventLiveDatas[key] as EventLiveData<ArrayList<T>>
        with(builder) {
            defaultOwner = getLifecycleOwner()
            liveData.observe()
        }
        return liveData
    }

    /**
     * 获取子live data
     */
    @Suppress("UNCHECKED_CAST")
    private fun <T : Any> getEventLiveData(builder: EventValueKeyBuilder<T>): EventLiveData<T> {
        builder.type = EventValueType.Normal
        val key = builder.buildKey()
        if (!mEventLiveDatas.containsKey(key)) {
            throw NoAttachException(key)
        }
        return mEventLiveDatas[key] as EventLiveData<T>
    }

    /**
     * 获取子live data
     */
    @Suppress("UNCHECKED_CAST")
    private fun <T : Any> getEventLiveDataForArrayList(builder: EventValueKeyBuilder<T>): EventLiveData<ArrayList<T>> {
        builder.type = EventValueType.ArrayList
        val key = builder.buildKey()
        if (!mEventLiveDatas.containsKey(key)) {
            throw NoAttachException(key)
        }
        return mEventLiveDatas[key] as EventLiveData<ArrayList<T>>
    }


    /**
     * 获取key
     */
    fun <T : Any> setEventValue(init: EventLiveDataSetterBuilder<T>.() -> Unit) {
        val builder = EventLiveDataSetterBuilder<T>()
        builder.init()
        getEventLiveData<T>(builder.keyBuilder).value = builder.value
    }

    /**
     * 获取key
     */
    fun <T : Any> setEventValueForArrayList(init: EventLiveDataArrayListSetterBuilder<T>.() -> Unit) {
        val builder = EventLiveDataArrayListSetterBuilder<T>()
        builder.init()
        getEventLiveDataForArrayList<T>(builder.keyBuilder).setValueForArrayList<T>(
            builder.value
        )
    }

    /**
     * 获取值
     */
    fun <T : Any> getEventValue(init: EventLiveDataGetterBuilder<T>.() -> Unit): EventValue<T>? {
        val builder = EventLiveDataGetterBuilder<T>()
        builder.init()
        return getEventLiveData<T>(builder.keyBuilder).value
    }

    /**
     * 获取值
     */
    fun <T : Any> getEventValueForArrayList(init: EventLiveDataArrayListGetterBuilder<T>.() -> Unit): EventValue<ArrayList<T>>? {
        val builder =
            EventLiveDataArrayListGetterBuilder<T>()
        builder.init()
        return getEventLiveDataForArrayList<T>(builder.keyBuilder).value
    }

    @LiveDataViewModelDslMarker
    class EventLiveDataBuilder<T : Any> : EventValueKeyBuilderWrapper<T>() {
        internal var defaultOwner: LifecycleOwner? = null
        private lateinit var builder: EventLiveDataObserveBuilder<T>

        init {
            type = EventValueType.Normal
        }

        fun observe(init: EventLiveDataObserveBuilder<T>.() -> Unit) {
            val builder = EventLiveDataObserveBuilder<T>()
            builder.init()
            this.builder = builder
        }

        fun build(): EventLiveData<T> {
            return EventLiveData()
        }

        @Suppress("UNCHECKED_CAST")
        internal fun EventLiveData<T>.observe() {
            val builder = this@EventLiveDataBuilder.builder
            val lifecycleOwner = builder.owner ?: this@EventLiveDataBuilder.defaultOwner ?: throw NoSetLifecycleOwnerException(this@EventLiveDataBuilder.getKey())
            observe(lifecycleOwner,builder)
        }
    }

    @LiveDataViewModelDslMarker
    class EventLiveDataArrayListBuilder<T : Any> : EventValueKeyBuilderWrapper<T>() {
        internal var defaultOwner: LifecycleOwner? = null
        private lateinit var builder: EventLiveDataObserveBuilder<ArrayList<T>>

        init {
            type = EventValueType.ArrayList
        }

        fun observe(init: EventLiveDataObserveBuilder<ArrayList<T>>.() -> Unit) {
            val builder = EventLiveDataObserveBuilder<ArrayList<T>>()
            builder.init()
            this.builder = builder
        }

        fun build(): EventLiveData<ArrayList<T>> {
            return EventLiveData()
        }

        @Suppress("UNCHECKED_CAST")
        internal fun EventLiveData<ArrayList<T>>.observe() {
            val builder = this@EventLiveDataArrayListBuilder.builder
            val lifecycleOwner = builder.owner ?: this@EventLiveDataArrayListBuilder.defaultOwner ?: throw NoSetLifecycleOwnerException(this@EventLiveDataArrayListBuilder.getKey())
            observe(lifecycleOwner,builder)
        }
    }
}