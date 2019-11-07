package com.wongki.framework.mvvm.lifecycle

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStore
import com.wongki.framework.http.retrofit.core.RetrofitServiceCore
import com.wongki.framework.mvvm.event.Event
import com.wongki.framework.mvvm.lifecycle.wrap.IEventLiveDataViewModel
import com.wongki.framework.mvvm.lifecycle.wrap.event.EventLiveData
import com.wongki.framework.mvvm.lifecycle.wrap.event.EventValueObserveBuilder
import com.wongki.framework.mvvm.remote.retrofit.IRetrofitRepo
import java.lang.ref.WeakReference

/**
 * @author  wangqi
 * date:    2019-11-05
 * email:   wangqi7676@163.com
 *
 */
@LiveDataViewModelDslMarker
open class LiveDataViewModel : ViewModel(), ILiveDataViewModel, IEventLiveDataViewModel, IRetrofitRepo, ILifecycleOwnerWrapper {
    val TAG = javaClass.simpleName
    override val mLiveDatas: HashMap<LiveDataKey, MutableLiveData<*>?> = HashMap()
    override val mEventLiveDatas: HashMap<LiveDataKey, EventLiveData<*>?> = HashMap()
    internal lateinit var lifecycleOwnerRef: WeakReference<LifecycleOwner?>


    /**
     * 真正发起网络请求&&通知UI前做一些事情
     */
    @Suppress("UNCHECKED_CAST")
    inline fun <API, reified RESPONSE_DATA : Any> RetrofitServiceCore<API>.RequesterBuilder<RESPONSE_DATA>.observeWithBeforeNotifyUI(
        crossinline init: EventValueObserveBuilder<RESPONSE_DATA>.() -> Unit = {}
    ): RetrofitServiceCore<API>.RetrofitRequester<RESPONSE_DATA> {
        lifecycleObserver { this@LiveDataViewModel }
        val builder = EventValueObserveBuilder<RESPONSE_DATA>()
        builder.init()
        val kClass = RESPONSE_DATA::class
        return observer {
            onStart {
                builder.onStart?.invoke()
                // 通知开始
                setEventValue<RESPONSE_DATA> {
                    key {
                        this.kClass = kClass
                    }
                    value {
                        event = Event.START
                    }
                }
            }

            onCancel {
                builder.onCancel?.invoke()
                // 通知取消
                setEventValue<RESPONSE_DATA> {
                    key {
                        this.kClass = kClass
                    }
                    value {
                        event = Event.CANCEL
                    }
                }
            }

            onSuccess {
                val result = this

                builder.onSuccess?.invoke(result)
                // 通知成功
                setEventValue<RESPONSE_DATA> {
                    key {
                        this.kClass = kClass
                    }
                    value {
                        event = Event.SUCCESS
                        data = result
                    }
                }

            }

            onFailed { code, message ->
                builder.onFailed?.invoke(code, message)
                // 通知失败
                setEventValue<RESPONSE_DATA> {
                    key {
                        this.kClass = kClass
                    }
                    value {
                        event = Event.FAILED
                        this.code = code
                        this.message = message
                    }
                }

                return@onFailed getEventValue<RESPONSE_DATA> {
                    key {
                        this.kClass = kClass
                    }
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
        crossinline init: EventValueObserveBuilder<ArrayList<ITEM>>.() -> Unit = {}
    ): RetrofitServiceCore<API>.RetrofitRequester<ArrayList<ITEM>> {
        lifecycleObserver { this@LiveDataViewModel }
        val builder = EventValueObserveBuilder<ArrayList<ITEM>>()
        builder.init()
        val kClass = ITEM::class
        return observer {

            onStart {
                builder.onStart?.invoke()
                // 通知开始
                setEventValueForArrayList<ITEM> {
                    key {
                        this.kClass = kClass
                    }

                    value {
                        event = Event.START
                    }
                }
            }

            onCancel {
                builder.onCancel?.invoke()
                // 通知取消
                setEventValueForArrayList<ITEM> {
                    key {
                        this.kClass = kClass
                    }
                    value {
                        event = Event.CANCEL
                    }
                }
            }

            onSuccess {
                val result = this

                builder.onSuccess?.invoke(result)
                // 通知成功
                setEventValueForArrayList<ITEM> {
                    key {
                        this.kClass = kClass
                    }
                    value {
                        event = Event.SUCCESS
                        data = result
                    }
                }

            }

            onFailed { code, message ->

                builder.onFailed?.invoke(code, message)
                // 通知失败
                setEventValueForArrayList<ITEM> {
                    key {
                        this.kClass = kClass
                    }
                    value {
                        event = Event.FAILED
                        this.code = code
                        this.message = message
                    }
                }

                return@onFailed getEventValueForArrayList<ITEM> {
                    key {
                        this.kClass = kClass
                    }
                }?.errorProcessed ?: false
            }
        }.apply {
            lifecycleObserver { this@LiveDataViewModel }
        }

    }


    fun setLifecycleOwner(lifecycleOwner: LifecycleOwner) {
        this.lifecycleOwnerRef = WeakReference(lifecycleOwner)

    }

    override fun getLifecycleOwner(): LifecycleOwner? = lifecycleOwnerRef.get()

    /**
     * 无需担心网络请求造成的内存泄漏 {@link[ViewModelStore.clear]}
     */
    override fun onCleared() {
        super.onCleared()
        onDestroy()
    }
}