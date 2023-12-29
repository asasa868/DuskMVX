package com.lzq.dawn.util.span;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Color;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.AlignmentSpan;
import android.text.style.BackgroundColorSpan;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.LeadingMarginSpan;
import android.text.style.MaskFilterSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.ScaleXSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.text.style.SubscriptSpan;
import android.text.style.SuperscriptSpan;
import android.text.style.TypefaceSpan;
import android.text.style.URLSpan;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.FloatRange;
import androidx.annotation.IntDef;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;

import java.io.Serializable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @Name :SpanUtils
 * @Time :2022/8/30 14:13
 * @Author :  Lzq
 * @Desc : Span
 */
public final class SpanUtils {

    private static final int COLOR_DEFAULT = 0xFEFFFFFF;

    public static final int ALIGN_BOTTOM = 0;
    public static final int ALIGN_BASELINE = 1;
    public static final int ALIGN_CENTER = 2;
    public static final int ALIGN_TOP = 3;

    @IntDef({ALIGN_BOTTOM, ALIGN_BASELINE, ALIGN_CENTER, ALIGN_TOP})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Align {
    }

    private static final String LINE_SEPARATOR = System.getProperty("line.separator");

    public static SpanUtils with(final TextView textView) {
        return new SpanUtils(textView);
    }

    private TextView mTextView;
    private CharSequence mText;
    private int flag;
    private int foregroundColor;
    private int backgroundColor;
    private int lineHeight;
    private int alignLine;
    private int quoteColor;
    private int stripeWidth;
    private int quoteGapWidth;
    private int first;
    private int rest;
    private int bulletColor;
    private int bulletRadius;
    private int bulletGapWidth;
    private int fontSize;
    private float proportion;
    private float xProportion;
    private boolean isStrikethrough;
    private boolean isUnderline;
    private boolean isSuperscript;
    private boolean isSubscript;
    private boolean isBold;
    private boolean isItalic;
    private boolean isBoldItalic;
    private String fontFamily;
    private Typeface typeface;
    private Layout.Alignment alignment;
    private int verticalAlign;
    private ClickableSpan clickSpan;
    private String url;
    private float blurRadius;
    private BlurMaskFilter.Blur style;
    private Shader shader;
    private float shadowRadius;
    private float shadowDx;
    private float shadowDy;
    private int shadowColor;
    private Object[] spans;

    private Bitmap imageBitmap;
    private Drawable imageDrawable;
    private Uri imageUri;
    private int imageResourceId;
    private int alignImage;

    private int spaceSize;
    private int spaceColor;

    private SerializableSpannableStringBuilder mBuilder;
    private boolean isCreated;

    private int mType;
    private final int mTypeCharSequence = 0;
    private final int mTypeImage = 1;
    private final int mTypeSpace = 2;

    private SpanUtils(TextView textView) {
        this();
        mTextView = textView;
    }

    public SpanUtils() {
        mBuilder = new SerializableSpannableStringBuilder();
        mText = "";
        mType = -1;
        setDefault();
    }

    private void setDefault() {
        flag = Spanned.SPAN_EXCLUSIVE_EXCLUSIVE;
        foregroundColor = COLOR_DEFAULT;
        backgroundColor = COLOR_DEFAULT;
        lineHeight = -1;
        quoteColor = COLOR_DEFAULT;
        first = -1;
        bulletColor = COLOR_DEFAULT;
        fontSize = -1;
        proportion = -1;
        xProportion = -1;
        isStrikethrough = false;
        isUnderline = false;
        isSuperscript = false;
        isSubscript = false;
        isBold = false;
        isItalic = false;
        isBoldItalic = false;
        fontFamily = null;
        typeface = null;
        alignment = null;
        verticalAlign = -1;
        clickSpan = null;
        url = null;
        blurRadius = -1;
        shader = null;
        shadowRadius = -1;
        spans = null;

        imageBitmap = null;
        imageDrawable = null;
        imageUri = null;
        imageResourceId = -1;

        spaceSize = -1;
    }

