package com.wongki.framework.mvvm.lifecycle.wrap

import androidx.lifecycle.ViewModel
import com.wongki.framework.http.retrofit.core.RetrofitServiceCore
import com.wongki.framework.mvvm.action.EventAction
import com.wongki.framework.mvvm.lifecycle.LiveDataViewModelDslMarker
import com.wongki.framework.mvvm.lifecycle.exception.AttachedException
import com.wongki.framework.mvvm.lifecycle.exception.NoAttachException
import com.wongki.framework.mvvm.remote.retrofit.IRetrofitViewModel
import kotlin.reflect.KClass

/**
 * @author  wangqi
 * date:    2019/7/4
 * email:   wangqi7676@163.com
 * desc:    .
 */

@LiveDataViewModelDslMarker
abstract class AbsLiveDataWrapperViewModel : ViewModel(), IRetrofitViewModel, ILiveDataWrapperViewModel {

    override val mLiveDataWrappers: HashMap<WrapperKey, LiveDataWrapper<*>?> = HashMap()

    inline fun <reified T : Any> getWrapperKey(type: DataWrapperType) =
        WrapperKey().apply { key = "${type.name}<${T::class.java.name}>" }

    /**
     * 生成对应的LiveData
     */
    @Suppress("UNCHECKED_CAST")
    inline fun <reified T : Any> attachLiveDataWrapper(init: LiveDataWrapper<T>.() -> Unit): LiveDataWrapper<T> {

        val clazz = T::class
        val key = getWrapperKey<T>(DataWrapperType.Normal)
        if (!mLiveDataWrappers.containsKey(key)) {
            mLiveDataWrappers[key] = LiveDataWrapper<T>() as LiveDataWrapper<*>
        }else{
            throw AttachedException(key)
        }
        return (mLiveDataWrappers[key] as LiveDataWrapper<T>).apply(init)
    }

    /**
     * 生成对应的LiveData
     */
    @Suppress("UNCHECKED_CAST")
    inline fun <reified T : Any> attachLiveDataWrapperForArrayList(init: LiveDataWrapper<ArrayList<T>>.() -> Unit): LiveDataWrapper<ArrayList<T>> {
        val key = getWrapperKey<T>(DataWrapperType.ArrayList)
        if (!mLiveDataWrappers.containsKey(key)) {
            mLiveDataWrappers[key] = LiveDataWrapper<ArrayList<T>>() as LiveDataWrapper<*>
        }else{
            throw AttachedException(key)
        }
        return (mLiveDataWrappers[key] as LiveDataWrapper<ArrayList<T>>).apply(init)
    }

    /**
     * 获取子live data
     */
    @Suppress("UNCHECKED_CAST")
    inline fun <reified T : Any> getLiveDataWrapper(clazz: KClass<T>): LiveDataWrapper<T> {
        val key = getWrapperKey<T>(DataWrapperType.Normal)
        if (!mLiveDataWrappers.containsKey(key)) {
            throw NoAttachException(key)
        }
        return mLiveDataWrappers[key] as LiveDataWrapper<T>
    }

    /**
     * 获取子live data
     */
    @Suppress("UNCHECKED_CAST")
    inline fun <reified T : Any> getLiveDataWrapperForArrayList(): LiveDataWrapper<ArrayList<T>> {
        val key = getWrapperKey<T>(DataWrapperType.ArrayList)
        if (!mLiveDataWrappers.containsKey(key)) {
            throw NoAttachException(key)
        }
        return mLiveDataWrappers[key] as LiveDataWrapper<ArrayList<T>>
    }

