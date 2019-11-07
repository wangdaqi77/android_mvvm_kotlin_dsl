package com.wongki.framework.mvvm.lifecycle

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.wongki.framework.mvvm.lifecycle.exception.NoAttachException
import com.wongki.framework.mvvm.lifecycle.exception.NoSetLifecycleOwnerException


/**
 * @author  wangqi
 * date:    2019/7/4
 * email:   wangqi7676@163.com
 * desc:    .
 */
interface ILiveDataViewModel : ILifecycleOwnerWrapper {

    val mLiveDatas: HashMap<LiveDataKey, MutableLiveData<*>?>


    @Suppress("UNCHECKED_CAST")
    fun <T : Any> attachObserve(init: LiveDataBuilder<T>.() -> Unit): MutableLiveData<T> {
        val builder = LiveDataBuilder<T>()
        builder.init()
        val key = builder.getKey()
        if (!mLiveDatas.containsKey(key)) {
            mLiveDatas[key] = builder.build()
            Log.d(this::class.java.simpleName, "")
        }

        val liveData = mLiveDatas[key] as MutableLiveData<T>
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
    private fun <T : Any> getLiveData(builder: LiveDataKeyBuilder<T>): MutableLiveData<T> {
        val key = builder.buildKey()
        if (!mLiveDatas.containsKey(key)) {
            throw NoAttachException(key)
        }
        return mLiveDatas[key] as MutableLiveData<T>
    }


    /**
     * 设置数据
     */
    fun <T : Any> setValue(init: LiveDataSetterBuilder<T>.() -> Unit) {
        val liveDataSetterValueBuilder = LiveDataSetterBuilder<T>()
        liveDataSetterValueBuilder.init()
        liveDataSetterValueBuilder.check()
        getLiveData<T>(liveDataSetterValueBuilder.keyBuilder).value =
            liveDataSetterValueBuilder.value
    }

    /**
     * 获取key
     */
    fun <T : Any> getValue(init: LiveDataGetterBuilder<T>.() -> Unit): T? {
        val liveDataGetterValueBuilder = LiveDataGetterBuilder<T>()
        liveDataGetterValueBuilder.init()
        return getLiveData<T>(liveDataGetterValueBuilder.keyBuilder).value
    }


    @LiveDataViewModelDslMarker
    class LiveDataBuilder<T : Any> : LiveDataKeyBuilderWrapper<T>() {
        internal var defaultOwner: LifecycleOwner? = null
        private lateinit var observerBuilder: ObserveBuilder<T>
        fun observe(init: ObserveBuilder<T>.() -> Unit) {
            val builder = ObserveBuilder<T>()
            builder.init()
            observerBuilder = builder
        }

        fun build(): MutableLiveData<T> {
            return MutableLiveData()
        }

        internal fun MutableLiveData<T>.observe() {
            val builder = this@LiveDataBuilder.observerBuilder
            val lifecycleOwner = builder.owner ?: defaultOwner ?: throw NoSetLifecycleOwnerException(getKey())
            observe(lifecycleOwner,
                Observer<T> { t -> builder.onChange?.invoke(t) })
        }
    }


    @LiveDataViewModelDslMarker
    class ObserveBuilder<T : Any> {
        var owner: LifecycleOwner? = null
        internal var onChange: (T?.() -> Unit)? = null

        fun onChange(onChange: T?.() -> Unit) {
            this.onChange = onChange
        }
    }


}