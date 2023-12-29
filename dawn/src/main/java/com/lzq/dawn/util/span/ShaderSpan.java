package com.lzq.dawn.util.span;

import android.graphics.Shader;
import android.text.TextPaint;
import android.text.style.CharacterStyle;
import android.text.style.UpdateAppearance;

/**
 * @Name :ShaderSpan
 * @Time :2022/8/30 14:32
 * @Author :  Lzq
 * @Desc :
 */
 class ShaderSpan extends CharacterStyle implements UpdateAppearance {
    private final Shader mShader;

    public ShaderSpan(final Shader shader) {
        this.mShader = shader;
    }

    @Override
    public void updateDrawState(final TextPaint tp) {
        tp.setShader(mShader);
    }
}