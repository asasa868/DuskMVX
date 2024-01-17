package com.lzq.dawn.util.span;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.style.ReplacementSpan;

import androidx.annotation.NonNull;

import java.lang.ref.WeakReference;

/**
 * @Name :CustomDynamicDrawableSpan
 * @Time :2022/8/30 14:30
 * @Author :  Lzq
 * @Desc :
 */
 abstract class CustomDynamicDrawableSpan extends ReplacementSpan {

    static final int ALIGN_BOTTOM = 0;

    static final int ALIGN_BASELINE = 1;

    static final int ALIGN_CENTER = 2;

    static final int ALIGN_TOP = 3;

    final int mVerticalAlignment;

    public CustomDynamicDrawableSpan() {
        mVerticalAlignment = ALIGN_BOTTOM;
    }

    public CustomDynamicDrawableSpan(final int verticalAlignment) {
        mVerticalAlignment = verticalAlignment;
    }

    public abstract Drawable getDrawable();

    @Override
    public int getSize(@NonNull final Paint paint, final CharSequence text,
                       final int start, final int end, final Paint.FontMetricsInt fm) {
        Drawable d = getCachedDrawable();
        Rect rect = d.getBounds();
        if (fm != null) {
//                LogUtils.d("fm.top: " + fm.top,
//                        "fm.ascent: " + fm.ascent,
//                        "fm.descent: " + fm.descent,
//                        "fm.bottom: " + fm.bottom,
//                        "lineHeight: " + (fm.bottom - fm.top));
            int lineHeight = fm.bottom - fm.top;
            if (lineHeight < rect.height()) {
                if (mVerticalAlignment == ALIGN_TOP) {
                    fm.top = fm.top;
                    fm.bottom = rect.height() + fm.top;
                } else if (mVerticalAlignment == ALIGN_CENTER) {
                    fm.top = -rect.height() / 2 - lineHeight / 4;
                    fm.bottom = rect.height() / 2 - lineHeight / 4;
                } else {
                    fm.top = -rect.height() + fm.bottom;
                    fm.bottom = fm.bottom;
                }
                fm.ascent = fm.top;
                fm.descent = fm.bottom;
            }
        }
        return rect.right;
    }

    @Override
    public void draw(@NonNull final Canvas canvas, final CharSequence text,
                     final int start, final int end, final float x,
                     final int top, final int y, final int bottom, @NonNull final Paint paint) {
        Drawable d = getCachedDrawable();
        Rect rect = d.getBounds();
        canvas.save();
        float transY;
        int lineHeight = bottom - top;
//            LogUtils.d("rectHeight: " + rect.height(),
//                    "lineHeight: " + (bottom - top));
        if (rect.height() < lineHeight) {
            if (mVerticalAlignment == ALIGN_TOP) {
                transY = top;
            } else if (mVerticalAlignment == ALIGN_CENTER) {
                transY = (bottom + top - rect.height()) / 2;
            } else if (mVerticalAlignment == ALIGN_BASELINE) {
                transY = y - rect.height();
            } else {
                transY = bottom - rect.height();
            }
            canvas.translate(x, transY);
        } else {
            canvas.translate(x, top);
        }
        d.draw(canvas);
        canvas.restore();
    }

    private Drawable getCachedDrawable() {
        WeakReference<Drawable> wr = mDrawableRef;
        Drawable d = null;
        if (wr != null) {
            d = wr.get();
        }
        if (d == null) {
            d = getDrawable();
            mDrawableRef = new WeakReference<>(d);
        }
        return d;
    }

    private WeakReference<Drawable> mDrawableRef;
}
