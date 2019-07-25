package com.wongki.framework.extensions

import android.app.Activity
import android.app.Dialog
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import java.lang.ref.WeakReference

/**
 * @author  wangqi
 * date:    2019/7/12
 * email:   wangqi7676@163.com
 * desc:    .
 */

private val Activity.dialogs by lazy { HashMap<String, WeakReference<AlertDialog?>?>() }

fun Dialog.dialogShow(dialogKey: String) {
    if (!isShowing) {
        Log.e("dialog", "dialogShow()   -> $dialogKey")
        show()
    }
}

fun Dialog.dialogDismiss(dialogKey: String) {
    if (isShowing) {
        Log.e("dialog", "dialogDismiss()   -> $dialogKey")
        dismiss()
    }
}

/**
 * @param seqNo 缓存dialog的唯一标识
 */
fun Activity.dialogDismiss(seqNo: Int) {
    val dialogKey = getDialogKey(seqNo)
    dialogs[dialogKey]?.get()?.dialogDismiss(dialogKey)
}


/**
 * @param seqNo 缓存dialog的唯一标识
 */
private fun Any.getDialogKey(seqNo: Int) = "${this.javaClass}:$seqNo"

/**
 * @param seqNo 缓存dialog的唯一标识
 */
fun Activity.showLoadingDialog(seqNo: Int, message: String = "加载中..."): AlertDialog {

    val dialogKey = getDialogKey(seqNo)
    var dialog = dialogs[dialogKey]?.get()
    if (dialog?.isShowing == true) {
        return dialog
    }
    dialog = createDialog(message)
    dialogs[dialogKey] = WeakReference(dialog)
    dialog.dialogShow(dialogKey)
    return dialog
}

/**
 * @param seqNo 缓存dialog的唯一标识
 */
fun Fragment.dialogDismiss(seqNo: Int) {
    val activity = activity ?: return
    val dialogKey = getDialogKey(seqNo)
    activity.dialogs[dialogKey]?.get()?.dialogDismiss(dialogKey)
}

/**
 * @param seqNo 缓存dialog的唯一标识
 */
fun Fragment.showLoadingDialog(seqNo: Int, message: String = "加载中..."): AlertDialog? {

    val activity = activity ?: return null

    val dialogKey = getDialogKey(seqNo)
    var dialog = activity.dialogs[dialogKey]?.get()
    if (dialog?.isShowing == true) {
        return dialog
    }
    dialog = activity.createDialog(message)

    activity.dialogs[dialogKey] = java.lang.ref.WeakReference(dialog)
    dialog.dialogShow(dialogKey)
    return dialog
}

fun Activity.createDialog(message: String): AlertDialog {
    return AlertDialog.Builder(this)
        .setMessage(message)
        .setCancelable(false)
        .create()
}