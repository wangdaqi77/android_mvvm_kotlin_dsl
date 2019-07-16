package com.wongki.framework.extensions

import android.content.Context
import androidx.appcompat.app.AlertDialog
import java.lang.ref.WeakReference

/**
 * @author  wangqi
 * date:    2019/7/12
 * email:   wangqi7676@163.com
 * desc:    .
 */

private val Context.dialogs by lazy { HashMap<Int, WeakReference<AlertDialog?>?>() }

fun Context.dialogDismiss(seqNo: Int) {
    dialogs[seqNo]?.get()?.dismiss()
}

fun Context.showLoadingDialog(seqNo: Int, message: String = "加载中..."): AlertDialog {
    val dialog =
        AlertDialog.Builder(this)
                    .setMessage(message)
                    .setCancelable(true).create()
    dialogs[seqNo] = WeakReference(dialog)
    dialog.show()
    return dialog
}