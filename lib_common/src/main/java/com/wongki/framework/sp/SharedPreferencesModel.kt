package com.feigeter.common.sp

import android.content.Context
import android.content.SharedPreferences
import com.wongki.framework.extensions.getApp
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * @author  wangqi
 * date:    2019/7/11
 * email:   wangqi7676@163.com
 * desc:    .
 */

object SharedPreferencesModel {

    private val preferences: SharedPreferences = getApp().getSharedPreferences("config", Context.MODE_PRIVATE)

    fun int(defaultValue: Int = 0) = object : ReadWriteProperty<SharedPreferencesModel, Int> {

        override fun getValue(thisRef: SharedPreferencesModel, property: KProperty<*>): Int {
            return thisRef.preferences.getInt(property.name, defaultValue)
        }

        override fun setValue(thisRef: SharedPreferencesModel, property: KProperty<*>, value: Int) {
            thisRef.preferences.edit().putInt(property.name, value).apply()
        }
    }

    fun long(defaultValue: Long = 0L) = object : ReadWriteProperty<SharedPreferencesModel, Long> {

        override fun getValue(thisRef: SharedPreferencesModel, property: KProperty<*>): Long {
            return thisRef.preferences.getLong(property.name, defaultValue)
        }

        override fun setValue(thisRef: SharedPreferencesModel, property: KProperty<*>, value: Long) {
            thisRef.preferences.edit().putLong(property.name, value).apply()
        }
    }

    fun boolean(defaultValue: Boolean = false) = object : ReadWriteProperty<SharedPreferencesModel, Boolean> {
        override fun getValue(thisRef: SharedPreferencesModel, property: KProperty<*>): Boolean {
            return thisRef.preferences.getBoolean(property.name, defaultValue)
        }

        override fun setValue(thisRef: SharedPreferencesModel, property: KProperty<*>, value: Boolean) {
            thisRef.preferences.edit().putBoolean(property.name, value).apply()
        }
    }

    fun float(defaultValue: Float = 0.0f) = object : ReadWriteProperty<SharedPreferencesModel, Float> {
        override fun getValue(thisRef: SharedPreferencesModel, property: KProperty<*>): Float {
            return thisRef.preferences.getFloat(property.name, defaultValue)
        }

        override fun setValue(thisRef: SharedPreferencesModel, property: KProperty<*>, value: Float) {
            thisRef.preferences.edit().putFloat(property.name, value).apply()
        }
    }

    fun string(defaultValue: String? = null) = object : ReadWriteProperty<SharedPreferencesModel, String?> {
        override fun getValue(thisRef: SharedPreferencesModel, property: KProperty<*>): String? {
            return thisRef.preferences.getString(property.name, defaultValue)
        }

        override fun setValue(thisRef: SharedPreferencesModel, property: KProperty<*>, value: String?) {
            thisRef.preferences.edit().putString(property.name, value).apply()
        }
    }

    fun setString(defaultValue: Set<String>? = null) = object : ReadWriteProperty<SharedPreferencesModel, Set<String>?> {
        override fun getValue(thisRef: SharedPreferencesModel, property: KProperty<*>): Set<String>? {
            return thisRef.preferences.getStringSet(property.name, defaultValue)
        }

        override fun setValue(thisRef: SharedPreferencesModel, property: KProperty<*>, value: Set<String>?) {
            thisRef.preferences.edit().putStringSet(property.name, value).apply()
        }
    }
}
