package com.lzq.dawn.util.span

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BlurMaskFilter
import android.graphics.BlurMaskFilter.Blur
import android.graphics.Color
import android.graphics.Shader
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.net.Uri
import android.text.Layout
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.AbsoluteSizeSpan
import android.text.style.AlignmentSpan
import android.text.style.BackgroundColorSpan
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.LeadingMarginSpan
import android.text.style.MaskFilterSpan
import android.text.style.RelativeSizeSpan
import android.text.style.ScaleXSpan
import android.text.style.StrikethroughSpan
import android.text.style.StyleSpan
import android.text.style.SubscriptSpan
import android.text.style.SuperscriptSpan
import android.text.style.TypefaceSpan
import android.text.style.URLSpan
import android.text.style.UnderlineSpan
import android.view.View
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.FloatRange
import androidx.annotation.IntDef
import androidx.annotation.IntRange
import java.io.Serializable

/**
 * @Name :SpanUtils
 * @Time :2022/8/30 14:13
 * @Author :  Lzq
 * @Desc : Span
 */
class SpanUtils() {
    @IntDef(ALIGN_BOTTOM, ALIGN_BASELINE, ALIGN_CENTER, ALIGN_TOP)
    @Retention(AnnotationRetention.SOURCE)
    annotation class Align

    private var mTextView: TextView? = null
    private var mText: CharSequence
    private var flag = 0
    private var foregroundColor = 0
    private var backgroundColor = 0
    private var lineHeight = 0
    private var alignLine = 0
    private var quoteColor = 0
    private var stripeWidth = 0
    private var quoteGapWidth = 0
    private var first = 0
    private var rest = 0
    private var bulletColor = 0
    private var bulletRadius = 0
    private var bulletGapWidth = 0
    private var fontSize = 0
    private var proportion = 0f
    private var xProportion = 0f
    private var isStrikethrough = false
    private var isUnderline = false
    private var isSuperscript = false
    private var isSubscript = false
    private var isBold = false
    private var isItalic = false
    private var isBoldItalic = false
    private var fontFamily: String? = null
    private var typeface: Typeface? = null
    private var alignment: Layout.Alignment? = null
    private var verticalAlign = 0
    private var clickSpan: ClickableSpan? = null
    private var url: String? = null
    private var blurRadius = 0f
    private var style: Blur? = null
    private var shader: Shader? = null
    private var shadowRadius = 0f
    private var shadowDx = 0f
    private var shadowDy = 0f
    private var shadowColor = 0
    private var spans: Array<Any>? = null
    private var imageBitmap: Bitmap? = null
    private var imageDrawable: Drawable? = null
    private var imageUri: Uri? = null
    private var imageResourceId = 0
    private var alignImage = 0
    private var spaceSize = 0
    private var spaceColor = 0
    private val mBuilder: SerializableSpannableStringBuilder
    private var isCreated = false
    private var mType: Int
    private val mTypeCharSequence = 0
    private val mTypeImage = 1
    private val mTypeSpace = 2

    private constructor(textView: TextView) : this() {
        mTextView = textView
    }

    init {
        mBuilder = SerializableSpannableStringBuilder()
        mText = ""
        mType = -1
        setDefault()
    }

    private fun setDefault() {
        flag = Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        foregroundColor = COLOR_DEFAULT
        backgroundColor = COLOR_DEFAULT
        lineHeight = -1
        quoteColor = COLOR_DEFAULT
        first = -1
        bulletColor = COLOR_DEFAULT
        fontSize = -1
        proportion = -1f
        xProportion = -1f
        isStrikethrough = false
        isUnderline = false
        isSuperscript = false
        isSubscript = false
        isBold = false
        isItalic = false
        isBoldItalic = false
        fontFamily = null
        typeface = null
        alignment = null
        verticalAlign = -1
        clickSpan = null
        url = null
        blurRadius = -1f
        shader = null
        shadowRadius = -1f
        spans = null
        imageBitmap = null
        imageDrawable = null
        imageUri = null
        imageResourceId = -1
        spaceSize = -1
    }

