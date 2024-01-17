package com.lzq.dawn.util.span

import android.graphics.Paint
import android.graphics.Typeface
import android.text.TextPaint
import android.text.style.TypefaceSpan

/**
 * @Name :CustomTypefaceSpan
 * @Time :2022/8/30 14:28
 * @Author :  Lzq
 * @Desc : 附加文本
 */
internal class CustomTypefaceSpan(private val newType: Typeface) : TypefaceSpan("") {
    override fun updateDrawState(textPaint: TextPaint) {
        apply(textPaint, newType)
    }

    override fun updateMeasureState(paint: TextPaint) {
        apply(paint, newType)
    }

    private fun apply(paint: Paint, tf: Typeface) {
        val oldStyle: Int
        val old = paint.typeface
        oldStyle = old?.style ?: 0
        val fake = oldStyle and tf.style.inv()
        if (fake and Typeface.BOLD != 0) {
            paint.isFakeBoldText = true
        }
        if (fake and Typeface.ITALIC != 0) {
            paint.textSkewX = -0.25f
        }
        paint.shader
        paint.typeface = tf
    }
}