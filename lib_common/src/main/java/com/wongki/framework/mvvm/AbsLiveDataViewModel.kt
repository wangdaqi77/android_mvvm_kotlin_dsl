package com.wongki.framework.mvvm

import androidx.lifecycle.ViewModel
import com.wongki.framework.http.retrofit.core.RetrofitServiceCore
import com.wongki.framework.mvvm.action.EventAction
import com.wongki.framework.mvvm.lifecycle.DataType
import com.wongki.framework.mvvm.lifecycle.ILiveDataViewModel
import com.wongki.framework.mvvm.lifecycle.WrapLiveData
import com.wongki.framework.mvvm.remote.retrofit.IRetrofitViewModel
import java.lang.RuntimeException
import kotlin.reflect.KClass

/**
 * @author  wangqi
 * date:    2019/7/4
 * email:   wangqi7676@163.com
 * desc:    .
 */

abstract class AbsLiveDataViewModel : ViewModel(), IRetrofitViewModel, ILiveDataViewModel {

    override val mSystemLiveData: HashMap<String, WrapLiveData<*>?> = HashMap()


    inline fun <reified T : Any> getKey(type: DataType, clazz: KClass<T>) = "${type.name}<${clazz.java.name}>"

    /**
     * 生成子live data
     */
    @Suppress("UNCHECKED_CAST")
    inline fun <reified T : Any> fork(clazz: KClass<T>): WrapLiveData<T> {
        val key = getKey(DataType.Normal, clazz)
        if (!mSystemLiveData.containsKey(key)) {
            mSystemLiveData[key] = WrapLiveData<T>() as WrapLiveData<*>
        }
        return mSystemLiveData[key] as WrapLiveData<T>
    }

    /**
     * 生成子live data
     */
    @Suppress("UNCHECKED_CAST")
    inline fun <reified T : Any> forkForArrayList(clazz: KClass<T>): WrapLiveData<ArrayList<T>> {
        val key = getKey(DataType.ArrayList, clazz)
        if (!mSystemLiveData.containsKey(key)) {
            mSystemLiveData[key] = WrapLiveData<ArrayList<T>>() as WrapLiveData<*>
        }
        return mSystemLiveData[key] as WrapLiveData<ArrayList<T>>
    }


    /**
     * 获取子live data
     */
    @Suppress("UNCHECKED_CAST")
    inline fun <reified T : Any> getLiveData(clazz: KClass<T>): WrapLiveData<T> {
        val key = getKey(DataType.Normal, clazz)
        if (!mSystemLiveData.containsKey(key)) {
            throw RuntimeException("call this before please call fork")
        }
        return mSystemLiveData[key] as WrapLiveData<T>
    }

    /**
     * 获取子live data
     */
    @Suppress("UNCHECKED_CAST")
    inline fun <reified T : Any> getLiveDataForArrayList(clazz: KClass<T>): WrapLiveData<ArrayList<T>> {
        val key = getKey(DataType.ArrayList, clazz)
        if (!mSystemLiveData.containsKey(key)) {
            throw RuntimeException("call this before please call fork")
        }
        return mSystemLiveData[key] as WrapLiveData<ArrayList<T>>
    }

    @Suppress("UNCHECKED_CAST")
    inline fun <API, reified RESPONSE_DATA : Any> RetrofitServiceCore.RetrofitRequester<API, RESPONSE_DATA>.commit(
        crossinline success: (RESPONSE_DATA?) -> Unit = {}
    ): RetrofitServiceCore.RetrofitRequester<API, RESPONSE_DATA> {

        return this
            .onStart {
                // 通知开始
                getLiveData(RESPONSE_DATA::class).setValueForAction(EventAction.START)
            }
            .onCancel {
                // 通知取消
                getLiveData(RESPONSE_DATA::class).setValueForAction(EventAction.CANCEL)
            }

            .onSuccess { result ->
                success(result)
                // 通知成功

                getLiveData(RESPONSE_DATA::class).setValue(EventAction.SUCCESS) {
                    if (result != null) {
                        this.data = result
                    }
                }
            }
            .onFailed { code, message ->
                // 通知失败
                val dataWrapper = getLiveData(RESPONSE_DATA::class).setValue(EventAction.FAILED) {
                    this.code = code
                    this.message = message
                }

                return@onFailed dataWrapper.errorProcessed
            }
            .request()
    }

    @Suppress("UNCHECKED_CAST")
    inline fun <API, reified T : Any> RetrofitServiceCore.RetrofitRequester<API, ArrayList<T>>.commitForArrayList(
        crossinline success: (ArrayList<T>?) -> Unit = {}
    ): RetrofitServiceCore.RetrofitRequester<API, ArrayList<T>> {

        return this
            .onStart {
                // 通知开始
                getLiveDataForArrayList(T::class).setValueForArrayListForAction<T>(EventAction.START)
            }
            .onCancel {
                // 通知取消
                getLiveDataForArrayList(T::class).setValueForArrayListForAction<T>(EventAction.CANCEL)
            }

            .onSuccess { result ->
                success(result)
                // 通知成功
                getLiveDataForArrayList(T::class).setValueForArrayList<T>(EventAction.SUCCESS) {
                    if (result != null) {
                        this.data = result
                    }
                }

            }
            .onFailed { code, message ->
                // 通知失败
                val dataWrapper = getLiveDataForArrayList(T::class).setValueForArrayList<T>(EventAction.SUCCESS) {
                    this.code = code
                    this.message = message
                }

                return@onFailed dataWrapper.errorProcessed
            }
            .request()
    }

    /**
     * 用于一次点击，多次网络请求
     * @param setStartAction 是否发送Start事件  只有多次网络请求的第一次是需要发送Start事件
     * @param finalForkKClass 最终订阅的kClass，与fork一一对应 [fork]
     * @param success 成功的回调
     * @return 请求器[RetrofitServiceCore.RetrofitRequester]
     */
    @Suppress("UNCHECKED_CAST")
    inline fun <reified T : Any, SERVICE, reified RESPONSE_DATA : Any> RetrofitServiceCore.RetrofitRequester<SERVICE, RESPONSE_DATA>.commitMulti(
        setStartAction: Boolean,
        finalForkKClass: KClass<T>,
        crossinline success: (RESPONSE_DATA?) -> Unit
    ): RetrofitServiceCore.RetrofitRequester<SERVICE, RESPONSE_DATA> {
        return this
            .onStart { if (setStartAction) getLiveData(finalForkKClass).setValueForAction(EventAction.START) }
            .onCancel { getLiveData(finalForkKClass).setValueForAction(EventAction.CANCEL) }
            .onSuccess { result ->
                success(result)
            }
            .onFailed { code, message ->
                val dataWrapper = getLiveData(finalForkKClass).setValue(EventAction.FAILED) {
                    this.code = code
                    this.message = message
                }
                return@onFailed dataWrapper.errorProcessed
            }.request()
    }

    override fun onCleared() {
        onDestroy()
    }
}