    /**
     * 设置标志
     *
     * @param flag flag.
     *
     *  * [Spanned.SPAN_INCLUSIVE_EXCLUSIVE]
     *  * [Spanned.SPAN_INCLUSIVE_INCLUSIVE]
     *  * [Spanned.SPAN_EXCLUSIVE_EXCLUSIVE]
     *  * [Spanned.SPAN_EXCLUSIVE_INCLUSIVE]
     *
     * @return the single [SpanUtils] instance
     */
    fun setFlag(flag: Int): SpanUtils {
        this.flag = flag
        return this
    }

    /**
     * 设置前景颜色
     *
     * @param color 前景颜色
     * @return the single [SpanUtils] instance
     */
    fun setForegroundColor(@ColorInt color: Int): SpanUtils {
        foregroundColor = color
        return this
    }

    /**
     * 设置背景颜色
     *
     * @param color 背景颜色
     * @return the single [SpanUtils] instance
     */
    fun setBackgroundColor(@ColorInt color: Int): SpanUtils {
        backgroundColor = color
        return this
    }

    /**
     * 设置线条高度
     *
     * @param lineHeight 线条高度，以像素为单位。
     * @return the single [SpanUtils] instance
     */
    fun setLineHeight(@IntRange(from = 0) lineHeight: Int): SpanUtils {
        return setLineHeight(lineHeight, ALIGN_CENTER)
    }

    /**
     * 设置线条高度
     *
     * @param lineHeight 线条高度，以像素为单位。
     * @param align      对准。
     *
     *  * [SpanUtils.ALIGN_TOP]
     *  * [SpanUtils.ALIGN_CENTER]
     *  * [SpanUtils.ALIGN_BOTTOM]
     *
     * @return the single [SpanUtils] instance
     */
    fun setLineHeight(
        @IntRange(from = 0) lineHeight: Int, @Align align: Int
    ): SpanUtils {
        this.lineHeight = lineHeight
        alignLine = align
        return this
    }

    /**
     * 设置 颜色范围
     *
     * @param color 引用的颜色
     * @return the single [SpanUtils] instance
     */
    fun setQuoteColor(@ColorInt color: Int): SpanUtils {
        return setQuoteColor(color, 2, 2)
    }

    /**
     * 设置 颜色范围
     *
     * @param color       引用的颜色
     * @param stripeWidth 条纹的宽度，以像素为单位。
     * @param gapWidth    间隙宽度，以像素为单位。
     * @return the single [SpanUtils] instance
     */
    fun setQuoteColor(
        @ColorInt color: Int, @IntRange(from = 1) stripeWidth: Int, @IntRange(from = 0) gapWidth: Int
    ): SpanUtils {
        quoteColor = color
        this.stripeWidth = stripeWidth
        quoteGapWidth = gapWidth
        return this
    }

    /**
     * 设置前边距。
     *
     * @param first 段落第一行的缩进。
     * @param rest  段落剩余行的缩进。
     * @return the single [SpanUtils] instance
     */
    fun setLeadingMargin(
        @IntRange(from = 0) first: Int, @IntRange(from = 0) rest: Int
    ): SpanUtils {
        this.first = first
        this.rest = rest
        return this
    }

    /**
     * Set the span of bullet.
     *
     * @param gapWidth 间隙宽度，以像素为单位。
     * @return the single [SpanUtils] instance
     */
    fun setBullet(@IntRange(from = 0) gapWidth: Int): SpanUtils {
        return setBullet(0, 3, gapWidth)
    }

    /**
     * Set the span of bullet.
     *
     * @param color    color
     * @param radius   圆角，以像素为单位。
     * @param gapWidth 间隙宽度，以像素为单位。
     * @return the single [SpanUtils] instance
     */
    fun setBullet(
        @ColorInt color: Int, @IntRange(from = 0) radius: Int, @IntRange(from = 0) gapWidth: Int
    ): SpanUtils {
        bulletColor = color
        bulletRadius = radius
        bulletGapWidth = gapWidth
        return this
    }

    /**
     * 设置字体大小
     *
     * @param size 字体的大小
     * @return the single [SpanUtils] instance
     */
    fun setFontSize(@IntRange(from = 0) size: Int): SpanUtils {
        return setFontSize(size, false)
    }

    /**
     * 设置字体大小
     *
     * @param size 字体的大小
     * @param isSp True使用sp，false使用像素。
     * @return the single [SpanUtils] instance
     */
    fun setFontSize(@IntRange(from = 0) size: Int, isSp: Boolean): SpanUtils {
        fontSize = if (isSp) {
            val fontScale = Resources.getSystem().displayMetrics.scaledDensity
            (size * fontScale + 0.5f).toInt()
        } else {
            size
        }
        return this
    }