    /**
     * 设置标志
     *
     * @param flag flag.
     *             <ul>
     *             <li>{@link Spanned#SPAN_INCLUSIVE_EXCLUSIVE}</li>
     *             <li>{@link Spanned#SPAN_INCLUSIVE_INCLUSIVE}</li>
     *             <li>{@link Spanned#SPAN_EXCLUSIVE_EXCLUSIVE}</li>
     *             <li>{@link Spanned#SPAN_EXCLUSIVE_INCLUSIVE}</li>
     *             </ul>
     * @return the single {@link SpanUtils} instance
     */
    public SpanUtils setFlag(final int flag) {
        this.flag = flag;
        return this;
    }

    /**
     * 设置前景颜色
     *
     * @param color 前景颜色
     * @return the single {@link SpanUtils} instance
     */
    public SpanUtils setForegroundColor(@ColorInt final int color) {
        this.foregroundColor = color;
        return this;
    }

    /**
     * 设置背景颜色
     *
     * @param color 背景颜色
     * @return the single {@link SpanUtils} instance
     */
    public SpanUtils setBackgroundColor(@ColorInt final int color) {
        this.backgroundColor = color;
        return this;
    }

    /**
     * 设置线条高度
     *
     * @param lineHeight 线条高度，以像素为单位。
     * @return the single {@link SpanUtils} instance
     */
    public SpanUtils setLineHeight(@IntRange(from = 0) final int lineHeight) {
        return setLineHeight(lineHeight, ALIGN_CENTER);
    }

    /**
     * 设置线条高度
     *
     * @param lineHeight 线条高度，以像素为单位。
     * @param align      对准。
     *                   <ul>
     *                   <li>{@link Align#ALIGN_TOP   }</li>
     *                   <li>{@link Align#ALIGN_CENTER}</li>
     *                   <li>{@link Align#ALIGN_BOTTOM}</li>
     *                   </ul>
     * @return the single {@link SpanUtils} instance
     */
    public SpanUtils setLineHeight(@IntRange(from = 0) final int lineHeight,
                                   @Align final int align) {
        this.lineHeight = lineHeight;
        this.alignLine = align;
        return this;
    }

    /**
     * 设置 颜色范围
     *
     * @param color 引用的颜色
     * @return the single {@link SpanUtils} instance
     */
    public SpanUtils setQuoteColor(@ColorInt final int color) {
        return setQuoteColor(color, 2, 2);
    }

    /**
     * 设置 颜色范围
     *
     * @param color       引用的颜色
     * @param stripeWidth 条纹的宽度，以像素为单位。
     * @param gapWidth    间隙宽度，以像素为单位。
     * @return the single {@link SpanUtils} instance
     */
    public SpanUtils setQuoteColor(@ColorInt final int color,
                                   @IntRange(from = 1) final int stripeWidth,
                                   @IntRange(from = 0) final int gapWidth) {
        this.quoteColor = color;
        this.stripeWidth = stripeWidth;
        this.quoteGapWidth = gapWidth;
        return this;
    }

    /**
     * 设置前边距。
     *
     * @param first 段落第一行的缩进。
     * @param rest  段落剩余行的缩进。
     * @return the single {@link SpanUtils} instance
     */
    public SpanUtils setLeadingMargin(@IntRange(from = 0) final int first,
                                      @IntRange(from = 0) final int rest) {
        this.first = first;
        this.rest = rest;
        return this;
    }

    /**
     * Set the span of bullet.
     *
     * @param gapWidth 间隙宽度，以像素为单位。
     * @return the single {@link SpanUtils} instance
     */
    public SpanUtils setBullet(@IntRange(from = 0) final int gapWidth) {
        return setBullet(0, 3, gapWidth);
    }

    /**
     * Set the span of bullet.
     *
     * @param color    color
     * @param radius   圆角，以像素为单位。
     * @param gapWidth 间隙宽度，以像素为单位。
     * @return the single {@link SpanUtils} instance
     */
    public SpanUtils setBullet(@ColorInt final int color,
                               @IntRange(from = 0) final int radius,
                               @IntRange(from = 0) final int gapWidth) {
        this.bulletColor = color;
        this.bulletRadius = radius;
        this.bulletGapWidth = gapWidth;
        return this;
    }

