package com.lzq.dawn.util.span;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.Layout;
import android.text.style.LeadingMarginSpan;

/**
 * @Name :CustomQuoteSpan
 * @Time :2022/8/30 14:26
 * @Author :  Lzq
 * @Desc : 行距
 */
 class CustomQuoteSpan implements LeadingMarginSpan {

    private final int color;
    private final int stripeWidth;
    private final int gapWidth;

    public CustomQuoteSpan(final int color, final int stripeWidth, final int gapWidth) {
        super();
        this.color = color;
        this.stripeWidth = stripeWidth;
        this.gapWidth = gapWidth;
    }

    @Override
    public int getLeadingMargin(final boolean first) {
        return stripeWidth + gapWidth;
    }

    @Override
    public void drawLeadingMargin(final Canvas c, final Paint p, final int x, final int dir,
                                  final int top, final int baseline, final int bottom,
                                  final CharSequence text, final int start, final int end,
                                  final boolean first, final Layout layout) {
        Paint.Style style = p.getStyle();
        int color = p.getColor();

        p.setStyle(Paint.Style.FILL);
        p.setColor(this.color);

        c.drawRect(x, top, x + dir * stripeWidth, bottom, p);

        p.setStyle(style);
        p.setColor(color);
    }
}