    /**
     * 设置字体比例
     *
     * @param proportion 字体的比例
     * @return the single [SpanUtils] instance
     */
    fun setFontProportion(proportion: Float): SpanUtils {
        this.proportion = proportion
        return this
    }

    /**
     * 设置字体横向比例
     *
     * @param proportion 字体的横向比例
     * @return the single [SpanUtils] instance
     */
    fun setFontXProportion(proportion: Float): SpanUtils {
        xProportion = proportion
        return this
    }

    /**
     * 设置删除线
     *
     * @return the single [SpanUtils] instance
     */
    fun setStrikethrough(): SpanUtils {
        isStrikethrough = true
        return this
    }

    /**
     * 设置下划线
     *
     * @return the single [SpanUtils] instance
     */
    fun setUnderline(): SpanUtils {
        isUnderline = true
        return this
    }

    /**
     * 设置上标
     *
     * @return the single [SpanUtils] instance
     */
    fun setSuperscript(): SpanUtils {
        isSuperscript = true
        return this
    }

    /**
     * 设置下标
     *
     * @return the single [SpanUtils] instance
     */
    fun setSubscript(): SpanUtils {
        isSubscript = true
        return this
    }

    /**
     * 设置粗体
     *
     * @return the single [SpanUtils] instance
     */
    fun setBold(): SpanUtils {
        isBold = true
        return this
    }

    /**
     * 设置斜体
     *
     * @return the single [SpanUtils] instance
     */
    fun setItalic(): SpanUtils {
        isItalic = true
        return this
    }

    /**
     * 设置粗体到斜体
     *
     * @return the single [SpanUtils] instance
     */
    fun setBoldItalic(): SpanUtils {
        isBoldItalic = true
        return this
    }

    /**
     * 设置字体系列
     * .appendLine("monospace 字体")
     * .setFontFamily("monospace")
     *
     * @param fontFamily 字体
     *
     *  * monospace
     *  * serif
     *  * sans-serif
     *
     * @return the single [SpanUtils] instance
     */
    fun setFontFamily(fontFamily: String): SpanUtils {
        this.fontFamily = fontFamily
        return this
    }

    /**
     * 设置字体
     *
     * @param typeface 字体
     * @return the single [SpanUtils] instance
     */
    fun setTypeface(typeface: Typeface): SpanUtils {
        this.typeface = typeface
        return this
    }

    /**
     * 设置水平对齐
     *
     * @param alignment The alignment.
     *
     *  * [Layout.Alignment.ALIGN_NORMAL]
     *  * [Layout.Alignment.ALIGN_OPPOSITE]
     *  * [Layout.Alignment.ALIGN_CENTER]
     *
     * @return the single [SpanUtils] instance
     */
    fun setHorizontalAlign(alignment: Layout.Alignment): SpanUtils {
        this.alignment = alignment
        return this
    }

    /**
     * 设置垂直对齐
     *
     * @param align The alignment.
     *
     *  * [SpanUtils.ALIGN_TOP]
     *  * [SpanUtils.ALIGN_CENTER]
     *  * [SpanUtils.ALIGN_BASELINE]
     *  * [SpanUtils.ALIGN_BOTTOM]
     *
     * @return the single [SpanUtils] instance
     */
    fun setVerticalAlign(@Align align: Int): SpanUtils {
        verticalAlign = align
        return this
    }

    /**
     * 设置单击
     *
     * 必须持有 `view.setMovementMethod(LinkMovementMethod.getInstance())`
     *
     * @param clickSpan 单击
     * @return the single [SpanUtils] instance
     */
    fun setClickSpan(clickSpan: ClickableSpan): SpanUtils {
        setMovementMethodIfNeed()
        this.clickSpan = clickSpan
        return this
    }

    /**
     * 设置单击
     *
     * 必须持有 `view.setMovementMethod(LinkMovementMethod.getInstance())`
     *
     * @param color         单击范围的颜色
     * @param underlineText True支持下划线，否则为false。
     * @param listener      单击范围的侦听器。
     * @return the single [SpanUtils] instance
     */
    fun setClickSpan(
        @ColorInt color: Int, underlineText: Boolean, listener: View.OnClickListener?
    ): SpanUtils {
        setMovementMethodIfNeed()
        clickSpan = object : ClickableSpan() {
            override fun updateDrawState(paint: TextPaint) {
                paint.color = color
                paint.isUnderlineText = underlineText
            }

            override fun onClick(widget: View) {
                listener?.onClick(widget)
            }
        }
        return this
    }

