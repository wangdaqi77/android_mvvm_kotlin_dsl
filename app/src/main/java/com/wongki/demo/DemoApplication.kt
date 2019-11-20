package com.wongki.demo

import android.util.Log
import com.wongki.framework.base.BaseApplication
import com.wongki.framework.http.CONTENTTYPE_JSON
import com.wongki.framework.http.exception.ApiException
import com.wongki.framework.http.httpGlobal
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

        /**
         * http全局配置
         */
        httpGlobal {
            newConfig {
                // tag
                tag = "http全局配置"
                // host 域名
                host = "https://api.apiopen.top"
                // 配置统一的Response class
                responseClass = MyResponse::class.java
                // 成功状态码，用于校验业务成功与否
                successfulCode = 200
                // 连接好事超时
                connectTimeOut = 10_000
                // 读取超时时间
                readTimeOut = 10_000
                // 写入超时时间
                writeTimeOut = 10_000

                // log
                log { message ->
                    Log.d("globalHttpConfig", message)
                }

                /**
                 * 响应体转换失败处理
                 * 当响应体结构转换失败时被触发
                 */
                onResponseConvertFailed { response, mediaType ->
                    var result: ApiException? = null
                    when (mediaType) {
                        CONTENTTYPE_JSON -> {
                            val myResponse = response.transform(MyResponse::class.java) { target ->
                                target.code = optInt("code", -1)
                                target.message = optString("message", "")
                            }

                            if (myResponse.code != -1) {
                                result = ApiException(myResponse.code, myResponse.message)
                            }
                        }
                    }
                    Log.e(
                        "onResponseConvertFailed", "解析结果:${result}\n" +
                                "mediaType:$mediaType, response:$response"
                    )
                    return@onResponseConvertFailed result
                }

                /**
                 * 全局的错误拦截
                 *
                 * 当请求失败时被触发
                 * 当返回true表示当前拦截处理
                 */
                addApiErrorInterceptor2FirstNode { code, message ->
                    when (code) {
                        // token 失效
                        1001 -> {
                            // 跳转登录页...
                            return@addApiErrorInterceptor2FirstNode true
                        }
                    }
                    return@addApiErrorInterceptor2FirstNode false

                }

                /**
                 * 添加公共的请求头
                 */
                addHeaders {
                    mutableMapOf(
                        "header" to "global",
                        "headerGlobal" to "global"
                    )
                }


                /**
                 * 添加公共的url参数
                 */
                addUrlQueryParams {
                    mutableMapOf(
                        "urlQueryParam" to "global",
                        "urlQueryParamGlobal" to "global"
                    )
                }
            }
        }
    }
}