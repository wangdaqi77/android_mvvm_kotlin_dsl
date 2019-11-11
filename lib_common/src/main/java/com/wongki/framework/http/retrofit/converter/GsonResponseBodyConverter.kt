package com.wongki.framework.http.retrofit.converter

import android.util.Log
import com.wongki.framework.http.exception.ApiException
import com.wongki.framework.model.domain.CommonResponse
import com.wongki.framework.http.HttpCode
import com.wongki.framework.http.exception.ParseResponseException
import com.google.gson.*
import com.wongki.framework.http.global.GlobalHttpConfig
import com.wongki.framework.http.retrofit.observer.HttpCommonObserver
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
        val mediaType = responseBody.contentType().toString()
        var convertResult: Any? = null
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
                convertResult = result


            } catch (e: Exception) {
                Log.d(TAG, "第一次解析失败, Exception -> ${e.message}, response -> $response")
            }
        } catch (e: Exception) {
            Log.d(TAG,  "response convert失败, Exception -> ${e.message}" )
        }

        /**
         * 解析失败，需要处理解析失败，分为三步
         * 1.解析结果为null，交给外层专门解析服务器的错误信息，解析到了抛异常
         * 2.解析结果为null，抛解析错误的异常
         * 3.解析的结构并不是约定的[CommonResponse]的子类，抛解析错误的异常
         *
         * 观察器中会捕获到这些异常[HttpCommonObserver.onError]
         */

        // 1.交给外层专门解析错误
        if (convertResult == null) {

            val apiException
                    = GlobalHttpConfig.onResponseConvertFailedListener?.onConvertFailed(response,mediaType)

            if (apiException != null) {

                /**
                 * 解析失败 && 服务器返回成功
                 * 这里的处理主要兼容服务器的数据返回空字符串，导致解析后和定义的类型不一致导致的错误。
                 * ex：定义result为User类型，实际返回{"code":200,"message":"成功!","result":""}
                 */
                if (GlobalHttpConfig.CODE_API_SUCCESS == apiException.code) {
                    val responseSubClass = GlobalHttpConfig.RESPONSE_SUB_CLASS
                    val newInstance = responseSubClass.newInstance()
                    newInstance.code = apiException.code
                    newInstance.message = apiException.message
                    return newInstance as T
                }else{
                    throw apiException
                }
            }
        }

        // 2. 有可能是定义的响应结构与服务器返回的类型不一致造成的.
        // ex：定义result为User，实际返回{"code":200,"message":"成功!","result":""}
        if (convertResult == null) {
            throw ParseResponseException("解析失败")
        }

        // 3.解析的结构并不是约束的CommonResponse的子类
        if (convertResult !is CommonResponse<*>) {
            throw ParseResponseException("解析失败，必须解析成CommonResponse的实现类，请检查你定义Service接口api方法的返回值")
        }

        val code = convertResult.code
        val msg = convertResult.message
        when (code) {
            GlobalHttpConfig.CODE_API_SUCCESS -> return convertResult as T
            else -> {
                throw ApiException(code, msg)
            }
        }
    }
}