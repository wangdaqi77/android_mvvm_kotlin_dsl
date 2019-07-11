package com.wongki.framework.mvvm

import androidx.lifecycle.ViewModel
import com.wongki.framework.http.retrofit.core.RetrofitServiceCore
import com.wongki.framework.mvvm.action.EventAction
import com.wongki.framework.mvvm.lifecycle.ILiveDataViewModel
import com.wongki.framework.mvvm.retrofit.IRetrofitViewModel

/**
 * @author  wangqi
 * date:    2019/7/4
 * email:   wangqi7676@163.com
 * desc:    .
 */

abstract class AbsLiveDataViewModel : ViewModel(), IRetrofitViewModel, ILiveDataViewModel {

    @Suppress("UNCHECKED_CAST")
    inline fun <API, reified RESPONSE_DATA : Any> RetrofitServiceCore.RetrofitRequester<API, RESPONSE_DATA>.commit(): RetrofitServiceCore.RetrofitRequester<API, RESPONSE_DATA> {
//        获取RESPONSE_DATA的运行时类型，但是失败了
//        java.lang.ClassCastException: libcore.reflect.TypeVariableImpl cannot be cast to java.lang.Class
//        val parameterizedType = javaClass.genericSuperclass as ParameterizedType
//        val responseType = parameterizedType.actualTypeArguments[1] as Class<RESPONSE_DATA>
        return this
            .onStart {
                // 通知开始
                postValue(RESPONSE_DATA::class, EventAction.START) {
                    it.data = null
                }
            }
            .onCancel {
                // 通知取消
                postValue(RESPONSE_DATA::class, EventAction.CANCEL) {}
            }

            .onSuccess { result ->
                // 通知成功
                postValue(RESPONSE_DATA::class, EventAction.SUCCESS) {
                    it.data = result
                }
            }
            .onFailed { code, message ->
                // 通知失败
                /**
                 * postValue方法是Handler发送消息，对于栈内而言此时属于异步
                 * 所以外层设置onFailed的返回值是不准确的
                 */
                postValue(RESPONSE_DATA::class, EventAction.FAILED) {
                    it.code = code
                    it.message = message
                }
                false
            }
            .request()
    }

    @Suppress("UNCHECKED_CAST")
    inline fun <API, reified T:Any> RetrofitServiceCore.RetrofitRequester<API, ArrayList<T>>.commitForArrayList(): RetrofitServiceCore.RetrofitRequester<API, ArrayList<T>> {
//        获取RESPONSE_DATA的运行时类型，但是失败了
//        java.lang.ClassCastException: libcore.reflect.TypeVariableImpl cannot be cast to java.lang.Class
//        val parameterizedType = javaClass.genericSuperclass as ParameterizedType
//        val responseType = parameterizedType.actualTypeArguments[1] as Class<RESPONSE_DATA>
        return this
            .onStart {
                // 通知开始
                postValueForArrayList(T::class, EventAction.START) {
                    it.data = null
                }
            }
            .onCancel {
                // 通知取消
                postValueForArrayList(T::class, EventAction.CANCEL) {}
            }

            .onSuccess { result ->
                // 通知成功
                postValueForArrayList(T::class, EventAction.SUCCESS) {
                    it.data = result as ArrayList<T>
                }
            }
            .onFailed { code, message ->
                // 通知失败
                /**
                 * postValue方法是Handler发送消息，对于栈内而言此时属于异步
                 * 所以外层设置onFailed的返回值是不准确的
                 */
                postValueForArrayList(T::class, EventAction.FAILED) {
                    it.code = code
                    it.message = message
                }
                false
            }
            .request()
    }

    override fun onCleared() {
        onDestroy()
    }
}


