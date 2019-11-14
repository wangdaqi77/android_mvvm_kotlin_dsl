package com.wongki.framework.kt

import kotlin.reflect.KProperty
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.javaField

/**
 * @author  wangqi
 * date:    2019-11-12
 * email:   wangqi7676@163.com
 * 根据obj的顺序获取值，只要不为null就返回
 * 注意：支持get，不支持set
 */
open class PriorityValueGetter<R, O : Any>(
    private var objArray: Array<O?>,
    private val onGet: ((O, R, String) -> Unit)? = null
) {
    open operator fun getValue(thisRef: O, property: KProperty<*>): R? {
        val iterator = objArray.iterator()
        while (iterator.hasNext()) {
            val obj = iterator.next() ?: continue
            val fieldName = property.name
//            val javaFieldName = property.javaField?.name //logger$delegate
            val objClass = obj::class
            val memberProperties = objClass.memberProperties
            memberProperties.forEach { p ->
                if (p.name == fieldName) {
                    val r = p.javaField
                    if (r != null) {
                        if (!r.isAccessible) {
                            r.isAccessible = true
                            val value = r.get(obj)
                            r.isAccessible = false
                            @Suppress("UNCHECKED_CAST")
                            if (value != null) {
                                value as R
                                onGet?.invoke(obj, value, fieldName)
                                return value
                            }
                        }
                        return@forEach
                    }
                }
            }
        }
        return null
    }

    operator fun setValue(innerConfig: O, property: KProperty<*>, value: R?) {
        throw IllegalArgumentException("仅支持get，不支持set")
    }
}