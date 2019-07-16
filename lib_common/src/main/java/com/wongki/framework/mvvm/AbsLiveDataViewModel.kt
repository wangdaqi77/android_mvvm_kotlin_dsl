package com.wongki.framework.mvvm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.wongki.framework.http.retrofit.core.RetrofitServiceCore
import com.wongki.framework.mvvm.action.EventAction
import com.wongki.framework.mvvm.lifecycle.DataWrapper
import com.wongki.framework.mvvm.lifecycle.ILiveDataViewModel
import com.wongki.framework.mvvm.remote.retrofit.IRetrofitViewModel
import kotlin.reflect.KClass

/**
 * @author  wangqi
 * date:    2019/7/4
 * email:   wangqi7676@163.com
 * desc:    .
 */

abstract class AbsLiveDataViewModel : ViewModel(), IRetrofitViewModel, ILiveDataViewModel {

    override val mSystemLiveData: HashMap<String, MutableLiveData<DataWrapper<*>>?> = HashMap()

    @Suppress("UNCHECKED_CAST")
    inline fun <API, reified RESPONSE_DATA : Any> RetrofitServiceCore.RetrofitRequester<API, RESPONSE_DATA>.commit(crossinline success: (RESPONSE_DATA?) -> Unit = {}): RetrofitServiceCore.RetrofitRequester<API, RESPONSE_DATA> {
//        获取RESPONSE_DATA的运行时类型，但是失败了
//        java.lang.ClassCastException: libcore.reflect.TypeVariableImpl cannot be cast to java.lang.Class
//        val parameterizedType = javaClass.genericSuperclass as ParameterizedType
//        val responseType = parameterizedType.actualTypeArguments[1] as Class<RESPONSE_DATA>
        val kClass = RESPONSE_DATA::class
        if (kClass == ArrayList::class) {
            val kTypeParameter = kClass.typeParameters[0]
            kTypeParameter as KClass<*>

         }
        return this
            .onStart {
                // 通知开始
                setValue(RESPONSE_DATA::class, EventAction.START) {}
            }
            .onCancel {
                // 通知取消
                setValue(RESPONSE_DATA::class, EventAction.CANCEL) {}
            }

            .onSuccess { result ->
                success(result)
                // 通知成功
                setValue(RESPONSE_DATA::class, EventAction.SUCCESS) {
                    this.data = result
                }
            }
            .onFailed { code, message ->
                // 通知失败
                val dataWrapper = setValue(RESPONSE_DATA::class, EventAction.FAILED) {
                    this.code = code
                    this.message = message
                }

                return@onFailed dataWrapper.errorProcessed
            }
            .request()
    }

    @Suppress("UNCHECKED_CAST")
    inline fun <API, reified T:Any> RetrofitServiceCore.RetrofitRequester<API, ArrayList<T>>.commitForArrayList(crossinline success: (ArrayList<T>?) -> Unit = {}): RetrofitServiceCore.RetrofitRequester<API, ArrayList<T>> {
//        获取RESPONSE_DATA的运行时类型，但是失败了
//        java.lang.ClassCastException: libcore.reflect.TypeVariableImpl cannot be cast to java.lang.Class
//        val parameterizedType = javaClass.genericSuperclass as ParameterizedType
//        val responseType = parameterizedType.actualTypeArguments[1] as Class<RESPONSE_DATA>
        return this
            .onStart {
                // 通知开始
                setValueForArrayList(T::class, EventAction.START) {}
            }
            .onCancel {
                // 通知取消
                setValueForArrayList(T::class, EventAction.CANCEL) {}
            }

            .onSuccess { result ->
                success(result)
                // 通知成功
                setValueForArrayList(T::class, EventAction.SUCCESS) {
                    this.data = result as ArrayList<T>
                }
            }
            .onFailed { code, message ->
                // 通知失败
                val dataWrapper = setValueForArrayList(T::class, EventAction.FAILED) {
                    this.code = code
                    this.message = message
                }
                return@onFailed dataWrapper.errorProcessed
            }
            .request()
    }

    override fun onCleared() {
        onDestroy()
    }
}


