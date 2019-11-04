/*
 * Copyright 2018 Allan Wang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wongki.framework.utils

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import androidx.annotation.IntRange
import java.io.Closeable
import java.math.RoundingMode
import java.text.DecimalFormat

/**
 * Created by Allan Wang on 2017-05-28.
 */

/**
 * Markers to isolate respective extension @UtilsDslMarker functions to their extended class
 * Avoids having a whole bunch of methods for nested calls
 */
@DslMarker
annotation class UtilsDslMarker


inline val Float.dpToPx: Float
    get() = this * Resources.getSystem().displayMetrics.density

@UtilsDslMarker
inline val Int.dpToPx: Int
    get() = toFloat().dpToPx.toInt()

@UtilsDslMarker
inline val Float.pxToDp: Float
    get() = this / Resources.getSystem().displayMetrics.density

@UtilsDslMarker
inline val Int.pxToDp: Int
    get() = toFloat().pxToDp.toInt()

@UtilsDslMarker
inline val Float.dpToSp: Float
    get() = this * Resources.getSystem().displayMetrics.scaledDensity

@UtilsDslMarker
inline val Int.dpToSp: Int
    get() = toFloat().dpToSp.toInt()

@UtilsDslMarker
inline val Float.spToDp: Float
    get() = this / Resources.getSystem().displayMetrics.scaledDensity

@UtilsDslMarker
inline val Int.spToDp: Int
    get() = toFloat().spToDp.toInt()


@UtilsDslMarker
fun Number.round(@IntRange(from = 1L) decimalCount: Int): String {
    val expression = StringBuilder().append("#.")
    (1..decimalCount).forEach { expression.append("#") }
    val formatter = DecimalFormat(expression.toString())
    formatter.roundingMode = RoundingMode.HALF_UP
    return formatter.format(this)
}

/**
 * Extracts the bitmap of a drawable, and applies a scale if given
 * For solid colors, a 1 x 1 pixel will be generated
 */
@UtilsDslMarker
fun Drawable.toBitmap(
    scaling: Float = 1f,
    config: Bitmap.Config = Bitmap.Config.ARGB_8888
): Bitmap {
    if (this is BitmapDrawable && bitmap != null) {
        if (scaling == 1f) return bitmap
        val width = (bitmap.width * scaling).toInt()
        val height = (bitmap.height * scaling).toInt()
        return Bitmap.createScaledBitmap(bitmap, width, height, false)
    }
    val bitmap = if (intrinsicWidth <= 0 || intrinsicHeight <= 0)
        Bitmap.createBitmap(1, 1, config)
    else
        Bitmap.createBitmap(
            (intrinsicWidth * scaling).toInt(),
            (intrinsicHeight * scaling).toInt(),
            config
        )
    val canvas = Canvas(bitmap)
    setBounds(0, 0, bitmap.width, bitmap.height)
    draw(canvas)
    return bitmap
}

/**
 * Use block for autocloseables
 */
@UtilsDslMarker
inline fun <T : AutoCloseable, R> T.use(block: (T) -> R): R {
    var closed = false
    try {
        return block(this)
    } catch (e: Exception) {
        closed = true
        try {
            close()
        } catch (closeException: Exception) {
            e.addSuppressed(closeException)
        }
        throw e
    } finally {
        if (!closed) {
            close()
        }
    }
}


@UtilsDslMarker
fun Closeable?.safeClose() {
    if (this == null) return
    try {
        close()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}