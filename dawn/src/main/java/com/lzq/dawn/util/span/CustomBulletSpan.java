package com.lzq.dawn.util.span;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.text.Layout;
import android.text.Spanned;
import android.text.style.LeadingMarginSpan;

/**
 * @Name :CustomBulletSpan
 * @Time :2022/8/30 14:27
 * @Author :  Lzq
 * @Desc : 行距
 */
 class CustomBulletSpan implements LeadingMarginSpan {

    private final int color;
    private final int radius;
    private final int gapWidth;

    private Path sBulletPath = null;

    CustomBulletSpan(final int color, final int radius, final int gapWidth) {
        this.color = color;
        this.radius = radius;
        this.gapWidth = gapWidth;
    }

    @Override
    public int getLeadingMargin(final boolean first) {
        return 2 * radius + gapWidth;
    }

    @Override
    public void drawLeadingMargin(final Canvas c, final Paint p, final int x, final int dir,
                                  final int top, final int baseline, final int bottom,
                                  final CharSequence text, final int start, final int end,
                                  final boolean first, final Layout l) {
        if (((Spanned) text).getSpanStart(this) == start) {
            Paint.Style style = p.getStyle();
            int oldColor = 0;
            oldColor = p.getColor();
            p.setColor(color);
            p.setStyle(Paint.Style.FILL);
            if (c.isHardwareAccelerated()) {
                if (sBulletPath == null) {
                    sBulletPath = new Path();
                    // Bullet is slightly better to avoid aliasing artifacts on mdpi devices.
                    sBulletPath.addCircle(0.0f, 0.0f, radius, Path.Direction.CW);
                }
                c.save();
                c.translate(x + dir * radius, (top + bottom) / 2.0f);
                c.drawPath(sBulletPath, p);
                c.restore();
            } else {
                c.drawCircle(x + dir * radius, (top + bottom) / 2.0f, radius, p);
            }
            p.setColor(oldColor);
            p.setStyle(style);
        }
    }
}