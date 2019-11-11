package com.wongki.framework.http.lifecycle

import com.wongki.framework.http.base.IRequester

/**
 * http生命周期管理接口
 * @author  wangqi
 * date:    2019/6/18
 * email:   wangqi7676@163.com
 * desc:    .
 */
class HttpRequesterManager {
    private val mCaches by lazy { HashMap<IHttpLifecycleObserver, ArrayList<IRequester>>() }

    /**
     * 根据tag查找requester
     */
    private inline fun find(httpLifecycleObserver: IHttpLifecycleObserver, onFind: (MutableMap.MutableEntry<IHttpLifecycleObserver, java.util.ArrayList<IRequester>>) -> Unit) {
        val iterator = mCaches.iterator()
        while (iterator.hasNext()) {
            val next = iterator.next()
            val httpLifecycleObserverCached = next.key
            if (httpLifecycleObserverCached == httpLifecycleObserver) {
                onFind(next)
                return
            }
        }
    }

    /**
     * 添加单个requester，requester绑定到tag
     */
    fun addRequester(httpLifecycleObserver: IHttpLifecycleObserver, requester: IRequester) {
        find(httpLifecycleObserver) { cache ->
            cache.value.add(requester)
            return@addRequester
        }

        val key = httpLifecycleObserver
        val value = ArrayList<IRequester>()
        value.add(requester)
        mCaches[key] = value
    }

    /**
     * 根据httpLifecycleObserver移除单个requester
     */
    fun removeRequester(httpLifecycleObserver: IHttpLifecycleObserver, requester: IRequester) {
        find(httpLifecycleObserver) { cache ->
            val iterator = cache.value.iterator()
            while (iterator.hasNext()) {
                val next = iterator.next()
                val cacheRequest = next

                if (requester == cacheRequest) {
//                        if (!requester.isCancel()) {
//                            requester.cancel()
//                        }
                    iterator.remove()
                    return@removeRequester
                }

            }
            return@removeRequester
        }
    }

    /**
     * 取消该tag下绑定的所有request
     */
    fun cancelRequest(httpLifecycleObserver: IHttpLifecycleObserver) {
        val iterator = mCaches.iterator()
        while (iterator.hasNext()) {
            val next = iterator.next()
            val httpLifecycleObserverCached = next.key
            if (httpLifecycleObserverCached == httpLifecycleObserver) {
                iterator.remove()
                val requestIterator = next.value.iterator()
                while (requestIterator.hasNext()) {
                    val request = requestIterator.next()
                    request.cancel()
                    requestIterator.remove()
                }
                return
            }

        }
    }
}