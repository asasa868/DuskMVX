package com.lzq.dawn.util.span

import android.graphics.Canvas
import android.graphics.Paint
import android.text.Layout
import android.text.style.LeadingMarginSpan

/**
 * @Name :CustomQuoteSpan
 * @Time :2022/8/30 14:26
 * @Author :  Lzq
 * @Desc : 行距
 */
internal class CustomQuoteSpan(
    private val color: Int,
    private val stripeWidth: Int,
    private val gapWidth: Int
) : LeadingMarginSpan {
    override fun getLeadingMargin(first: Boolean): Int {
        return stripeWidth + gapWidth
    }

    override fun drawLeadingMargin(
        c: Canvas,
        p: Paint,
        x: Int,
        dir: Int,
        top: Int,
        baseline: Int,
        bottom: Int,
        text: CharSequence,
        start: Int,
        end: Int,
        first: Boolean,
        layout: Layout
    ) {
        val style = p.style
        val color = p.color
        p.style = Paint.Style.FILL
        p.color = this.color
        c.drawRect(x.toFloat(), top.toFloat(), (x + dir * stripeWidth).toFloat(), bottom.toFloat(), p)
        p.style = style
        p.color = color
    }
}