    /**
     * 设置url
     *
     * Must set `view.setMovementMethod(LinkMovementMethod.getInstance())`
     *
     * @param url url.
     * @return the single [SpanUtils] instance
     */
    fun setUrl(url: String): SpanUtils {
        setMovementMethodIfNeed()
        this.url = url
        return this
    }

    private fun setMovementMethodIfNeed() {
        if (mTextView != null && mTextView!!.movementMethod == null) {
            mTextView!!.movementMethod = LinkMovementMethod.getInstance()
        }
    }

    /**
     * 设置模糊
     *
     * @param radius 模糊半径
     * @param style  风格
     *
     *  * [BlurMaskFilter.Blur.NORMAL]
     *  * [BlurMaskFilter.Blur.SOLID]
     *  * [BlurMaskFilter.Blur.OUTER]
     *  * [BlurMaskFilter.Blur.INNER]
     *
     * @return the single [SpanUtils] instance
     */
    fun setBlur(
        @FloatRange(from = 0.0, fromInclusive = false) radius: Float, style: Blur?
    ): SpanUtils {
        blurRadius = radius
        this.style = style
        return this
    }

    /**
     * 设置着色器
     *
     *
     * appendLine("图片着色")
     * .setFontSize(64, true)
     * .setShader(BitmapShader(BitmapFactory.decodeResource(resources, R.drawable.span_cheetah),
     * Shader.TileMode.REPEAT,
     * Shader.TileMode.REPEAT))
     *
     * @param shader shader.
     * @return the single [SpanUtils] instance
     */
    fun setShader(shader: Shader): SpanUtils {
        this.shader = shader
        return this
    }

    /**
     * 设置阴影
     *
     * @param radius      阴影的半径。
     * @param dx          X轴偏移，以像素为单位。
     * @param dy          Y轴偏移，以像素为单位。
     * @param shadowColor 阴影的颜色
     * @return the single [SpanUtils] instance
     */
    fun setShadow(
        @FloatRange(from = 0.0, fromInclusive = false) radius: Float, dx: Float, dy: Float, shadowColor: Int
    ): SpanUtils {
        shadowRadius = radius
        shadowDx = dx
        shadowDy = dy
        this.shadowColor = shadowColor
        return this
    }

    /**
     * Set the spans.
     *
     * @param spans spans.
     * @return the single [SpanUtils] instance
     */
    fun setSpans(vararg spans: Any): SpanUtils {
        if (spans.isNotEmpty()) {
            this.spans = arrayOf(spans)
        }
        return this
    }

    /**
     * 将文本附加到文本。
     *
     * @param text text.
     * @return the single [SpanUtils] instance
     */
    fun append(text: CharSequence): SpanUtils {
        apply(mTypeCharSequence)
        mText = text
        return this
    }

    /**
     * 追加一行。
     *
     * @return the single [SpanUtils] instance
     */
    fun appendLine(): SpanUtils {
        apply(mTypeCharSequence)
        mText = LINE_SEPARATOR!!
        return this
    }

