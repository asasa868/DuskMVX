package com.lzq.dawn.util.span;

import android.text.TextPaint;
import android.text.style.CharacterStyle;
import android.text.style.UpdateAppearance;

/**
 * @Name :ShadowSpan
 * @Time :2022/8/30 14:34
 * @Author :  Lzq
 * @Desc :
 */
class ShadowSpan extends CharacterStyle implements UpdateAppearance {
    private final float radius;
    private final float dx;
    private final float dy;
    private final int shadowColor;

    ShadowSpan(final float radius,
               final float dx,
               final float dy,
               final int shadowColor) {
        this.radius = radius;
        this.dx = dx;
        this.dy = dy;
        this.shadowColor = shadowColor;
    }

    @Override
    public void updateDrawState(final TextPaint tp) {
        tp.setShadowLayer(radius, dx, dy, shadowColor);
    }
}