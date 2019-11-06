package com.wongki.framework.mvvm.lifecycle.wrap

import androidx.lifecycle.LifecycleOwner
import com.wongki.framework.mvvm.lifecycle.LiveDataKey
import com.wongki.framework.mvvm.lifecycle.LiveDataViewModelDslMarker
import com.wongki.framework.mvvm.lifecycle.exception.AttachedException
import com.wongki.framework.mvvm.lifecycle.exception.NoAttachException


/**
 * @author  wangqi
 * date:    2019/7/4
 * email:   wangqi7676@163.com
 * desc:    .
 */
interface ILiveDataWrapperViewModel {

    val mLiveDataWrappers: HashMap<LiveDataKey, LiveDataWrapper<*>?>


    fun <T : Any> WrapperKeyBuilder<T>.transformKeyBuilderFunction(): WrapperKeyBuilder<T>.() -> Unit =
        {
            this.kClass = this@transformKeyBuilderFunction.kClass
            this.key = this@transformKeyBuilderFunction.key
            this.type = this@transformKeyBuilderFunction.type
        }

    /**
     * 获取key
     */
    private fun <T : Any> getKey(init: WrapperKeyBuilder<T>. () -> Unit): WrapperKey {
        val builder = WrapperKeyBuilder<T>()
        builder.init()
        builder.checkKey()
        return builder.buildKey()
    }

    /**
     * 生成对应的LiveData
     */
    @Suppress("UNCHECKED_CAST")
    fun <T : Any> attachWrapper(init: LiveDataWrapperBuilder<T>.() -> Unit): LiveDataWrapper<T> {
        val builder = LiveDataWrapperBuilder<T>()
        builder.type = ValueWrapperType.Normal
        builder.init()
        val key = getKey<T>(builder.transformKeyBuilderFunction())
        if (!mLiveDataWrappers.containsKey(key)) {
            mLiveDataWrappers[key] = builder.build() as LiveDataWrapper<*>
        } else {
            throw AttachedException(key)
        }
        return mLiveDataWrappers[key] as LiveDataWrapper<T>
    }

    /**
     * 生成对应的LiveData
     */
    @Suppress("UNCHECKED_CAST")
    fun <T : Any> attachWrapperForArrayList(
        init: LiveDataWrapperArrayListBuilder<T>.() -> Unit
    ): LiveDataWrapper<ArrayList<T>> {
        val builder = LiveDataWrapperArrayListBuilder<T>()
        builder.type = ValueWrapperType.ArrayList
        builder.init()
        val key = getKey(
            builder.transformKeyBuilderFunction()
        )

        if (!mLiveDataWrappers.containsKey(key)) {
            mLiveDataWrappers[key] = builder.build() as LiveDataWrapper<*>
        } else {
            throw AttachedException(key)
        }
        return mLiveDataWrappers[key] as LiveDataWrapper<ArrayList<T>>
    }

    /**
     * 获取子live data
     */
    @Suppress("UNCHECKED_CAST")
    private fun <T : Any> getLiveDataWrapper(init: WrapperKeyBuilder<T>.() -> Unit): LiveDataWrapper<T> {
        val builder = WrapperKeyBuilder<T>()
        builder.type = ValueWrapperType.Normal
        builder.init()
        val key = getKey(builder.transformKeyBuilderFunction())
        if (!mLiveDataWrappers.containsKey(key)) {
            throw NoAttachException(key)
        }
        return mLiveDataWrappers[key] as LiveDataWrapper<T>
    }

    /**
     * 获取子live data
     */
    @Suppress("UNCHECKED_CAST")
    private fun <T : Any> getLiveDataWrapperForArrayList(init: WrapperKeyBuilder<T>.() -> Unit): LiveDataWrapper<ArrayList<T>> {
        val builder = WrapperKeyBuilder<T>()
        builder.type = ValueWrapperType.ArrayList
        builder.init()
        val key = getKey(builder.transformKeyBuilderFunction())
        if (!mLiveDataWrappers.containsKey(key)) {
            throw NoAttachException(key)
        }
        return mLiveDataWrappers[key] as LiveDataWrapper<ArrayList<T>>
    }


    /**
     * 获取key
     */
    fun <T : Any> setWrapperValue(init: LiveDataWrapperSetterValueBuilder<T>.() -> Unit) {
        val builder = LiveDataWrapperSetterValueBuilder<T>()
        builder.type = ValueWrapperType.Normal
        builder.init()
        getLiveDataWrapper<T>(builder.transformKeyBuilderFunction()).value =
            builder.value
    }

    /**
     * 获取key
     */
    fun <T : Any> setWrapperArrayListValue(init: LiveDataWrapperArrayListSetterValueBuilder<T>.() -> Unit) {
        val builder = LiveDataWrapperArrayListSetterValueBuilder<T>()
        builder.type = ValueWrapperType.ArrayList
        builder.init()
        getLiveDataWrapperForArrayList<T>(builder.transformKeyBuilderFunction()).setValueForArrayList<T>(
            builder.value
        )
    }

    /**
     * 获取值
     */
    fun <T : Any> getWrapperValue(init: LiveDataWrapperGetterValueBuilder<T>.() -> Unit): ValueWrapper<T>? {
        val builder = LiveDataWrapperGetterValueBuilder<T>()
        builder.type = ValueWrapperType.Normal
        builder.init()
        return getLiveDataWrapper<T>(builder.transformKeyBuilderFunction()).value
    }

    /**
     * 获取值
     */
    fun <T : Any> getWrapperArrayListValue(init: LiveDataWrapperArrayListGetterValueBuilder<T>.() -> Unit): ValueWrapper<ArrayList<T>>? {
        val builder = LiveDataWrapperArrayListGetterValueBuilder<T>()
        builder.type = ValueWrapperType.ArrayList
        builder.init()
        return getLiveDataWrapperForArrayList<T>(builder.transformKeyBuilderFunction()).value
    }


    @LiveDataViewModelDslMarker
    class LiveDataWrapperBuilder<T : Any> : WrapperKeyBuilder<T>() {
        lateinit var owner: LifecycleOwner
        private var initLiveDataWrapper: (LiveDataWrapperObserveBuilder<T>.() -> Unit)? = null

        fun observe(initLiveDataWrapper: LiveDataWrapperObserveBuilder<T>.() -> Unit) {
            this.initLiveDataWrapper = initLiveDataWrapper
        }

        fun build(): LiveDataWrapper<T> {
            val mutableLiveData = LiveDataWrapper<T>()
            initLiveDataWrapper?.apply {
                mutableLiveData.observe(this)
            }
            return mutableLiveData
        }
    }

    @LiveDataViewModelDslMarker
    class LiveDataWrapperArrayListBuilder<T : Any> : WrapperKeyBuilder<T>() {
        private lateinit var initLiveDataWrapper: LiveDataWrapperObserveBuilder<ArrayList<T>>.() -> Unit

        fun observe(initLiveDataWrapper: LiveDataWrapperObserveBuilder<ArrayList<T>>.() -> Unit) {
            this.initLiveDataWrapper = initLiveDataWrapper
        }

        fun build(): LiveDataWrapper<ArrayList<T>> {
            val mutableLiveData = LiveDataWrapper<ArrayList<T>>()
            mutableLiveData.observe(initLiveDataWrapper)
            return mutableLiveData
        }
    }
}