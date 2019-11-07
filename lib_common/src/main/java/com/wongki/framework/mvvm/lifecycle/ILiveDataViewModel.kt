package com.wongki.framework.mvvm.lifecycle

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.wongki.framework.mvvm.lifecycle.exception.AttachedException
import com.wongki.framework.mvvm.lifecycle.exception.NoAttachException


/**
 * @author  wangqi
 * date:    2019/7/4
 * email:   wangqi7676@163.com
 * desc:    .
 */
interface ILiveDataViewModel:ILifecycleOwnerWrapper{

    val mLiveDatas: HashMap<LiveDataKey, MutableLiveData<*>?>

    fun <T : Any> LiveDataKeyBuilder<T>.transformKeyBuilderFunction(): LiveDataKeyBuilder<T>.() -> Unit = {
        this.key = this@transformKeyBuilderFunction.key
        this.kClass = this@transformKeyBuilderFunction.kClass
    }
    /**
     * 获取key
     */
    private fun <T : Any> getKey(init: LiveDataKeyBuilder<T>. () -> Unit): LiveDataKey {
        val keyBuilder = LiveDataKeyBuilder<T>()
        keyBuilder.init()
        return keyBuilder.buildKey()
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> attach(init: LiveDataBuilder<T>.() -> Unit): MutableLiveData<T> {
        val liveDataBuilder = LiveDataBuilder<T>()
        liveDataBuilder.init()
        val key = getKey<T>(liveDataBuilder.transformKeyBuilderFunction())

        if (!mLiveDatas.containsKey(key)) {
            mLiveDatas[key] = liveDataBuilder.build()
            Log.d(this::class.java.simpleName, "")
        } else {
            throw AttachedException(key)
        }
        return mLiveDatas[key] as MutableLiveData<T>
    }


    /**
     * 获取子live data
     */
    @Suppress("UNCHECKED_CAST")
    private fun <T : Any> getLiveData(init: LiveDataKeyBuilder<T>.() -> Unit): MutableLiveData<T> {
        val key = getKey<T>(init)
        if (!mLiveDatas.containsKey(key)) {
            throw NoAttachException(key)
        }
        return mLiveDatas[key] as MutableLiveData<T>
    }



    /**
     * 设置数据
     */
    fun <T : Any> setValue(init: LiveDataSetterValueBuilder<T>.() -> Unit) {
        val liveDataSetterValueBuilder = LiveDataSetterValueBuilder<T>()
        liveDataSetterValueBuilder.init()
        liveDataSetterValueBuilder.check()
        getLiveData<T>(liveDataSetterValueBuilder.transformKeyBuilderFunction()).value = liveDataSetterValueBuilder.value
    }

    /**
     * 获取key
     */
    fun <T : Any> getValue(init: LiveDataGetterValueBuilder<T>.() -> Unit): T? {
        val liveDataGetterValueBuilder = LiveDataGetterValueBuilder<T>()
        liveDataGetterValueBuilder.init()
        return getLiveData<T>(liveDataGetterValueBuilder.transformKeyBuilderFunction()).value
    }




    @LiveDataViewModelDslMarker
    class LiveDataBuilder<T : Any> : LiveDataKeyBuilder<T>() {

        private  lateinit var observerBuilder: ObserveBuilder<T>
        fun observe(init: ObserveBuilder<T>.() -> Unit) {
            val builder = ObserveBuilder<T>()
            builder.init()
            observerBuilder = builder
        }

        fun build(): MutableLiveData<T> {
            val mutableLiveData = MutableLiveData<T>()
            val builder = observerBuilder
            mutableLiveData.observe(builder.owner,
                Observer<T> { t -> builder.onChange?.invoke(t) })
            return mutableLiveData
        }
    }



    @LiveDataViewModelDslMarker
    class ObserveBuilder<T : Any>  {
        lateinit var owner: LifecycleOwner
        internal var onChange: (T?.() -> Unit)? = null

        fun onChange(onChange: T?.() -> Unit) {
            this.onChange = onChange
        }
    }


}