    /**
     * 设置字体大小
     *
     * @param size 字体的大小
     * @return the single {@link SpanUtils} instance
     */
    public SpanUtils setFontSize(@IntRange(from = 0) final int size) {
        return setFontSize(size, false);
    }

    /**
     * 设置字体大小
     *
     * @param size 字体的大小
     * @param isSp True使用sp，false使用像素。
     * @return the single {@link SpanUtils} instance
     */
    public SpanUtils setFontSize(@IntRange(from = 0) final int size, final boolean isSp) {
        if (isSp) {
            final float fontScale = Resources.getSystem().getDisplayMetrics().scaledDensity;
            this.fontSize = (int) (size * fontScale + 0.5f);
        } else {
            this.fontSize = size;
        }
        return this;
    }

    /**
     * 设置字体比例
     *
     * @param proportion 字体的比例
     * @return the single {@link SpanUtils} instance
     */
    public SpanUtils setFontProportion(final float proportion) {
        this.proportion = proportion;
        return this;
    }

    /**
     * 设置字体横向比例
     *
     * @param proportion 字体的横向比例
     * @return the single {@link SpanUtils} instance
     */
    public SpanUtils setFontXProportion(final float proportion) {
        this.xProportion = proportion;
        return this;
    }

    /**
     * 设置删除线
     *
     * @return the single {@link SpanUtils} instance
     */
    public SpanUtils setStrikethrough() {
        this.isStrikethrough = true;
        return this;
    }

    /**
     * 设置下划线
     *
     * @return the single {@link SpanUtils} instance
     */
    public SpanUtils setUnderline() {
        this.isUnderline = true;
        return this;
    }

    /**
     * 设置上标
     *
     * @return the single {@link SpanUtils} instance
     */
    public SpanUtils setSuperscript() {
        this.isSuperscript = true;
        return this;
    }

    /**
     * 设置下标
     *
     * @return the single {@link SpanUtils} instance
     */
    public SpanUtils setSubscript() {
        this.isSubscript = true;
        return this;
    }

    /**
     * 设置粗体
     *
     * @return the single {@link SpanUtils} instance
     */
    public SpanUtils setBold() {
        isBold = true;
        return this;
    }

    /**
     * 设置斜体
     *
     * @return the single {@link SpanUtils} instance
     */
    public SpanUtils setItalic() {
        isItalic = true;
        return this;
    }

    /**
     * 设置粗体到斜体
     *
     * @return the single {@link SpanUtils} instance
     */
    public SpanUtils setBoldItalic() {
        isBoldItalic = true;
        return this;
    }

    /**
     * 设置字体系列
     * .appendLine("monospace 字体")
     * .setFontFamily("monospace")
     *
     * @param fontFamily 字体
     *                   <ul>
     *                   <li>monospace</li>
     *                   <li>serif</li>
     *                   <li>sans-serif</li>
     *                   </ul>
     * @return the single {@link SpanUtils} instance
     */
    public SpanUtils setFontFamily(@NonNull final String fontFamily) {
        this.fontFamily = fontFamily;
        return this;
    }

    /**
     * 设置字体
     *
     * @param typeface 字体
     * @return the single {@link SpanUtils} instance
     */
    public SpanUtils setTypeface(@NonNull final Typeface typeface) {
        this.typeface = typeface;
        return this;
    }

    /**
     * 设置水平对齐
     *
     * @param alignment The alignment.
     *                  <ul>
     *                  <li>{@link Layout.Alignment#ALIGN_NORMAL  }</li>
     *                  <li>{@link Layout.Alignment#ALIGN_OPPOSITE}</li>
     *                  <li>{@link Layout.Alignment#ALIGN_CENTER  }</li>
     *                  </ul>
     * @return the single {@link SpanUtils} instance
     */
    public SpanUtils setHorizontalAlign(@NonNull final Layout.Alignment alignment) {
        this.alignment = alignment;
        return this;
    }

