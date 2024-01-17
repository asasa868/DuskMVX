package com.lzq.dawn.util.span

import android.graphics.Shader
import android.text.TextPaint
import android.text.style.CharacterStyle
import android.text.style.UpdateAppearance

/**
 * @Name :ShaderSpan
 * @Time :2022/8/30 14:32
 * @Author :  Lzq
 * @Desc :
 */
internal class ShaderSpan(private val mShader: Shader) : CharacterStyle(), UpdateAppearance {
    override fun updateDrawState(tp: TextPaint) {
        tp.shader = mShader
    }
}