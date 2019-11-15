package com.wongki.framework.mvvm.lifecycle.wrap

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import com.wongki.framework.mvvm.lifecycle.ILifecycleOwnerWrapper
import com.wongki.framework.mvvm.lifecycle.IViewModel
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
interface IEventLiveDataViewModel : ILifecycleOwnerWrapper, IViewModel {

    val mEventLiveDatas: HashMap<LiveDataKey, EventLiveData<*>?>

    /**
     * 生成对应的LiveData
     * owner默认使用创建LiveData时的LifecycleOwner
     */
    @Suppress("UNCHECKED_CAST")
    fun <T : Any> attachEventObserve(builder: EventLiveDataBuilder<T>): EventLiveData<T> {
        val key = builder.buildKey(getKeyPrefix())
        if (!mEventLiveDatas.containsKey(key)) {
            mEventLiveDatas[key] = builder.build() as EventLiveData<*>
            Log.d(this::class.java.simpleName, "[attachEvent] key:$key")
        }
        val liveData = mEventLiveDatas[key] as EventLiveData<T>

        with(builder) {
            defaultOwner = getLifecycleOwner()
            liveData.observe()
            Log.d(this::class.java.simpleName, "[observeEvent] key:$key")
        }
        return liveData
    }

    /**
     * 生成对应的LiveData
     * owner默认使用创建LiveData时的LifecycleOwner
     */
    @Suppress("UNCHECKED_CAST")
    fun <ITEM : Any> attachEventObserveForArrayList(builder: EventLiveDataArrayListBuilder<ITEM>): EventLiveData<ArrayList<ITEM>> {
        val key = builder.buildKey(getKeyPrefix())
        if (!mEventLiveDatas.containsKey(key)) {
            mEventLiveDatas[key] = builder.build() as EventLiveData<*>
            Log.d(this::class.java.simpleName, "[attachEventForArrayList] key:$key")
        }
        val liveData = mEventLiveDatas[key] as EventLiveData<ArrayList<ITEM>>
        with(builder) {
            defaultOwner = getLifecycleOwner()
            liveData.observe()
            Log.d(this::class.java.simpleName, "[observeEventForArrayList] key:$key")
        }
        return liveData
    }

    /**
     * 获取子live data
     */
    @Suppress("UNCHECKED_CAST")
    private fun <T : Any> getEventLiveData(builder: EventValueKeyBuilder): EventLiveData<T> {
        builder.type = EventValueType.Normal
        val key = builder.buildKey(getKeyPrefix())
        if (!mEventLiveDatas.containsKey(key)) {
            throw NoAttachException(key)
        }
        return mEventLiveDatas[key] as EventLiveData<T>
    }

    /**
     * 获取子live data
     */
    @Suppress("UNCHECKED_CAST")
    private fun <ITEM : Any> getEventLiveDataForArrayList(builder: EventValueKeyBuilder): EventLiveData<ArrayList<ITEM>> {
        builder.type = EventValueType.ArrayList
        val key = builder.buildKey(getKeyPrefix())
        if (!mEventLiveDatas.containsKey(key)) {
            throw NoAttachException(key)
        }
        return mEventLiveDatas[key] as EventLiveData<ArrayList<ITEM>>
    }


    /**
     * 获取key
     */
    fun <T : Any> setEventValue(builder: EventLiveDataSetterBuilder<T>) {
        getEventLiveData<T>(builder.keyBuilder).value = builder.value
        Log.d(this::class.java.simpleName, "[setEventValue] key:${builder.buildKey(getKeyPrefix())}, value:${builder.value}")
    }

    /**
     * 获取key
     */
    fun <ITEM : Any> setEventValueForArrayList(builder: EventLiveDataArrayListSetterBuilder<ITEM>) {
        getEventLiveDataForArrayList<ITEM>(builder.keyBuilder).setValueForArrayList<ITEM>(
            builder.value
        )
        Log.d(this::class.java.simpleName, "[setEventValueForArrayList] key:${builder.buildKey(getKeyPrefix())}, value:${builder.value}")
    }

    /**
     * 获取值
     */
    fun <T : Any> getEventValue(builder: EventLiveDataGetterBuilder<T>): EventValue<T>? {
        return getEventLiveData<T>(builder.keyBuilder).value
    }

    /**
     * 获取值
     */
    fun <ITEM : Any> getEventValueForArrayList(builder: EventLiveDataArrayListGetterBuilder<ITEM>): EventValue<ArrayList<ITEM>>? {
        return getEventLiveDataForArrayList<ITEM>(builder.keyBuilder).value
    }

    @LiveDataViewModelDslMarker
    class EventLiveDataBuilder<T : Any> : DslEventValueKeyBuilder() {
        internal var defaultOwner: LifecycleOwner? = null
        private var builder: EventLiveDataObserveBuilder<T>?=null

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
            val builder = this@EventLiveDataBuilder.builder ?: return
            val lifecycleOwner = builder.owner ?: this@EventLiveDataBuilder.defaultOwner ?: throw NoSetLifecycleOwnerException()
            observe(lifecycleOwner,builder)
        }
    }

    @LiveDataViewModelDslMarker
    class EventLiveDataArrayListBuilder<ITEM : Any> : DslEventValueKeyBuilder() {
        internal var defaultOwner: LifecycleOwner? = null
        private var builder: EventLiveDataObserveBuilder<ArrayList<ITEM>>?=null

        init {
            type = EventValueType.ArrayList
        }

        @LiveDataViewModelDslMarker
        fun observe(init: EventLiveDataObserveBuilder<ArrayList<ITEM>>.() -> Unit) {
            val builder = EventLiveDataObserveBuilder<ArrayList<ITEM>>()
            builder.init()
            this.builder = builder
        }

        fun build(): EventLiveData<ArrayList<ITEM>> {
            return EventLiveData()
        }

        @Suppress("UNCHECKED_CAST")
        internal fun EventLiveData<ArrayList<ITEM>>.observe() {
            val builder = this@EventLiveDataArrayListBuilder.builder ?: return
            val lifecycleOwner = builder.owner ?: this@EventLiveDataArrayListBuilder.defaultOwner ?: throw NoSetLifecycleOwnerException()
            observe(lifecycleOwner,builder)
        }
    }
}