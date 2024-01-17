package com.lzq.dawn.util.span

import android.text.TextPaint
import android.text.style.CharacterStyle
import android.text.style.UpdateAppearance

/**
 * @Name :ShadowSpan
 * @Time :2022/8/30 14:34
 * @Author :  Lzq
 * @Desc :
 */
internal class ShadowSpan(
    private val radius: Float, private val dx: Float, private val dy: Float, private val shadowColor: Int
) : CharacterStyle(), UpdateAppearance {
    override fun updateDrawState(tp: TextPaint) {
        tp.setShadowLayer(radius, dx, dy, shadowColor)
    }
}