    /**
     * 设置垂直对齐
     *
     * @param align The alignment.
     *              <ul>
     *              <li>{@link Align#ALIGN_TOP     }</li>
     *              <li>{@link Align#ALIGN_CENTER  }</li>
     *              <li>{@link Align#ALIGN_BASELINE}</li>
     *              <li>{@link Align#ALIGN_BOTTOM  }</li>
     *              </ul>
     * @return the single {@link SpanUtils} instance
     */
    public SpanUtils setVerticalAlign(@Align final int align) {
        this.verticalAlign = align;
        return this;
    }

    /**
     * 设置单击
     * <p>必须持有 {@code view.setMovementMethod(LinkMovementMethod.getInstance())}</p>
     *
     * @param clickSpan 单击
     * @return the single {@link SpanUtils} instance
     */
    public SpanUtils setClickSpan(@NonNull final ClickableSpan clickSpan) {
        setMovementMethodIfNeed();
        this.clickSpan = clickSpan;
        return this;
    }

    /**
     * 设置单击
     * <p>必须持有 {@code view.setMovementMethod(LinkMovementMethod.getInstance())}</p>
     *
     * @param color         单击范围的颜色
     * @param underlineText True支持下划线，否则为false。
     * @param listener      单击范围的侦听器。
     * @return the single {@link SpanUtils} instance
     */
    public SpanUtils setClickSpan(@ColorInt final int color,
                                  final boolean underlineText,
                                  final View.OnClickListener listener) {
        setMovementMethodIfNeed();
        this.clickSpan = new ClickableSpan() {

            @Override
            public void updateDrawState(@NonNull TextPaint paint) {
                paint.setColor(color);
                paint.setUnderlineText(underlineText);
            }

            @Override
            public void onClick(@NonNull View widget) {
                if (listener != null) {
                    listener.onClick(widget);
                }
            }
        };
        return this;
    }

    /**
     * 设置url
     * <p>Must set {@code view.setMovementMethod(LinkMovementMethod.getInstance())}</p>
     *
     * @param url url.
     * @return the single {@link SpanUtils} instance
     */
    public SpanUtils setUrl(@NonNull final String url) {
        setMovementMethodIfNeed();
        this.url = url;
        return this;
    }

