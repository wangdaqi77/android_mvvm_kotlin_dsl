package com.wongki.framework.mvvm.lifecycle

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.wongki.framework.mvvm.action.EventAction
import com.wongki.framework.mvvm.retrofit.RetrofitLiveDataViewModel
import java.lang.RuntimeException

/**
 * @author  wangqi
 * date:    2019/7/4
 * email:   wangqi7676@163.com
 * desc:    .
 *          ViewModel接口
 *          核心：管理多个子LiveData，管理多种类型数据变动
 */
interface ILiveDataViewModel {

    val mSystemLiveData: HashMap<String, MutableLiveData<DataWrapper<*>>?>


    private fun <T> getKey(type: DataType, clazz: Class<T>) = "${type.name}<${clazz.name}>"

    /**
     * 生成子live data
     */
    @Suppress("UNCHECKED_CAST")
    fun <T> fork(clazz: Class<T>): MutableLiveData<DataWrapper<T>> {
        val key = getKey(DataType.Normal, clazz)
        if (!mSystemLiveData.containsKey(key)) {
            mSystemLiveData[key] = MutableLiveData<DataWrapper<T>>() as MutableLiveData<DataWrapper<*>>
        }
        return mSystemLiveData[key] as MutableLiveData<DataWrapper<T>>
    }

    /**
     * 生成子live data
     */
    @Suppress("UNCHECKED_CAST")
    fun <T> forkForArrayList(clazz: Class<T>): MutableLiveData<DataWrapper<ArrayList<T>>> {
        val key = getKey(DataType.ArrayList, clazz)
        if (!mSystemLiveData.containsKey(key)) {
            mSystemLiveData[key] = MutableLiveData<DataWrapper<ArrayList<T>>>() as MutableLiveData<DataWrapper<*>>
        }
        return mSystemLiveData[key] as MutableLiveData<DataWrapper<ArrayList<T>>>
    }

    /**
     * 获取子live data
     */
    @Suppress("UNCHECKED_CAST")
    fun <T> getLiveData(clazz: Class<T>): MutableLiveData<DataWrapper<T>> {
        val key = getKey(DataType.Normal, clazz)
        if (!mSystemLiveData.containsKey(key)) {
            throw RuntimeException("call this before please call fork")
        }
        return mSystemLiveData[key] as MutableLiveData<DataWrapper<T>>
    }

    /**
     * 获取子live data
     */
    @Suppress("UNCHECKED_CAST")
    fun <T> getLiveDataForArrayList(clazz: Class<T>): MutableLiveData<DataWrapper<ArrayList<T>>> {
        val key = getKey(DataType.ArrayList, clazz)
        if (!mSystemLiveData.containsKey(key)) {
            throw RuntimeException("call this before please call fork")
        }
        return mSystemLiveData[key] as MutableLiveData<DataWrapper<ArrayList<T>>>
    }


    /**
     * 发送数据到接收者[observe]
     */
    fun <T> postValue(responseType: Class<T>, action: EventAction, apply: (DataWrapper<T>) -> Unit) {
        val dataWrapper = DataWrapper<T>()
        dataWrapper.action = action
        apply(dataWrapper)
        // 发送到订阅的位置
        getLiveData(responseType).postValue(dataWrapper)
    }

    fun <T> setValue(responseType: Class<T>, action: EventAction, apply: (DataWrapper<T>) -> Unit) {
        val dataWrapper = DataWrapper<T>()
        dataWrapper.action = action
        apply(dataWrapper)
        // 发送到订阅的位置
        getLiveData(responseType).setValue(dataWrapper)
    }


    /**
     * 发送数据到接收者[observe]
     */
    fun <T> postValueForArrayList(responseType: Class<T>, action: EventAction, apply: (DataWrapper<ArrayList<T>>) -> Unit) {
        val dataWrapper = DataWrapper<ArrayList<T>>()
        dataWrapper.action = action
        apply(dataWrapper)
        // 发送到订阅的位置
        getLiveDataForArrayList(responseType).postValue(dataWrapper)
    }


    fun <T> setValueForArrayList(responseType: Class<T>, action: EventAction, apply: (DataWrapper<ArrayList<T>>) -> Unit) {
        val dataWrapper = DataWrapper<ArrayList<T>>()
        dataWrapper.action = action
        apply(dataWrapper)
        // 发送到订阅的位置
        getLiveDataForArrayList(responseType).setValue(dataWrapper)
    }

}


/**
 * 订阅，接收数据变化的事件通知
 * 通知数据变化->[RetrofitLiveDataViewModel.commit]
 */
fun <T> MutableLiveData<DataWrapper<T>>?.observeSimple(
    owner: LifecycleOwner,
    onFailed: (Int, String?) -> Boolean = { _, _ -> false },
    onSuccess: (T?) -> Unit
) {
    if (this == null) {
        Log.e("LiveDataViewModel", "MutableLiveData is null")
        return
    }

    this.observe(owner, Observer<DataWrapper<T>> { result ->
        if (result != null) {
            val action = result.action
            when (action) {
                EventAction.FAILED -> {
                    onFailed(result.code, result.message)
                }

                EventAction.SUCCESS -> {
                    onSuccess(result.data)
                }
                else -> {
                    Log.e("LiveDataViewModel", "未处理的Action：${action.name}")
                }
            }
        }

    })
}


/**
 * 订阅，接收数据变化的事件通知
 * 通知数据变化->[RetrofitLiveDataViewModel.commit]
 */
fun <T> MutableLiveData<DataWrapper<T>>?.observe(
    owner: LifecycleOwner,
    onStart: () -> Unit,
    onCancel: () -> Unit,
    onFailed: (Int, String?) -> Boolean,
    onSuccess: (T?) -> Unit
) {
    if (this == null) {
        Log.e("LiveDataViewModel", "MutableLiveData is null")
        return
    }

    this.observe(owner, Observer<DataWrapper<T>> { result ->
        if (result != null) {
            val action = result.action
            when (action) {
                EventAction.START -> {
                    onStart()
                }
                EventAction.CANCEL -> {
                    onCancel()
                }
                EventAction.SUCCESS -> {
                    onSuccess(result.data)
                }
                EventAction.FAILED -> {
                    onFailed(result.code, result.message)
                }
                else -> {
                    Log.e("LiveDataViewModel", "未处理的Action：${action.name}")
                }
            }
        }

    })
}