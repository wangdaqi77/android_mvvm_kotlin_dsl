package com.wongki.framework.mvvm.lifecycle

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStore
import com.wongki.framework.model.domain.MyResponse
import com.wongki.framework.http.retrofit.core.RetrofitServiceCore
import com.wongki.framework.mvvm.event.Event
import com.wongki.framework.mvvm.lifecycle.exception.DslRejectedException
import com.wongki.framework.mvvm.lifecycle.wrap.IEventLiveDataViewModel
import com.wongki.framework.mvvm.lifecycle.wrap.event.*
import com.wongki.framework.mvvm.remote.retrofit.IRetrofitRepo
import java.lang.ref.WeakReference

/**
 * @author  wangqi
 * date:    2019-11-05
 * email:   wangqi7676@163.com
 *
 * 淡化了LiveData、Lifecycle的存在，dsl style便于阅读
 *
 *
 * View - 装载订阅
 *
 *  viewModel<XXViewModel> {
 *      attachObserve<String> {
 *          key {
 *              method = "setUserName"
 *          }
 *          observe {
 *              onChange {tv_user_name.text = "$this"} // 更新UI
 *          }
 *  }
 *
 *      attachObserve<XX> {
 *          // ...
 *      }
 *  }

 * ViewModel - 设置、更新数据
 *
 *  fun setUserName(name:String) {
 *      setValue<String> {
 *      key {
 *          method = "setUserName"
 *      }
 *          value { name } // 通知订阅的地方
 *      }
 *  }
 *
 * API说明
 *
 * 一、装载订阅
 * 1.[LiveDataViewModel.attachObserve]常规无状态，使用参考上面的例子
 * 2.[LiveDataViewModel.attachEventObserve]异步场景有状态[EventValue.event]
 * 3.[LiveDataViewModel.attachEventObserveForArrayList]异步场景有状态
 *
 * 二、设置值
 * 1.[LiveDataViewModel.setValue]常规无状态，使用参考上面的例子
 * 2.[LiveDataViewModel.setEventValue]异步场景有状态，
 * 具体使用可参考[LiveDataViewModel.observeAndTransformEventObserver]
 * 3.[LiveDataViewModel.setEventValueForArrayList]异步场景ArrayList有状态，
 * 具体使用可参考[LiveDataViewModel.observeAndTransformEventObserverForArrayList]
 *
 * 三、获取值(参考设置值)
 * 1.[LiveDataViewModel.getValue]常规无状态
 * 2.[LiveDataViewModel.getEventValue]异步场景有状态
 * 3.[LiveDataViewModel.getEventValueForArrayList]异步场景ArrayList有状态
 *
 *  注意：
 *  装载订阅时生命周期的提供者默认值为创建ViewModel时的LifecycleOwner对象，
 *  详情请查看[FragmentActivity.viewModel]和[Fragment.viewModel]的拓展函数,
 *  以及[ILiveDataViewModel.attachObserve]等装载订阅函数，如果你需要为LiveData
 *  提供其他的LifecycleOwner，那么需要在装载订阅时覆盖掉默认值
 *      attachObserve {
 *          key {...}
 *          observe {
 *              owner = LifecycleOwner
 *          }
 *      }
 *
 */
