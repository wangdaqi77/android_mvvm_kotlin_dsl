package com.wongki.framework.http.retrofit.converter

import android.util.Log
import com.wongki.framework.http.exception.ApiException
import com.wongki.framework.model.domain.CommonResponse
import com.wongki.framework.http.HttpCode
import com.wongki.framework.http.exception.ParseResponseException
import com.google.gson.*
import com.google.gson.reflect.TypeToken
import com.wongki.framework.utils.transform
import okhttp3.MediaType
import okhttp3.ResponseBody
import retrofit2.Converter
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStreamReader
import java.nio.charset.Charset


class GsonResponseBodyConverter<T>(private val gson: Gson, private val adapter: TypeAdapter<T>) :
    Converter<ResponseBody, T> {
    companion object {
        private val TAG = GsonResponseBodyConverter::class.java.simpleName
        private val MEDIA_TYPE = MediaType.parse("application/json; charset=UTF-8")
        private val UTF_8 = Charset.forName("UTF-8")
    }

    @Suppress("UNCHECKED_CAST")
    @Throws(ApiException::class, IOException::class, ParseResponseException::class)
    override fun convert(responseBody: ResponseBody): T? {
        val response = responseBody.string()

        var commonResponse: CommonResponse<*>? = null
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
                val result = adapter.read(jsonReader)
                commonResponse = result as CommonResponse<*>


            } catch (e: Exception) {
                /**
                 * 第二次解析
                 */
                val jsonType = object : TypeToken<CommonResponse<*>>() {
                }.type
                try {
                    commonResponse = this.gson.fromJson<T>(response, jsonType) as CommonResponse<*>
                } catch (e: JsonSyntaxException) {
                    Log.d(TAG, "强制解析失败, Exception -> ${e.message}, response -> $response")
                }
            }
        } catch (e: Exception) {
            Log.d(TAG,  "response convert失败, Exception -> ${e.message}" )
        }

        // 解析失败
        if (commonResponse == null) {
            // 仅仅解析meta

            var code: Int = -1
            var msg: String = ""
            response.transform(CommonResponse::class.java) { target ->

                code = optInt("status", -1)
                msg = optString("msg", "")

            }
            if (code != -1) {
                throw ApiException(code, msg)
            }
            throw ParseResponseException("parse failed!")
        }

        val code = commonResponse.code
        val msg = commonResponse.message
        when (code) {
            HttpCode.SUCCESSFUL -> return commonResponse as T
            else -> {
                throw ApiException(code, msg)
            }
        }
    }
}