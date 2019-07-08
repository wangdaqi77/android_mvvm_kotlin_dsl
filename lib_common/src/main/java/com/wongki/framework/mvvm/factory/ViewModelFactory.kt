package com.wongki.framework.mvvm.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.lang.reflect.Method

/**
 * @author  wangqi
 * date:    2019/7/3
 * email:   wangqi7676@163.com
 * desc:
 *          该类的主要作用是：创建ViewModel和桥接View和ViewModel，降低耦合
 *          利用动态代理或者其他技术来进行数据交换
 *
 */
abstract class ViewModelFactory<PROXY_INTERFACE> : ViewModelProvider.Factory {
    companion object {
        val ARGS_EMPTY = arrayOf<Any>()
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {

        return modelClass.newInstance()
//        val genericSuperclass = this.javaClass.genericSuperclass as ParameterizedType
//        val realProxyInterfaceType = genericSuperclass.actualTypeArguments[0] as Class<PROXY_INTERFACE>
//        Proxy.newProxyInstance(modelClass.classLoader, arrayOf(modelClass)) { instance, method, args ->
//            if (needProxy(realProxyInterfaceType, method)) {
//                loadModelMethod(method).invoke(args ?: ARGS_EMPTY)
//            } else {
//                method.invoke(instance, args)
//            }
//
//        } as T
    }

    private fun needProxy(realProxyInterfaceType: Class<PROXY_INTERFACE>, method: Method): Boolean {
       return  realProxyInterfaceType.declaredMethods.contains(method)
//        return try {
//            realProxyInterfaceType.getDeclaredMethod(method.name, *method.parameterTypes)
//            true
//        } catch (e: Exception) {
//            e.printStackTrace()
//            false
//        }
    }


    private fun loadModelMethod(method: Method): ViewModelMethod {
        return ViewModelMethod(method)
    }

    /**
     * 是否需要适配
     */
    abstract fun adapt(modelMethod: ViewModelMethod, args: Array<Any>): Any?

    inner class ViewModelMethod(val method: Method) {
        fun invoke(args: Array<Any>): Any? {
            return adapt(this, args)
        }
    }

}