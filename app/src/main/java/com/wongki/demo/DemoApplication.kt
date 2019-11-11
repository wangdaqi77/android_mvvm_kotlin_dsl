package com.wongki.demo

import android.util.Log
import com.wongki.framework.base.BaseApplication
import com.wongki.framework.http.exception.ApiException
import com.wongki.framework.http.global.globalHttpConfig
import com.wongki.framework.model.domain.CommonResponse
import com.wongki.framework.model.domain.MyResponse
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
            RESPONSE_SUB_CLASS = MyResponse::class.java
            CODE_API_SUCCESS = 200

            onConvertFailed { response, mediaType ->
                /**
                 * 当转换失败时被触发
                 * 在这里你需要把服务器的错误信息转换成ApiException，
                 * 如果没有有效的服务器错误信息需要返回null
                 */
                Log.e("onConvertFailed","mediaType:$mediaType")
                var code: Int = -1
                var msg: String = ""
                response.transform(MyResponse::class.java) { target ->
                    code = optInt("code", -1)
                    msg = optString("message", "")
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