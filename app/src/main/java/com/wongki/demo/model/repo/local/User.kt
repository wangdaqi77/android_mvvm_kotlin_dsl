package com.wongki.demo.model.repo.local

import com.wongki.framework.extensions.getApp
import com.wongki.framework.kpref.*

/**
 * @author  wangqi
 * date:    2019-10-30
 * email:   wangqi7676@163.com
 * desc:    .
 */

@DslMarker
annotation class UserDslMarker

@UserDslMarker
class UserPref(builder: KPrefBuilder) : KPref(builder) {

    init {
        initialize(getApp(), "user")
        reset()
    }


    var userName by kpref("user_name", "")

    var isLogin by kpref("is_login", false)

}

@UserDslMarker
object User {
    private var androidUserPref = UserPref(KPrefBuilderAndroid)
    private var memUserPref = UserPref(KPrefBuilderInMemory)

    /**
     * 保存用户数据
     */
    @UserDslMarker
    fun push(action: UserPref.() -> Unit) {
        androidUserPref.action()
        memUserPref.action()
    }

    /**
     * 获取用户数据
     */
    @UserDslMarker
    fun <T> pull(action: UserPref.() -> T): T {
        return memUserPref.action()
    }

}

@UserDslMarker
inline fun user(action: User.() -> Unit) {
    User.action()
}
