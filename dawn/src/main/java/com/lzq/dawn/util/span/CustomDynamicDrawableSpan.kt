package com.lzq.dawn.util.span

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Paint.FontMetricsInt
import android.graphics.drawable.Drawable
import android.text.style.ReplacementSpan
import java.lang.ref.WeakReference

/**
 * @Name :CustomDynamicDrawableSpan
 * @Time :2022/8/30 14:30
 * @Author :  Lzq
 * @Desc :
 */
internal abstract class CustomDynamicDrawableSpan : ReplacementSpan {
    val mVerticalAlignment: Int

    constructor() {
        mVerticalAlignment = ALIGN_BOTTOM
    }

    constructor(verticalAlignment: Int) {
        mVerticalAlignment = verticalAlignment
    }

    abstract val drawable: Drawable?
    override fun getSize(
        paint: Paint, text: CharSequence, start: Int, end: Int, fm: FontMetricsInt?
    ): Int {
        val d = cachedDrawable
        val rect = d!!.bounds
        if (fm != null) {
//                LogUtils.d("fm.top: " + fm.top,
//                        "fm.ascent: " + fm.ascent,
//                        "fm.descent: " + fm.descent,
//                        "fm.bottom: " + fm.bottom,
//                        "lineHeight: " + (fm.bottom - fm.top));
            val lineHeight = fm.bottom - fm.top
            if (lineHeight < rect.height()) {
                if (mVerticalAlignment == ALIGN_TOP) {
                    fm.top = fm.top
                    fm.bottom = rect.height() + fm.top
                } else if (mVerticalAlignment == ALIGN_CENTER) {
                    fm.top = -rect.height() / 2 - lineHeight / 4
                    fm.bottom = rect.height() / 2 - lineHeight / 4
                } else {
                    fm.top = -rect.height() + fm.bottom
                    fm.bottom = fm.bottom
                }
                fm.ascent = fm.top
                fm.descent = fm.bottom
            }
        }
        return rect.right
    }

    override fun draw(
        canvas: Canvas,
        text: CharSequence,
        start: Int,
        end: Int,
        x: Float,
        top: Int,
        y: Int,
        bottom: Int,
        paint: Paint
    ) {
        val d = cachedDrawable
        val rect = d!!.bounds
        canvas.save()
        val transY: Float
        val lineHeight = bottom - top
        //            LogUtils.d("rectHeight: " + rect.height(),
//                    "lineHeight: " + (bottom - top));
        if (rect.height() < lineHeight) {
            transY = if (mVerticalAlignment == ALIGN_TOP) {
                top.toFloat()
            } else if (mVerticalAlignment == ALIGN_CENTER) {
                ((bottom + top - rect.height()) / 2).toFloat()
            } else if (mVerticalAlignment == ALIGN_BASELINE) {
                (y - rect.height()).toFloat()
            } else {
                (bottom - rect.height()).toFloat()
            }
            canvas.translate(x, transY)
        } else {
            canvas.translate(x, top.toFloat())
        }
        d.draw(canvas)
        canvas.restore()
    }

    private val cachedDrawable: Drawable?
        private get() {
            val wr = mDrawableRef
            var d: Drawable? = null
            if (wr != null) {
                d = wr.get()
            }
            if (d == null) {
                d = drawable
                mDrawableRef = WeakReference(d)
            }
            return d
        }
    private var mDrawableRef: WeakReference<Drawable?>? = null

    companion object {
        const val ALIGN_BOTTOM = 0
        const val ALIGN_BASELINE = 1
        const val ALIGN_CENTER = 2
        const val ALIGN_TOP = 3
    }
}