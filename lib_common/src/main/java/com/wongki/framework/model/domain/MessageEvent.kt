package com.wongki.framework.model.domain

/**
 * Created by lixuebo on 2018/12/12.
 */
data class MessageEvent(var code: Int, var arg1: String, var arg2: Any?) {

    companion object {
        const val MESSAGE_PUSH_RECEIVE_RED_POINT = 100 //收到推送消息展示消息中心入口红点
        const val APP_UPDATE_NOW = 101 //更新
        const val APP_UPDATE_AFTER = 102 //非强制更新
        const val APP_LOGOUT = 103 //登出
        const val HOMEREFRESHSTATUE = 104 //登出
        const val MINE_REFRESH = 105 //我的界面刷新
        const val GOTOMINE_EVENT = 106 //切换到我的界面
        const val BINDCARD_EVENT = 107 //解绑事件
        const val REGIST_EVENT = 108 //解绑事件
        const val REDWALLETLAYOUT = 109 //我的界面红包布局
        const val HOMECONFIRM_MONEY = 110 //我的界面红包布局
    }
}