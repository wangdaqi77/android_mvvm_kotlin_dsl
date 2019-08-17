package com.wongki.framework.http.retrofit.converter

import android.util.Log
import com.wongki.framework.http.exception.ApiException
import com.wongki.framework.model.domain.CommonResponse
import com.wongki.framework.http.HttpCode
import com.wongki.framework.http.exception.ParseResponseException
import com.wongki.framework.utils.LogUtil
import com.google.gson.*
import com.google.gson.reflect.TypeToken
import okhttp3.MediaType
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Converter
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStreamReader
import java.lang.reflect.ParameterizedType
import java.nio.charset.Charset

/**
 * @author  wangqi
 * date:    2019/6/12
 * email:   wangqi7676@163.com
 * desc:    .
 */
class GsonResponseBodyConverter<T>(private val gson: Gson, private val adapter: TypeAdapter<T>) : Converter<ResponseBody, T> {
    companion object {
        private val MEDIA_TYPE = MediaType.parse("application/json; charset=UTF-8")
        private val UTF_8 = Charset.forName("UTF-8")
    }

    @Suppress("UNCHECKED_CAST")
    @Throws(ApiException::class, IOException::class, ParseResponseException::class)
    override fun convert(responseBody: ResponseBody): T? {
        val response = responseBody.string()
//        val response = "{\"code\":200,\"msg\":\"查询成功\",\"data\":[]}"
//        val response = "{\"code\":200,\"msg\":\"查询成功\",\"data\":{}}"
//        val response = "{\"code\":200,\"msg\":\"查询成功\",\"data\":\"\"}"


        var feigeterResponse: CommonResponse<*>? = null
        try {
            /**
             * 第一次解析
             */
            try {
                val contentType = responseBody.contentType()
                val charset = if (contentType != null) contentType.charset(UTF_8) else UTF_8
                val inputStream = ByteArrayInputStream(response.toByteArray())
                val reader = InputStreamReader(inputStream, charset!!)
                val jsonReader = gson.newJsonReader(reader)
                val read = adapter.read(jsonReader)
                feigeterResponse = read as CommonResponse<*>


            } catch (e: JsonSyntaxException) {

                /**
                 * 第二次解析
                 *
                 * 这里是为了兼容非常"可爱"的后台数据，下面举个例子：CommonResponse<T> 当T的类型是一个数组时，
                 * 服务器查到的数据为0个元素，正确的json格式应该是"data":[]，但是现实呢往往是残酷的，服务器此时有可能返回"data":"" ，
                 * 所以一定会解析错误，但是我们APP此时应该显示空列表页面而不是提示用户解析错误，所以就有了以下兼容处理逻辑
                 * 再此如果解析成功真实的类型应该是CommonResponse<String>，那么就需要在外层处理一下
                 *
                 * ps:如果开发前期和后台协商好了，按照正确的json格式返回，完全可以忽略这个逻辑。
                 */
                val jsonType = object : TypeToken<CommonResponse<*>>() {
                }.type
                try {
                    feigeterResponse = this.gson.fromJson<T>(response, jsonType) as CommonResponse<*>
                } catch (e: JsonSyntaxException) {
                    Log.w("强制解析失败", "Exception -> ${e.message}, response -> $response")
                }
            }
        } catch (e: Exception) {
            LogUtil.w("response convert失败", "Exception -> ${e.message}")
        }

        // 解析失败
        if (feigeterResponse == null) {
            throw ParseResponseException("parse failed!")
        }

        val code = feigeterResponse.code
        val msg = feigeterResponse.message
        when (code) {
            /*HttpCode.JSON_REQUEST_SUCCESSFUL, */HttpCode.SUCCESSFUL -> return feigeterResponse as T
            else -> {
                throw ApiException(code, msg)
            }
        }
    }
}