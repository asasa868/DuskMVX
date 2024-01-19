package com.lzq.dawn.util.span

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Paint.FontMetricsInt
import android.text.style.ReplacementSpan

/**
 * @Name :VerticalAlignSpan
 * @Time :2022/8/30 14:18
 * @Author :  Lzq
 * @Desc : 垂直对齐
 */
internal class VerticalAlignSpan(val mVerticalAlignment: Int) : ReplacementSpan() {
    override fun getSize(paint: Paint, textStr: CharSequence, start: Int, end: Int, fm: FontMetricsInt?): Int {
        var text = textStr
        text = text.subSequence(start, end)
        return paint.measureText(text.toString()).toInt()
    }

    override fun draw(
        canvas: Canvas,
        textStr: CharSequence,
        start: Int,
        end: Int,
        x: Float,
        top: Int,
        y: Int,
        bottom: Int,
        paint: Paint
    ) {
        var text = textStr
        text = text.subSequence(start, end)
        val fm = paint.fontMetricsInt
        canvas.drawText(
            text.toString(),
            x,
            (y - ((y + fm.descent + y + fm.ascent) / 2 - (bottom + top) / 2)).toFloat(),
            paint
        )
    }

    companion object {
        const val ALIGN_CENTER = 2
        const val ALIGN_TOP = 3
    }
}