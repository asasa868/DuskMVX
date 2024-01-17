package com.lzq.dawn.util.span;

import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.style.TypefaceSpan;

/**
 * @Name :CustomTypefaceSpan
 * @Time :2022/8/30 14:28
 * @Author :  Lzq
 * @Desc : 附加文本
 */

class CustomTypefaceSpan extends TypefaceSpan {

    private final Typeface newType;

    CustomTypefaceSpan(final Typeface type) {
        super("");
        newType = type;
    }

    @Override
    public void updateDrawState(final TextPaint textPaint) {
        apply(textPaint, newType);
    }

    @Override
    public void updateMeasureState(final TextPaint paint) {
        apply(paint, newType);
    }


    private void apply(final Paint paint, final Typeface tf) {
        int oldStyle;
        Typeface old = paint.getTypeface();
        if (old == null) {
            oldStyle = 0;
        } else {
            oldStyle = old.getStyle();
        }

        int fake = oldStyle & ~tf.getStyle();
        if ((fake & Typeface.BOLD) != 0) {
            paint.setFakeBoldText(true);
        }

        if ((fake & Typeface.ITALIC) != 0) {
            paint.setTextSkewX(-0.25f);
        }

        paint.getShader();

        paint.setTypeface(tf);
    }
}