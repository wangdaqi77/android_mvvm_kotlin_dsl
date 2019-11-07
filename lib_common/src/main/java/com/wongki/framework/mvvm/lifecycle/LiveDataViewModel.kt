package com.wongki.framework.mvvm.lifecycle

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.wongki.framework.http.retrofit.core.RetrofitServiceCore
import com.wongki.framework.mvvm.event.Event
import com.wongki.framework.mvvm.lifecycle.wrap.IEventLiveDataViewModel
import com.wongki.framework.mvvm.lifecycle.wrap.event.EventLiveData
import com.wongki.framework.mvvm.remote.retrofit.IRetrofitViewModel

/**
 * @author  wangqi
 * date:    2019-11-05
 * email:   wangqi7676@163.com
 * desc:    .
 */
@LiveDataViewModelDslMarker
open class LiveDataViewModel : ViewModel(), IRetrofitViewModel, ILiveDataViewModel,
    IEventLiveDataViewModel {
    val TAG = javaClass.simpleName
    override val mLiveDatas: HashMap<LiveDataKey, MutableLiveData<*>?> = HashMap()
    override val mLiveDataWrappers: HashMap<LiveDataKey, EventLiveData<*>?> = HashMap()


    /**
     * 真正发起网络请求&&通知UI前做一些事情
     */
    @Suppress("UNCHECKED_CAST")
    inline fun <API, reified RESPONSE_DATA : Any> RetrofitServiceCore<API>.RequesterBuilder<RESPONSE_DATA>.observeWithBeforeNotifyUI(
        crossinline init: ObserveBuilder<RESPONSE_DATA>.() -> Unit = {}
    ): RetrofitServiceCore<API>.RetrofitRequester<RESPONSE_DATA> {
        val builder = ObserveBuilder<RESPONSE_DATA>()
        builder.init()
        val kClass = RESPONSE_DATA::class
        return observer {

            onStart {
                builder.onStart?.invoke()
                // 通知开始
                setWrapperValue<RESPONSE_DATA> {
                    this.kClass = kClass

                    value {
                        event = Event.START
                    }
                }
            }

            onCancel {
                builder.onCancel?.invoke()
                // 通知取消
                setWrapperValue<RESPONSE_DATA> {
                    this.kClass = kClass
                    value {
                        event = Event.CANCEL
                    }
                }
            }

            onSuccess {
                val result = this

                builder.onSuccess?.invoke(result)
                // 通知成功
                setWrapperValue<RESPONSE_DATA> {
                    this.kClass = kClass
                    value {
                        event = Event.SUCCESS
                        data = result
                    }
                }

            }

            onFailed { code, message ->
                builder.onFailed?.invoke(code, message)
                // 通知失败
                setWrapperValue<RESPONSE_DATA> {
                    this.kClass = kClass
                    value {
                        event = Event.FAILED
                        this.code = code
                        this.message = message
                    }
                }

                return@onFailed getWrapperValue<RESPONSE_DATA> {
                    this.kClass = kClass
                }?.errorProcessed ?: false
            }
        }.apply {
            lifecycleObserver { this@LiveDataViewModel }
        }

    }

    /**
     * 真正发起网络请求&&通知UI前做一些事情
     */
    @Suppress("UNCHECKED_CAST")
    inline fun <API, reified ITEM : Any> RetrofitServiceCore<API>.RequesterBuilder<ArrayList<ITEM>>.observeWithBeforeNotifyUIForArrayList(
        crossinline init: ObserveBuilder<ArrayList<ITEM>>.() -> Unit = {}
    ): RetrofitServiceCore<API>.RetrofitRequester<ArrayList<ITEM>> {
        val builder = ObserveBuilder<ArrayList<ITEM>>()
        builder.init()
        val kClass = ITEM::class
        return observer {

            onStart {
                builder.onStart?.invoke()
                // 通知开始
                setWrapperArrayListValue<ITEM> {
                    this.kClass = kClass

                    value {
                        event = Event.START
                    }
                }
            }

            onCancel {
                builder.onCancel?.invoke()
                // 通知取消
                setWrapperArrayListValue<ITEM> {
                    this.kClass = kClass
                    value {
                        event = Event.CANCEL
                    }
                }
            }

            onSuccess {
                val result = this

                builder.onSuccess?.invoke(result)
                // 通知成功
                setWrapperArrayListValue<ITEM> {
                    this.kClass = kClass
                    value {
                        event = Event.SUCCESS
                        data = result
                    }
                }

            }

            onFailed { code, message ->

                builder.onFailed?.invoke(code, message)
                // 通知失败
                setWrapperArrayListValue<ITEM> {
                    this.kClass = kClass
                    value {
                        event = Event.FAILED
                        this.code = code
                        this.message = message
                    }
                }

                return@onFailed getWrapperArrayListValue<ITEM> {
                    this.kClass = kClass
                }?.errorProcessed ?: false
            }
        }.apply {
            lifecycleObserver { this@LiveDataViewModel }
        }

    }

//
//    /**
//     * 用于一次点击，多次网络请求
//     * @param setStartAction 是否发送Start事件  只有多次网络请求的第一次是需要发送Start事件
//     * @param finalAttachedKClass 最终订阅的kClass，与attach一一对应 [attachLiveDataWrapper]
//     * @param success 成功的回调
//     * @return 请求器[RetrofitServiceCore.RetrofitRequester]
//     */
//    @Suppress("UNCHECKED_CAST")
//    inline fun <reified T : Any, SERVICE, reified RESPONSE_DATA : Any> RetrofitServiceCore<SERVICE>.RequesterBuilder<RESPONSE_DATA>.observeLiveDataWrapperForMulti(
//        setStartAction: Boolean,
//        finalAttachedKClass: KClass<T>,
//        crossinline success: RESPONSE_DATA?.() -> Unit
//    ): RetrofitServiceCore<SERVICE>.RetrofitRequester<RESPONSE_DATA> {
//        return observer {
//
//            onStart {
//                if (setStartAction) {
//                    getLiveDataWrapper(finalAttachedKClass).setValueForAction(Event.START)
//                }
//            }
//
//            onCancel {
//                getLiveDataWrapper(finalAttachedKClass).setValueForAction(Event.CANCEL)
//            }
//
//            onSuccess {
//                success(this)
//            }
//
//            onFailed { code, message ->
//                val dataWrapper = getLiveDataWrapper(finalAttachedKClass).setValue(Event.FAILED) {
//                    this.code = code
//                    this.message = message
//                }
//                return@onFailed dataWrapper.errorProcessed
//            }
//
//        }.apply {
//            lifecycleObserver { this@AbsLiveDataWrapperViewModel }
//        }
//    }
//

    override fun onCleared() {
        super.onCleared()
        onDestroy()
    }
}