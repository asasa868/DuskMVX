package com.lzq.dawn.util.span

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Paint.FontMetricsInt
import android.text.style.ReplacementSpan
import androidx.annotation.IntRange

/**
 * @Name :SpaceSpan
 * @Time :2022/8/30 14:24
 * @Author :  Lzq
 * @Desc :
 */
internal class SpaceSpan(private val width: Int, color: Int) : ReplacementSpan() {
    private val paint = Paint()

    private constructor(width: Int) : this(width, Color.TRANSPARENT)

    init {
        paint.color = color
        paint.style = Paint.Style.FILL
    }

    override fun getSize(
        paint: Paint,
        text: CharSequence,
        @IntRange(from = 0) start: Int,
        @IntRange(from = 0) end: Int,
        fm: FontMetricsInt?
    ): Int {
        return width
    }

    override fun draw(
        canvas: Canvas,
        text: CharSequence,
        @IntRange(from = 0) start: Int,
        @IntRange(from = 0) end: Int,
        x: Float,
        top: Int,
        y: Int,
        bottom: Int,
        paint: Paint
    ) {
        canvas.drawRect(x, top.toFloat(), x + width, bottom.toFloat(), this.paint)
    }
}