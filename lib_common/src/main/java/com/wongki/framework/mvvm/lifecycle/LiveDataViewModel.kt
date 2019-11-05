package com.wongki.framework.mvvm.lifecycle

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.wongki.framework.mvvm.lifecycle.exception.AttachedException
import com.wongki.framework.mvvm.lifecycle.exception.NoAttachException

/**
 * @author  wangqi
 * date:    2019-11-05
 * email:   wangqi7676@163.com
 * desc:    .
 */
@LiveDataViewModelDslMarker
class LiveDataViewModel : ILiveDataViewModel {
    val TAG = javaClass.simpleName
    override val mLiveDatas: HashMap<Key, MutableLiveData<*>?> = HashMap()

    @Suppress("UNCHECKED_CAST")
    inline fun <reified T : Any> attachLiveData(init: LiveDataBuilder<T>.() -> Unit): MutableLiveData<T> {
        val liveDataBuilder = LiveDataBuilder<T>()
        liveDataBuilder.init()
        val keyBuilder = liveDataBuilder.key
        val key = getKey<T> { keyBuilder }
        if (!mLiveDatas.containsKey(key)) {
            mLiveDatas[key] = liveDataBuilder.build()
            Log.d(TAG, "")
        } else {
            throw AttachedException(key)
        }
        return mLiveDatas[key] as MutableLiveData<T>
    }


    /**
     * 获取子live data
     */
    @Suppress("UNCHECKED_CAST")
    inline fun <reified T : Any> getLiveData(initKey: String): MutableLiveData<T> {
        val key = getKey<T> { initKey }
        if (!mLiveDatas.containsKey(key)) {
            throw NoAttachException(key)
        }
        return mLiveDatas[key] as MutableLiveData<T>
    }

    /**
     * 获取key
     */
    inline fun <reified T : Any> getKey(init: () -> String): Key {
        val keyBuilder = KeyBuilder()
        val className = T::class.java.name
        val initKey = init.invoke()
        keyBuilder.key = "$className-${initKey}"
        return keyBuilder.buildKey()
    }

    /**
     * 获取key
     */
    inline fun <reified T : Any> setValue(init: LiveDataSetterValueBuilder<T>.() -> Unit) {
        val liveDataSetterValueBuilder = LiveDataSetterValueBuilder<T>()
        liveDataSetterValueBuilder.init()
        val initKey = liveDataSetterValueBuilder.key
        getLiveData<T>(initKey).value = liveDataSetterValueBuilder.value
    }

    /**
     * 获取key
     */
    inline fun <reified T : Any> getValue(init: LiveDataGetterValueBuilder<T>.() -> Unit): T? {
        val liveDataGetterValueBuilder = LiveDataGetterValueBuilder<T>()
        liveDataGetterValueBuilder.init()
        val initKey = liveDataGetterValueBuilder.key
        return getLiveData<T>(initKey).value
    }

    class LiveDataBuilder<T> : KeyBuilder() {
        private lateinit var owner: LifecycleOwner
        private var onChange: (T?.() -> Unit)? = null

        fun observe(onChange: T?.() -> Unit) {
            this.onChange = onChange
        }

        fun build(): MutableLiveData<T> {
            val mutableLiveData = MutableLiveData<T>()
            mutableLiveData.observe(owner,
                Observer<T> { t -> this@LiveDataBuilder.onChange?.invoke(t) })
            return mutableLiveData
        }
    }

    class LiveDataSetterValueBuilder<T> : KeyBuilder() {
        var value: T? = null
    }

    class LiveDataGetterValueBuilder<T> : KeyBuilder()

}