    private void setMovementMethodIfNeed() {
        if (mTextView != null && mTextView.getMovementMethod() == null) {
            mTextView.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }

    /**
     * 设置模糊
     *
     * @param radius 模糊半径
     * @param style  风格
     *               <ul>
     *               <li>{@link BlurMaskFilter.Blur#NORMAL}</li>
     *               <li>{@link BlurMaskFilter.Blur#SOLID}</li>
     *               <li>{@link BlurMaskFilter.Blur#OUTER}</li>
     *               <li>{@link BlurMaskFilter.Blur#INNER}</li>
     *               </ul>
     * @return the single {@link SpanUtils} instance
     */
    public SpanUtils setBlur(@FloatRange(from = 0, fromInclusive = false) final float radius,
                             final BlurMaskFilter.Blur style) {
        this.blurRadius = radius;
        this.style = style;
        return this;
    }

    /**
     * 设置着色器
     * <p>
     * appendLine("图片着色")
     * .setFontSize(64, true)
     * .setShader(BitmapShader(BitmapFactory.decodeResource(resources, R.drawable.span_cheetah),
     * Shader.TileMode.REPEAT,
     * Shader.TileMode.REPEAT))
     *
     * @param shader shader.
     * @return the single {@link SpanUtils} instance
     */
    public SpanUtils setShader(@NonNull final Shader shader) {
        this.shader = shader;
        return this;
    }

    /**
     * 设置阴影
     *
     * @param radius      阴影的半径。
     * @param dx          X轴偏移，以像素为单位。
     * @param dy          Y轴偏移，以像素为单位。
     * @param shadowColor 阴影的颜色
     * @return the single {@link SpanUtils} instance
     */
    public SpanUtils setShadow(@FloatRange(from = 0, fromInclusive = false) final float radius,
                               final float dx,
                               final float dy,
                               final int shadowColor) {
        this.shadowRadius = radius;
        this.shadowDx = dx;
        this.shadowDy = dy;
        this.shadowColor = shadowColor;
        return this;
    }


    /**
     * Set the spans.
     *
     * @param spans spans.
     * @return the single {@link SpanUtils} instance
     */
    public SpanUtils setSpans(@NonNull final Object... spans) {
        if (spans.length > 0) {
            this.spans = spans;
        }
        return this;
    }

    /**
     * 将文本附加到文本。
     *
     * @param text text.
     * @return the single {@link SpanUtils} instance
     */
    public SpanUtils append(@NonNull final CharSequence text) {
        apply(mTypeCharSequence);
        mText = text;
        return this;
    }

    /**
     * 追加一行。
     *
     * @return the single {@link SpanUtils} instance
     */
    public SpanUtils appendLine() {
        apply(mTypeCharSequence);
        mText = LINE_SEPARATOR;
        return this;
    }

    /**
     * 附加文本和一行
     *
     * @return the single {@link SpanUtils} instance
     */
    public SpanUtils appendLine(@NonNull final CharSequence text) {
        apply(mTypeCharSequence);
        mText = text + LINE_SEPARATOR;
        return this;
    }

    /**
     * 附加一个图像。
     *
     * @param bitmap bitmap
     * @return the single {@link SpanUtils} instance
     */
    public SpanUtils appendImage(@NonNull final Bitmap bitmap) {
        return appendImage(bitmap, ALIGN_BOTTOM);
    }

    /**
     * 附加一个图像
     *
     * @param bitmap bitmap.
     * @param align  对准
     *               <ul>
     *               <li>{@link Align#ALIGN_TOP     }</li>
     *               <li>{@link Align#ALIGN_CENTER  }</li>
     *               <li>{@link Align#ALIGN_BASELINE}</li>
     *               <li>{@link Align#ALIGN_BOTTOM  }</li>
     *               </ul>
     * @return the single {@link SpanUtils} instance
     */
    public SpanUtils appendImage(@NonNull final Bitmap bitmap, @Align final int align) {
        apply(mTypeImage);
        this.imageBitmap = bitmap;
        this.alignImage = align;
        return this;
    }

    /**
     * 附加一个图像。
     *
     * @param drawable drawable
     * @return the single {@link SpanUtils} instance
     */
    public SpanUtils appendImage(@NonNull final Drawable drawable) {
        return appendImage(drawable, ALIGN_BOTTOM);
    }

    /**
     * 附加一个图像
     *
     * @param drawable drawable
     * @param align    对准
     *                 <ul>
     *                 <li>{@link Align#ALIGN_TOP     }</li>
     *                 <li>{@link Align#ALIGN_CENTER  }</li>
     *                 <li>{@link Align#ALIGN_BASELINE}</li>
     *                 <li>{@link Align#ALIGN_BOTTOM  }</li>
     *                 </ul>
     * @return the single {@link SpanUtils} instance
     */
    public SpanUtils appendImage(@NonNull final Drawable drawable, @Align final int align) {
        apply(mTypeImage);
        this.imageDrawable = drawable;
        this.alignImage = align;
        return this;
    }

    /**
     * 附加一个图像
     *
     * @param uri 图像的uri。
     * @return the single {@link SpanUtils} instance
     */
    public SpanUtils appendImage(@NonNull final Uri uri) {
        return appendImage(uri, ALIGN_BOTTOM);
    }

    /**
     * 附加一个图像
     *
     * @param uri   图像的uri
     * @param align 对准
     *              <ul>
     *              <li>{@link Align#ALIGN_TOP     }</li>
     *              <li>{@link Align#ALIGN_CENTER  }</li>
     *              <li>{@link Align#ALIGN_BASELINE}</li>
     *              <li>{@link Align#ALIGN_BOTTOM  }</li>
     *              </ul>
     * @return the single {@link SpanUtils} instance
     */
    public SpanUtils appendImage(@NonNull final Uri uri, @Align final int align) {
        apply(mTypeImage);
        this.imageUri = uri;
        this.alignImage = align;
        return this;
    }

    /**
     * 附加一个图像
     *
     * @param resourceId resource id
     * @return the single {@link SpanUtils} instance
     */
    public SpanUtils appendImage(@DrawableRes final int resourceId) {
        return appendImage(resourceId, ALIGN_BOTTOM);
    }

    /**
     * 附加一个图像
     *
     * @param resourceId resource id
     * @param align      对准
     *                   <ul>
     *                   <li>{@link Align#ALIGN_TOP     }</li>
     *                   <li>{@link Align#ALIGN_CENTER  }</li>
     *                   <li>{@link Align#ALIGN_BASELINE}</li>
     *                   <li>{@link Align#ALIGN_BOTTOM  }</li>
     *                   </ul>
     * @return the single {@link SpanUtils} instance
     */
    public SpanUtils appendImage(@DrawableRes final int resourceId, @Align final int align) {
        apply(mTypeImage);
        this.imageResourceId = resourceId;
        this.alignImage = align;
        return this;
    }

    /**
     * 追加空格
     *
     * @param size 空格大小
     * @return the single {@link SpanUtils} instance
     */
    public SpanUtils appendSpace(@IntRange(from = 0) final int size) {
        return appendSpace(size, Color.TRANSPARENT);
    }

    /**
     * 追加空格
     *
     * @param size 空格大小
     * @param color 空格的颜色
     * @return the single {@link SpanUtils} instance
     */
    public SpanUtils appendSpace(@IntRange(from = 0) final int size, @ColorInt final int color) {
        apply(mTypeSpace);
        spaceSize = size;
        spaceColor = color;
        return this;
    }

    private void apply(final int type) {
        applyLast();
        mType = type;
    }

    public SpannableStringBuilder get() {
        return mBuilder;
    }

    /**
     * 创建
     *
     * @return the span string
     */
    public SpannableStringBuilder create() {
        applyLast();
        if (mTextView != null) {
            mTextView.setText(mBuilder);
        }
        isCreated = true;
        return mBuilder;
    }

    private void applyLast() {
        if (isCreated) {
            return;
        }
        if (mType == mTypeCharSequence) {
            updateCharCharSequence();
        } else if (mType == mTypeImage) {
            updateImage();
        } else if (mType == mTypeSpace) {
            updateSpace();
        }
        setDefault();
    }

    private void updateCharCharSequence() {
        if (mText.length() == 0) {
            return;
        }
        int start = mBuilder.length();
        if (start == 0 && lineHeight != -1) {// bug of LineHeightSpan when first line
            mBuilder.append(Character.toString((char) 2))
                    .append("\n")
                    .setSpan(new AbsoluteSizeSpan(0), 0, 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            start = 2;
        }
        mBuilder.append(mText);
        int end = mBuilder.length();
        if (verticalAlign != -1) {
            mBuilder.setSpan(new VerticalAlignSpan(verticalAlign), start, end, flag);
        }
        if (foregroundColor != COLOR_DEFAULT) {
            mBuilder.setSpan(new ForegroundColorSpan(foregroundColor), start, end, flag);
        }
        if (backgroundColor != COLOR_DEFAULT) {
            mBuilder.setSpan(new BackgroundColorSpan(backgroundColor), start, end, flag);
        }
        if (first != -1) {
            mBuilder.setSpan(new LeadingMarginSpan.Standard(first, rest), start, end, flag);
        }
        if (quoteColor != COLOR_DEFAULT) {
            mBuilder.setSpan(
                    new CustomQuoteSpan(quoteColor, stripeWidth, quoteGapWidth),
                    start,
                    end,
                    flag
            );
        }
        if (bulletColor != COLOR_DEFAULT) {
            mBuilder.setSpan(
                    new CustomBulletSpan(bulletColor, bulletRadius, bulletGapWidth),
                    start,
                    end,
                    flag
            );
        }
        if (fontSize != -1) {
            mBuilder.setSpan(new AbsoluteSizeSpan(fontSize, false), start, end, flag);
        }
        if (proportion != -1) {
            mBuilder.setSpan(new RelativeSizeSpan(proportion), start, end, flag);
        }
        if (xProportion != -1) {
            mBuilder.setSpan(new ScaleXSpan(xProportion), start, end, flag);
        }
        if (lineHeight != -1) {
            mBuilder.setSpan(new CustomLineHeightSpan(lineHeight, alignLine), start, end, flag);
        }
        if (isStrikethrough) {
            mBuilder.setSpan(new StrikethroughSpan(), start, end, flag);
        }
        if (isUnderline) {
            mBuilder.setSpan(new UnderlineSpan(), start, end, flag);
        }
        if (isSuperscript) {
            mBuilder.setSpan(new SuperscriptSpan(), start, end, flag);
        }
        if (isSubscript) {
            mBuilder.setSpan(new SubscriptSpan(), start, end, flag);
        }
        if (isBold) {
            mBuilder.setSpan(new StyleSpan(Typeface.BOLD), start, end, flag);
        }
        if (isItalic) {
            mBuilder.setSpan(new StyleSpan(Typeface.ITALIC), start, end, flag);
        }
        if (isBoldItalic) {
            mBuilder.setSpan(new StyleSpan(Typeface.BOLD_ITALIC), start, end, flag);
        }
        if (fontFamily != null) {
            mBuilder.setSpan(new TypefaceSpan(fontFamily), start, end, flag);
        }
        if (typeface != null) {
            mBuilder.setSpan(new CustomTypefaceSpan(typeface), start, end, flag);
        }
        if (alignment != null) {
            mBuilder.setSpan(new AlignmentSpan.Standard(alignment), start, end, flag);
        }
        if (clickSpan != null) {
            mBuilder.setSpan(clickSpan, start, end, flag);
        }
        if (url != null) {
            mBuilder.setSpan(new URLSpan(url), start, end, flag);
        }
        if (blurRadius != -1) {
            mBuilder.setSpan(
                    new MaskFilterSpan(new BlurMaskFilter(blurRadius, style)),
                    start,
                    end,
                    flag
            );
        }
        if (shader != null) {
            mBuilder.setSpan(new ShaderSpan(shader), start, end, flag);
        }
        if (shadowRadius != -1) {
            mBuilder.setSpan(
                    new ShadowSpan(shadowRadius, shadowDx, shadowDy, shadowColor),
                    start,
                    end,
                    flag
            );
        }
        if (spans != null) {
            for (Object span : spans) {
                mBuilder.setSpan(span, start, end, flag);
            }
        }
    }

    private void updateImage() {
        int start = mBuilder.length();
        mText = "<img>";
        updateCharCharSequence();
        int end = mBuilder.length();
        if (imageBitmap != null) {
            mBuilder.setSpan(new CustomImageSpan(imageBitmap, alignImage), start, end, flag);
        } else if (imageDrawable != null) {
            mBuilder.setSpan(new CustomImageSpan(imageDrawable, alignImage), start, end, flag);
        } else if (imageUri != null) {
            mBuilder.setSpan(new CustomImageSpan(imageUri, alignImage), start, end, flag);
        } else if (imageResourceId != -1) {
            mBuilder.setSpan(new CustomImageSpan(imageResourceId, alignImage), start, end, flag);
        }
    }

    private void updateSpace() {
        int start = mBuilder.length();
        mText = "< >";
        updateCharCharSequence();
        int end = mBuilder.length();
        mBuilder.setSpan(new SpaceSpan(spaceSize, spaceColor), start, end, flag);
    }

    private static class SerializableSpannableStringBuilder extends SpannableStringBuilder
            implements Serializable {

        private static final long serialVersionUID = 4909567650765875771L;
    }
}
