package com.lzq.dawn.util.span;

import android.graphics.Paint;
import android.text.Spanned;
import android.text.style.LineHeightSpan;

/**
 * @Name :CustomLineHeightSpan
 * @Time :2022/8/30 14:22
 * @Author :  Lzq
 * @Desc : 段落行高
 */
 class CustomLineHeightSpan implements LineHeightSpan {


    private final int height;

    static final int ALIGN_CENTER = 2;
    static final int ALIGN_TOP    = 3;

    final  int                  mVerticalAlignment;
    static Paint.FontMetricsInt sfm;

    CustomLineHeightSpan(int height, int verticalAlignment) {
        this.height = height;
        mVerticalAlignment = verticalAlignment;
    }

    @Override
    public void chooseHeight(final CharSequence text, final int start, final int end,
                             final int spanstartv, final int v, final Paint.FontMetricsInt fm) {
//            LogUtils.e(fm, sfm);
        if (sfm == null) {
            sfm = new Paint.FontMetricsInt();
            sfm.top = fm.top;
            sfm.ascent = fm.ascent;
            sfm.descent = fm.descent;
            sfm.bottom = fm.bottom;
            sfm.leading = fm.leading;
        } else {
            fm.top = sfm.top;
            fm.ascent = sfm.ascent;
            fm.descent = sfm.descent;
            fm.bottom = sfm.bottom;
            fm.leading = sfm.leading;
        }
        int need = height - (v + fm.descent - fm.ascent - spanstartv);
        if (need > 0) {
            if (mVerticalAlignment == ALIGN_TOP) {
                fm.descent += need;
            } else if (mVerticalAlignment == ALIGN_CENTER) {
                fm.descent += need / 2;
                fm.ascent -= need / 2;
            } else {
                fm.ascent -= need;
            }
        }
        need = height - (v + fm.bottom - fm.top - spanstartv);
        if (need > 0) {
            if (mVerticalAlignment == ALIGN_TOP) {
                fm.bottom += need;
            } else if (mVerticalAlignment == ALIGN_CENTER) {
                fm.bottom += need / 2;
                fm.top -= need / 2;
            } else {
                fm.top -= need;
            }
        }
        if (end == ((Spanned) text).getSpanEnd(this)) {
            sfm = null;
        }
//            LogUtils.e(fm, sfm);
    }
}
