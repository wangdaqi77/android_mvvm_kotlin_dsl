package com.wongki.framework.debug

import android.util.Log
import com.wongki.framework.BuildConfig
import com.wongki.framework.logging.KL
import kotlin.reflect.jvm.jvmName

/**
 * @author  wangqi
 * date:    2019-11-07
 * email:   wangqi7676@163.com
 * desc:    .
 */
internal val isDebug = BuildConfig.DEBUG


/*
val arrayList = ArrayList<ArrayList<SearchMusic.Response.Item>>()
java.simpleName:ArrayList
java.name:java.util.ArrayList
java.canonicalName:java.util.ArrayList
java.typeName:java.util.ArrayList
java.typeParameters:1,E

kClass.jvmName:java.util.ArrayList
kClass.simpleName:ArrayList
kClass.qualifiedName:java.util.ArrayList
kClass.typeParameters:1,E

val arrayList = arrayOf<CommonResponse<SearchMusic.Response.Item>>()
java.simpleName:CommonResponse[]
java.name:[Lcom.wongki.framework.model.domain.CommonResponse;
java.canonicalName:com.wongki.framework.model.domain.CommonResponse[]
java.typeName:com.wongki.framework.model.domain.CommonResponse[]
java.typeParameters:
kClass.jvmName:[Lcom.wongki.framework.model.domain.CommonResponse;
kClass.simpleName:Array
kClass.qualifiedName:kotlin.Array
kClass.typeParameters:1,T
 */

fun (Any.() -> Any).getMethodName(): String {
    return "getMethodName"
}

fun Any.printClassInfo() {
    if (!isDebug) return


    val kClass = this::class
    val typeParameters = kClass.typeParameters
    var kclassTypePs = ""
    typeParameters.forEachIndexed { index, kTypeParameter ->
        kclassTypePs += "${index + 1},${kTypeParameter.name}\n"
    }

    val java = this.javaClass
    val typeParametersJava = java.typeParameters

    var javaTypePs = ""
    typeParametersJava.forEachIndexed { index, typeParameter ->
        javaTypePs += "${index + 1},${typeParameter.name}\n"
    }



    KL.test {
        "\njava.simpleName:${java.simpleName}\n" +
                "java.name:${java.name}\n" +
                "java.canonicalName:${java.canonicalName}\n" +
                "java.typeName:${java.typeName}\n" +
                "java.typeParameters:${javaTypePs}\n" +
                "kClass.jvmName:${kClass.jvmName}\n" +
                "kClass.simpleName:${kClass.simpleName}\n" +
                "kClass.qualifiedName:${kClass.qualifiedName}\n" +
                "kClass.typeParameters:${kclassTypePs}\n"
    }

}