    @Suppress("UNCHECKED_CAST")
    inline fun <API, reified RESPONSE_DATA : Any> RetrofitServiceCore<API>.RequesterBuilder<RESPONSE_DATA>.observeLiveDataWrapper(
        crossinline success: RESPONSE_DATA?.() -> Unit = {}
    ): RetrofitServiceCore<API>.RetrofitRequester<RESPONSE_DATA> {


        return observer {

            onStart {
                // 通知开始
                getLiveDataWrapper(RESPONSE_DATA::class).setValueForAction(EventAction.START)
            }

            onCancel {
                // 通知取消
                getLiveDataWrapper(RESPONSE_DATA::class).setValueForAction(EventAction.CANCEL)
            }

            onSuccess {
                val result = this
                success(result)
                // 通知成功

                getLiveDataWrapper(RESPONSE_DATA::class).setValue(EventAction.SUCCESS) {
                    if (result != null) {
                        this.data = result
                    }
                }
            }

            onFailed { code, message ->
                // 通知失败
                val dataWrapper = getLiveDataWrapper(RESPONSE_DATA::class).setValue(EventAction.FAILED) {
                    this.code = code
                    this.message = message
                }

                return@onFailed dataWrapper.errorProcessed
            }
        }.apply {
            lifecycleObserver { this@AbsLiveDataWrapperViewModel }
        }

    }

    @Suppress("UNCHECKED_CAST")
    inline fun <API, reified T : Any> RetrofitServiceCore<API>.RequesterBuilder<ArrayList<T>>.observeLiveDataWrapperForArrayList(
        crossinline success: ArrayList<T>?.() -> Unit = {}
    ): RetrofitServiceCore<API>.RetrofitRequester<ArrayList<T>> {

        return observer {


            onStart {
                // 通知开始
                getLiveDataWrapperForArrayList<T>().setValueForArrayListForAction<T>(EventAction.START)
            }

            onCancel {
                // 通知取消
                getLiveDataWrapperForArrayList<T>().setValueForArrayListForAction<T>(EventAction.CANCEL)
            }

            onSuccess {
                val result = this
                success(this)
                // 通知成功
                getLiveDataWrapperForArrayList<T>().setValueForArrayList<T>(EventAction.SUCCESS) {
                    if (result != null) {
                        this.data = result
                    }
                }

            }

            onFailed { code, message ->
                // 通知失败
                val dataWrapper =
                    getLiveDataWrapperForArrayList<T>().setValueForArrayList<T>(EventAction.SUCCESS) {
                        this.code = code
                        this.message = message
                    }

                return@onFailed dataWrapper.errorProcessed
            }

        }.apply {
            lifecycleObserver { this@AbsLiveDataWrapperViewModel }
        }

    }

    /**
     * 用于一次点击，多次网络请求
     * @param setStartAction 是否发送Start事件  只有多次网络请求的第一次是需要发送Start事件
     * @param finalAttachedKClass 最终订阅的kClass，与attach一一对应 [attachLiveDataWrapper]
     * @param success 成功的回调
     * @return 请求器[RetrofitServiceCore.RetrofitRequester]
     */
    @Suppress("UNCHECKED_CAST")
    inline fun <reified T : Any, SERVICE, reified RESPONSE_DATA : Any> RetrofitServiceCore<SERVICE>.RequesterBuilder<RESPONSE_DATA>.observeLiveDataWrapperForMulti(
        setStartAction: Boolean,
        finalAttachedKClass: KClass<T>,
        crossinline success: RESPONSE_DATA?.() -> Unit
    ): RetrofitServiceCore<SERVICE>.RetrofitRequester<RESPONSE_DATA> {
        return observer {

            onStart {
                if (setStartAction) {
                    getLiveDataWrapper(finalAttachedKClass).setValueForAction(EventAction.START)
                }
            }

            onCancel {
                getLiveDataWrapper(finalAttachedKClass).setValueForAction(EventAction.CANCEL)
            }

            onSuccess {
                success(this)
            }

            onFailed { code, message ->
                val dataWrapper = getLiveDataWrapper(finalAttachedKClass).setValue(EventAction.FAILED) {
                    this.code = code
                    this.message = message
                }
                return@onFailed dataWrapper.errorProcessed
            }

        }.apply {
            lifecycleObserver { this@AbsLiveDataWrapperViewModel }
        }
    }

    override fun onCleared() {
        onDestroy()
    }
}


