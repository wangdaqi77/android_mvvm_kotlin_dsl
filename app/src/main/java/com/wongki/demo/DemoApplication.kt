package com.wongki.demo

import android.util.Log
import com.wongki.framework.base.BaseApplication
import com.wongki.framework.http.exception.ApiException
import com.wongki.framework.http.global.globalHttpConfig
import com.wongki.framework.model.domain.CommonResponse
import com.wongki.framework.utils.transform

/**
 * @author  wangqi
 * date:    2019/6/26
 * email:   wangqi7676@163.com
 * desc:    .
 */
class DemoApplication : BaseApplication() {

    override fun onCreate() {
        super.onCreate()
        initHttp()
    }

    private fun initHttp() {
        globalHttpConfig {

            onConvertFailed { response, mediaType ->
                /**
                 * 当转换失败时被触发
                 * 在这里你需要把服务器的错误码转换成ApiException，如果没有有效的错误信息可以返回null
                 */
                Log.e("onConvertFailed","mediaType:$mediaType")
                var code: Int = -1
                var msg: String = ""
                response.transform(CommonResponse::class.java) { target ->
                    code = optInt("status", -1)
                    msg = optString("msg", "")
                }

                if (code != -1) {
                    return@onConvertFailed ApiException(code, msg)
                }
                return@onConvertFailed null
            }



            onErrorIntercept { code, message ->
                /**
                 * 当请求失败时被触发
                 * 当返回true表示当前拦截处理
                 */
                when (code) {
                    // token 失效
                    1001->{
                        // 跳转登录页...
                        return@onErrorIntercept true
                    }
                }
                return@onErrorIntercept false

            }
        }
    }
}