    /**
     * 附加文本和一行
     *
     * @return the single [SpanUtils] instance
     */
    fun appendLine(text: CharSequence): SpanUtils {
        apply(mTypeCharSequence)
        mText = text.toString() + LINE_SEPARATOR
        return this
    }
    /**
     * 附加一个图像
     *
     * @param bitmap bitmap.
     * @param align  对准
     *
     *  * [SpanUtils.ALIGN_TOP]
     *  * [SpanUtils.ALIGN_CENTER]
     *  * [SpanUtils.ALIGN_BASELINE]
     *  * [SpanUtils.ALIGN_BOTTOM]
     *
     * @return the single [SpanUtils] instance
     */
    @JvmOverloads
    fun appendImage(bitmap: Bitmap, @Align align: Int = ALIGN_BOTTOM): SpanUtils {
        apply(mTypeImage)
        imageBitmap = bitmap
        alignImage = align
        return this
    }
    /**
     * 附加一个图像
     *
     * @param drawable drawable
     * @param align    对准
     *
     *  * [SpanUtils.ALIGN_TOP]
     *  * [SpanUtils.ALIGN_CENTER]
     *  * [SpanUtils.ALIGN_BASELINE]
     *  * [SpanUtils.ALIGN_BOTTOM]
     *
     * @return the single [SpanUtils] instance
     */
    @JvmOverloads
    fun appendImage(drawable: Drawable, @Align align: Int = ALIGN_BOTTOM): SpanUtils {
        apply(mTypeImage)
        imageDrawable = drawable
        alignImage = align
        return this
    }
    /**
     * 附加一个图像
     *
     * @param uri   图像的uri
     * @param align 对准
     *
     *  * [SpanUtils.ALIGN_TOP]
     *  * [SpanUtils.ALIGN_CENTER]
     *  * [SpanUtils.ALIGN_BASELINE]
     *  * [SpanUtils.ALIGN_BOTTOM]
     *
     * @return the single [SpanUtils] instance
     */
    @JvmOverloads
    fun appendImage(uri: Uri, @Align align: Int = ALIGN_BOTTOM): SpanUtils {
        apply(mTypeImage)
        imageUri = uri
        alignImage = align
        return this
    }
    /**
     * 附加一个图像
     *
     * @param resourceId resource id
     * @param align      对准
     *
     *  * [SpanUtils.ALIGN_TOP]
     *  * [SpanUtils.ALIGN_CENTER]
     *  * [SpanUtils.ALIGN_BASELINE]
     *  * [SpanUtils.ALIGN_BOTTOM]
     *
     * @return the single [SpanUtils] instance
     */
    @JvmOverloads
    fun appendImage(@DrawableRes resourceId: Int, @Align align: Int = ALIGN_BOTTOM): SpanUtils {
        apply(mTypeImage)
        imageResourceId = resourceId
        alignImage = align
        return this
    }
    /**
     * 追加空格
     *
     * @param size 空格大小
     * @param color 空格的颜色
     * @return the single [SpanUtils] instance
     */
    @JvmOverloads
    fun appendSpace(@IntRange(from = 0) size: Int, @ColorInt color: Int = Color.TRANSPARENT): SpanUtils {
        apply(mTypeSpace)
        spaceSize = size
        spaceColor = color
        return this
    }

    private fun apply(type: Int) {
        applyLast()
        mType = type
    }

    fun get(): SpannableStringBuilder {
        return mBuilder
    }

    /**
     * 创建
     *
     * @return the span string
     */
    fun create(): SpannableStringBuilder {
        applyLast()
        if (mTextView != null) {
            mTextView!!.text = mBuilder
        }
        isCreated = true
        return mBuilder
    }

    private fun applyLast() {
        if (isCreated) {
            return
        }
        when (mType) {
            mTypeCharSequence -> {
                updateCharCharSequence()
            }
            mTypeImage -> {
                updateImage()
            }
            mTypeSpace -> {
                updateSpace()
            }
        }
        setDefault()
    }