@LiveDataViewModelDslMarker
open class LiveDataViewModel : ViewModel(), ILiveDataViewModel, IEventLiveDataViewModel,
    IRetrofitRepo, ILifecycleOwnerWrapper {
    val TAG = javaClass.simpleName
    override val mLiveDatas: HashMap<LiveDataKey, MutableLiveData<*>?> = HashMap()
    override val mEventLiveDatas: HashMap<LiveDataKey, EventLiveData<*>?> = HashMap()
    internal lateinit var lifecycleOwnerRef: WeakReference<LifecycleOwner?>

    @LiveDataViewModelDslMarker
    @Suppress("UNCHECKED_CAST")
    inline fun <reified T : Any> attachObserve(init: ILiveDataViewModel.LiveDataBuilder<T>.() -> Unit): MutableLiveData<T> {
        val builder = ILiveDataViewModel.LiveDataBuilder<T>()
        builder.init()
        if (!builder.keyBuilder.check()) {
            throw DslRejectedException(
                "attachObserve<${T::class.simpleName}>", "key", "method = ?"
            )
        }
        return super.attachObserve(builder)
    }

    @LiveDataViewModelDslMarker
    @Suppress("UNCHECKED_CAST")
    inline fun <reified T : Any> attachEventObserve(init: IEventLiveDataViewModel.EventLiveDataBuilder<T>.() -> Unit): EventLiveData<T> {
        val builder = IEventLiveDataViewModel.EventLiveDataBuilder<T>()
        builder.init()
        builder.keyBuilder.kClass = T::class
        if (!builder.keyBuilder.check()) {
            throw DslRejectedException(
                "attachEventObserve<${T::class.simpleName}>", "key", "kClass = ?"
            )
        }
        return super.attachEventObserve(builder)
    }

    @LiveDataViewModelDslMarker
    @Suppress("UNCHECKED_CAST")
    inline fun <reified ITEM : Any> attachEventObserveForArrayList(init: IEventLiveDataViewModel.EventLiveDataArrayListBuilder<ITEM>.() -> Unit): EventLiveData<ArrayList<ITEM>> {
        val builder = IEventLiveDataViewModel.EventLiveDataArrayListBuilder<ITEM>()
        builder.init()
        builder.keyBuilder.kClass = ITEM::class
        if (!builder.keyBuilder.check()) {
            throw DslRejectedException(
                "attachEventObserveForArrayList<${ITEM::class.simpleName}>", "key", "kClass = ?"
            )
        }
        return super.attachEventObserveForArrayList(builder)
    }

    /**
     * 设置数据
     */
    @LiveDataViewModelDslMarker
    inline fun <reified T : Any> setValue(init: LiveDataSetterBuilder<T>.() -> Unit) {
        val builder = LiveDataSetterBuilder<T>()
        builder.init()
        if (!builder.keyBuilder.check()) {
            throw DslRejectedException(
                "setValue<${T::class.simpleName}>", "key", "method = ?"
            )
        }
        return super.setValue(builder)
    }

    /**
     * 获取数据
     */
    @LiveDataViewModelDslMarker
    inline fun <reified T : Any> getValue(init: LiveDataGetterBuilder<T>.() -> Unit): T? {
        val builder = LiveDataGetterBuilder<T>()
        builder.init()
        if (!builder.keyBuilder.check()) {
            throw DslRejectedException(
                "getValue<${T::class.simpleName}>", "key", "method = ?"
            )
        }
        return super.getValue(builder)
    }


    /**
     * 设置事件数据
     */
    @LiveDataViewModelDslMarker
    inline fun <reified T : Any> setEventValue(init: EventLiveDataSetterBuilder<T>.() -> Unit) {
        val builder = EventLiveDataSetterBuilder<T>()
        builder.init()
        if (!builder.keyBuilder.check()) {
            throw DslRejectedException(
                "setEventValue<${T::class.simpleName}>", "key", "method = ?"
            )
        }
        return super.setEventValue(builder)
    }

    /**
     * 设置list类型的事件数据
     */
    @LiveDataViewModelDslMarker
    inline fun <reified ITEM : Any> setEventValueForArrayList(init: EventLiveDataArrayListSetterBuilder<ITEM>.() -> Unit) {
        val builder = EventLiveDataArrayListSetterBuilder<ITEM>()
        builder.init()
        if (!builder.keyBuilder.check()) {
            throw DslRejectedException(
                "setEventValueForArrayList<${ITEM::class.simpleName}>", "key", "method = ?"
            )
        }
        return super.setEventValueForArrayList(builder)
    }

    /**
     * 获取事件数据
     */
    @LiveDataViewModelDslMarker
    inline fun <reified T : Any> getEventValue(init: EventLiveDataGetterBuilder<T>.() -> Unit): EventValue<T>? {
        val builder = EventLiveDataGetterBuilder<T>()
        builder.init()
        if (!builder.keyBuilder.check()) {
            throw DslRejectedException(
                "getEventValue<${T::class.simpleName}>", "key", "method = ?"
            )
        }
        return super.getEventValue(builder)
    }

    /**
     * 获取list类型的事件数据
     */
    @LiveDataViewModelDslMarker
    inline fun <reified ITEM : Any> getEventValueForArrayList(init: EventLiveDataArrayListGetterBuilder<ITEM>.() -> Unit): EventValue<ArrayList<ITEM>>? {
        val builder = EventLiveDataArrayListGetterBuilder<ITEM>()
        builder.init()
        if (!builder.keyBuilder.check()) {
            throw DslRejectedException(
                "getEventValueForArrayList<${ITEM::class.simpleName}>", "key", "method = ?"
            )
        }
        return super.getEventValueForArrayList(builder)
    }

    /**
     * 网络请求的观察器转换成EventValue[setEventValue]通知[attachEventObserve]
     * &&
     * 在通知UI前观察数据
     */
    @LiveDataViewModelDslMarker
    @Suppress("UNCHECKED_CAST")
    inline fun <API, reified RESPONSE_DATA : Any> RetrofitServiceCore<API>.RequesterBuilder<MyResponse<RESPONSE_DATA>>.observeAndTransformEventObserver(
        crossinline init: EventValueObserverBuilder<RESPONSE_DATA>.() -> Unit = {}
    ) {
        val builder = EventValueObserverBuilder<RESPONSE_DATA>()
        builder.init()
        val kClass = RESPONSE_DATA::class

        lifecycleObserver = this@LiveDataViewModel
        observer {
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
                val data = this?.data

                builder.onSuccess?.invoke(data)
                // 通知成功
                setEventValue<RESPONSE_DATA> {
                    key {
                        this.kClass = kClass
                    }
                    value {
                        event = Event.SUCCESS
                        this.data = this@onSuccess?.data
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
        }
    }


    /**
     * 网络请求的观察器转换成EventValue[setEventValueForArrayList]通知[attachEventObserveForArrayList]
     * &&
     * 在通知UI前观察数据
     */
    @LiveDataViewModelDslMarker
    @Suppress("UNCHECKED_CAST")
    inline fun <API, reified ITEM : Any> RetrofitServiceCore<API>.RequesterBuilder<MyResponse<ArrayList<ITEM>>>.observeAndTransformEventObserverForArrayList(
        crossinline init: EventValueObserverBuilder<ArrayList<ITEM>>.() -> Unit = {}
    ) {
        lifecycleObserver = this@LiveDataViewModel
        val builder = EventValueObserverBuilder<ArrayList<ITEM>>()
        builder.init()
        val kClass = ITEM::class
        observer {

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
                val result = this?.data
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