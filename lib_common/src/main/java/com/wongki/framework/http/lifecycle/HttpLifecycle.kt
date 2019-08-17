package com.wongki.framework.http.lifecycle

import com.wongki.framework.http.base.IRequester

/**
 * http生命周期管理接口
 * @author  wangqi
 * date:    2019/6/18
 * email:   wangqi7676@163.com
 * desc:    .
 */
class HttpLifecycle {
    private val mCaches by lazy { HashMap<IHttpLifecycleObserver, ArrayList<IRequester>>() }

    /**
     * 根据tag查找requester
     */
    private inline fun find(tag: IHttpLifecycleObserver, onFind: (MutableMap.MutableEntry<IHttpLifecycleObserver, java.util.ArrayList<IRequester>>) -> Unit) {
        val iterator = mCaches.iterator()
        while (iterator.hasNext()) {
            val next = iterator.next()
            val cacheTag = next.key
            if (cacheTag == tag) {
                onFind(next)
                return
            }
        }
    }


    /**
     * 添加单个requester，requester绑定到tag
     */
    @Synchronized
    fun addRequester(tag: IHttpLifecycleObserver, requester: IRequester) {
        find(tag) { cache ->
            cache.value.add(requester)
            return@addRequester
        }

        val key = tag
        val value = ArrayList<IRequester>()
        value.add(requester)
        mCaches[key] = value
    }

    /**
     * 取消request&&移除单个requester，requester取消绑定tag
     */
    @Synchronized
    fun removeRequester(tag: IHttpLifecycleObserver, requester: IRequester) {
        find(tag) { cache ->
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
    @Synchronized
    fun cancelRequest(tag: IHttpLifecycleObserver) {
        val iterator = mCaches.iterator()
        while (iterator.hasNext()) {
            val next = iterator.next()
            val key = next.key
            val cacheTag = key
            if (cacheTag == tag) {
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