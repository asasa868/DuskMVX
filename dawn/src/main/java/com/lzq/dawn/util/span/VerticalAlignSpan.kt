package com.lzq.dawn.util.span;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.style.ReplacementSpan;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * @Name :VerticalAlignSpan
 * @Time :2022/8/30 14:18
 * @Author :  Lzq
 * @Desc : 垂直对齐
 */
class VerticalAlignSpan extends ReplacementSpan {


    static final int ALIGN_CENTER = 2;
    static final int ALIGN_TOP = 3;

    final int mVerticalAlignment;

    VerticalAlignSpan(int verticalAlignment) {
        mVerticalAlignment = verticalAlignment;
    }

    @Override
    public int getSize(@NonNull Paint paint, CharSequence text, int start, int end, @Nullable Paint.FontMetricsInt fm) {
        text = text.subSequence(start, end);
        return (int) paint.measureText(text.toString());
    }

    @Override
    public void draw(@NonNull Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, @NonNull Paint paint) {
        text = text.subSequence(start, end);
        Paint.FontMetricsInt fm = paint.getFontMetricsInt();
        canvas.drawText(text.toString(), x, y - ((y + fm.descent + y + fm.ascent) / 2 - (bottom + top) / 2), paint);
    }
}
