package com.lzq.dawn.util.span

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.text.Layout
import android.text.Spanned
import android.text.style.LeadingMarginSpan

/**
 * @Name :CustomBulletSpan
 * @Time :2022/8/30 14:27
 * @Author :  Lzq
 * @Desc : 行距
 */
internal class CustomBulletSpan(private val color: Int, private val radius: Int, private val gapWidth: Int) :
    LeadingMarginSpan {
    private var sBulletPath: Path? = null
    override fun getLeadingMargin(first: Boolean): Int {
        return 2 * radius + gapWidth
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
        l: Layout
    ) {
        if ((text as Spanned).getSpanStart(this) == start) {
            val style = p.style
            var oldColor = 0
            oldColor = p.color
            p.color = color
            p.style = Paint.Style.FILL
            if (c.isHardwareAccelerated) {
                if (sBulletPath == null) {
                    sBulletPath = Path()
                    // Bullet is slightly better to avoid aliasing artifacts on mdpi devices.
                    sBulletPath!!.addCircle(0.0f, 0.0f, radius.toFloat(), Path.Direction.CW)
                }
                c.save()
                c.translate((x + dir * radius).toFloat(), (top + bottom) / 2.0f)
                c.drawPath(sBulletPath!!, p)
                c.restore()
            } else {
                c.drawCircle((x + dir * radius).toFloat(), (top + bottom) / 2.0f, radius.toFloat(), p)
            }
            p.color = oldColor
            p.style = style
        }
    }
}