package com.wongki.framework.mvvm.lifecycle.exception


/**
 * @author  wangqi
 * date:    2019-11-05
 * email:   wangqi7676@163.com
 * desc:    .
 */
class DslRejectedException(dslParentMethod: String, dslMethod: String, dslSubParam: String = "") :
    Exception(
        "未设置参数，请检查参数:\n" +
                "$dslParentMethod {" +
                "   $dslMethod{" +
                "       $dslSubParam..." +
                "   }" +
                "}"
    )
