package com.wongki.framework.extensions

import java.lang.ref.WeakReference
import kotlin.reflect.KProperty

class WeakDelegate<T>(initializer: () -> T?) {
    var obj: WeakReference<T?> = WeakReference(initializer())
    constructor() : this({ null })

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T? {
        return obj.get()
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T?) {
        obj = WeakReference(value)
    }
}