    private fun updateCharCharSequence() {
        if (mText.isEmpty()) {
            return
        }
        var start = mBuilder.length
        if (start == 0 && lineHeight != -1) { // bug of LineHeightSpan when first line
            mBuilder.append(2.toChar().toString()).append("\n")
                .setSpan(AbsoluteSizeSpan(0), 0, 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            start = 2
        }
        mBuilder.append(mText)
        val end = mBuilder.length
        if (verticalAlign != -1) {
            mBuilder.setSpan(VerticalAlignSpan(verticalAlign), start, end, flag)
        }
        if (foregroundColor != COLOR_DEFAULT) {
            mBuilder.setSpan(ForegroundColorSpan(foregroundColor), start, end, flag)
        }
        if (backgroundColor != COLOR_DEFAULT) {
            mBuilder.setSpan(BackgroundColorSpan(backgroundColor), start, end, flag)
        }
        if (first != -1) {
            mBuilder.setSpan(LeadingMarginSpan.Standard(first, rest), start, end, flag)
        }
        if (quoteColor != COLOR_DEFAULT) {
            mBuilder.setSpan(
                CustomQuoteSpan(quoteColor, stripeWidth, quoteGapWidth), start, end, flag
            )
        }
        if (bulletColor != COLOR_DEFAULT) {
            mBuilder.setSpan(
                CustomBulletSpan(bulletColor, bulletRadius, bulletGapWidth), start, end, flag
            )
        }
        if (fontSize != -1) {
            mBuilder.setSpan(AbsoluteSizeSpan(fontSize, false), start, end, flag)
        }
        if (proportion != -1f) {
            mBuilder.setSpan(RelativeSizeSpan(proportion), start, end, flag)
        }
        if (xProportion != -1f) {
            mBuilder.setSpan(ScaleXSpan(xProportion), start, end, flag)
        }
        if (lineHeight != -1) {
            mBuilder.setSpan(CustomLineHeightSpan(lineHeight, alignLine), start, end, flag)
        }
        if (isStrikethrough) {
            mBuilder.setSpan(StrikethroughSpan(), start, end, flag)
        }
        if (isUnderline) {
            mBuilder.setSpan(UnderlineSpan(), start, end, flag)
        }
        if (isSuperscript) {
            mBuilder.setSpan(SuperscriptSpan(), start, end, flag)
        }
        if (isSubscript) {
            mBuilder.setSpan(SubscriptSpan(), start, end, flag)
        }
        if (isBold) {
            mBuilder.setSpan(StyleSpan(Typeface.BOLD), start, end, flag)
        }
        if (isItalic) {
            mBuilder.setSpan(StyleSpan(Typeface.ITALIC), start, end, flag)
        }
        if (isBoldItalic) {
            mBuilder.setSpan(StyleSpan(Typeface.BOLD_ITALIC), start, end, flag)
        }
        if (fontFamily != null) {
            mBuilder.setSpan(TypefaceSpan(fontFamily), start, end, flag)
        }
        if (typeface != null) {
            mBuilder.setSpan(CustomTypefaceSpan(typeface!!), start, end, flag)
        }
        if (alignment != null) {
            mBuilder.setSpan(AlignmentSpan.Standard(alignment!!), start, end, flag)
        }
        if (clickSpan != null) {
            mBuilder.setSpan(clickSpan, start, end, flag)
        }
        if (url != null) {
            mBuilder.setSpan(URLSpan(url), start, end, flag)
        }
        if (blurRadius != -1f) {
            mBuilder.setSpan(
                MaskFilterSpan(BlurMaskFilter(blurRadius, style)), start, end, flag
            )
        }
        if (shader != null) {
            mBuilder.setSpan(ShaderSpan(shader!!), start, end, flag)
        }
        if (shadowRadius != -1f) {
            mBuilder.setSpan(
                ShadowSpan(shadowRadius, shadowDx, shadowDy, shadowColor), start, end, flag
            )
        }
        if (spans != null) {
            for (span in spans!!) {
                mBuilder.setSpan(span, start, end, flag)
            }
        }
    }

    private fun updateImage() {
        val start = mBuilder.length
        mText = "<img>"
        updateCharCharSequence()
        val end = mBuilder.length
        if (imageBitmap != null) {
            mBuilder.setSpan(CustomImageSpan(imageBitmap, alignImage), start, end, flag)
        } else if (imageDrawable != null) {
            mBuilder.setSpan(CustomImageSpan(imageDrawable, alignImage), start, end, flag)
        } else if (imageUri != null) {
            mBuilder.setSpan(CustomImageSpan(imageUri, alignImage), start, end, flag)
        } else if (imageResourceId != -1) {
            mBuilder.setSpan(CustomImageSpan(imageResourceId, alignImage), start, end, flag)
        }
    }

    private fun updateSpace() {
        val start = mBuilder.length
        mText = "< >"
        updateCharCharSequence()
        val end = mBuilder.length
        mBuilder.setSpan(SpaceSpan(spaceSize, spaceColor), start, end, flag)
    }

    private class SerializableSpannableStringBuilder : SpannableStringBuilder(), Serializable {
        private val serialVersionUID = 4909567650765875771L
    }

    companion object {
        private const val COLOR_DEFAULT = -0x1000001
        const val ALIGN_BOTTOM = 0
        const val ALIGN_BASELINE = 1
        const val ALIGN_CENTER = 2
        const val ALIGN_TOP = 3
        private val LINE_SEPARATOR = System.getProperty("line.separator")
        fun with(textView: TextView): SpanUtils {
            return SpanUtils(textView)
        }
    }
}