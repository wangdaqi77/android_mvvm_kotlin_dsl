package com.wongki.framework.http.config

import com.wongki.framework.kt.PriorityValueGetter
import kotlin.reflect.KProperty

/**
 * @author  wangqi
 * date:    2019-11-12
 * email:   wangqi7676@163.com
 * 根据array的先后顺序获取值，只要不为null就返回
 */
class HttpConfigValueGetter<R>(initializer: () -> Array<IHttpConfig?>) :
    PriorityValueGetter<R, IHttpConfig>(
        initializer(),
        { obj, value, fieldName ->
//            if (BuildConfig.DEBUG) {
//                Log.e("HttpConfigValueGetter", "tag:${obj.tag}, $fieldName=$value")
//            }
        }

    ) {


    override operator fun getValue(thisRef: IHttpConfig, property: KProperty<*>): R? {
        return super.getValue(thisRef, property)
    }

}