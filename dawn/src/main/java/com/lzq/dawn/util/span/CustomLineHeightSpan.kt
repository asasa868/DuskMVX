package com.lzq.dawn.util.span

import android.graphics.Paint.FontMetricsInt
import android.text.Spanned
import android.text.style.LineHeightSpan

/**
 * @Name :CustomLineHeightSpan
 * @Time :2022/8/30 14:22
 * @Author :  Lzq
 * @Desc : 段落行高
 */
internal class CustomLineHeightSpan(private val height: Int, val mVerticalAlignment: Int) : LineHeightSpan {
    override fun chooseHeight(
        text: CharSequence, start: Int, end: Int, spanstartv: Int, v: Int, fm: FontMetricsInt
    ) {
//            LogUtils.e(fm, sfm);
        if (sfm == null) {
            sfm = FontMetricsInt()
            sfm!!.top = fm.top
            sfm!!.ascent = fm.ascent
            sfm!!.descent = fm.descent
            sfm!!.bottom = fm.bottom
            sfm!!.leading = fm.leading
        } else {
            fm.top = sfm!!.top
            fm.ascent = sfm!!.ascent
            fm.descent = sfm!!.descent
            fm.bottom = sfm!!.bottom
            fm.leading = sfm!!.leading
        }
        var need = height - (v + fm.descent - fm.ascent - spanstartv)
        if (need > 0) {
            if (mVerticalAlignment == ALIGN_TOP) {
                fm.descent += need
            } else if (mVerticalAlignment == ALIGN_CENTER) {
                fm.descent += need / 2
                fm.ascent -= need / 2
            } else {
                fm.ascent -= need
            }
        }
        need = height - (v + fm.bottom - fm.top - spanstartv)
        if (need > 0) {
            if (mVerticalAlignment == ALIGN_TOP) {
                fm.bottom += need
            } else if (mVerticalAlignment == ALIGN_CENTER) {
                fm.bottom += need / 2
                fm.top -= need / 2
            } else {
                fm.top -= need
            }
        }
        if (end == (text as Spanned).getSpanEnd(this)) {
            sfm = null
        }
        //            LogUtils.e(fm, sfm);
    }

    companion object {
        const val ALIGN_CENTER = 2
        const val ALIGN_TOP = 3
        var sfm: FontMetricsInt? = null
    }
}