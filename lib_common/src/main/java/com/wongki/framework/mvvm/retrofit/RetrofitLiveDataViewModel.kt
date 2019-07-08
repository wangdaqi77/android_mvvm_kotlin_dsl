package com.wongki.framework.mvvm.retrofit

import com.wongki.framework.http.retrofit.core.RetrofitServiceCore
import com.wongki.framework.mvvm.action.EventAction
import com.wongki.framework.mvvm.lifecycle.ILiveDataViewModel

/**
 * @author  wangqi
 * date:    2019/7/4
 * email:   wangqi7676@163.com
 * desc:    .
 */

interface RetrofitLiveDataViewModel : IRetrofitViewModel, ILiveDataViewModel {

    @Suppress("UNCHECKED_CAST")
    fun <API, RESPONSE_DATA> RetrofitServiceCore.RetrofitRequester<API, RESPONSE_DATA>.commit(responseType: Class<RESPONSE_DATA>): RetrofitServiceCore.RetrofitRequester<API, RESPONSE_DATA> {
//        获取RESPONSE_DATA的运行时类型，但是失败了
//        java.lang.ClassCastException: libcore.reflect.TypeVariableImpl cannot be cast to java.lang.Class
//        val parameterizedType = javaClass.genericSuperclass as ParameterizedType
//        val responseType = parameterizedType.actualTypeArguments[1] as Class<RESPONSE_DATA>
        return this
            .onStart {
                // 通知开始
                postValue(responseType, EventAction.START) {
                    it.data = null
                }
            }
            .onCancel {
                // 通知取消
                postValue(responseType, EventAction.CANCEL) {}
            }

            .onSuccess { result ->
                // 通知成功
                postValue(responseType, EventAction.SUCCESS) {
                    it.data = result
                }
            }
            .onFailed { code, message ->
                // 通知失败
                /**
                 * postValue方法是Handler发送消息，对于栈内而言此时属于异步
                 * 所以外层设置onFailed的返回值是不准确的
                 */
                postValue(responseType, EventAction.FAILED) {
                    it.code = code
                    it.message = message
                }
                false
            }
            .request()
    }

    @Suppress("UNCHECKED_CAST")
    fun <API,T> RetrofitServiceCore.RetrofitRequester<API, ArrayList<T>>.commitForArrayList(responseType: Class<T>): RetrofitServiceCore.RetrofitRequester<API, ArrayList<T>> {
//        获取RESPONSE_DATA的运行时类型，但是失败了
//        java.lang.ClassCastException: libcore.reflect.TypeVariableImpl cannot be cast to java.lang.Class
//        val parameterizedType = javaClass.genericSuperclass as ParameterizedType
//        val responseType = parameterizedType.actualTypeArguments[1] as Class<RESPONSE_DATA>
        return this
            .onStart {
                // 通知开始
                postValueForArrayList(responseType, EventAction.START) {
                    it.data = null
                }
            }
            .onCancel {
                // 通知取消
                postValueForArrayList(responseType, EventAction.CANCEL) {}
            }

            .onSuccess { result ->
                // 通知成功
                postValueForArrayList(responseType, EventAction.SUCCESS) {
                    it.data = result as ArrayList<T>
                }
            }
            .onFailed { code, message ->
                // 通知失败
                /**
                 * postValue方法是Handler发送消息，对于栈内而言此时属于异步
                 * 所以外层设置onFailed的返回值是不准确的
                 */
                postValueForArrayList(responseType, EventAction.FAILED) {
                    it.code = code
                    it.message = message
                }
                false
            }
            .request()
    }
}


