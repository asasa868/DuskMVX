package com.lzq.dawn.view

import android.animation.ArgbEvaluator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import com.lzq.dawn.R

/**
 * @author Lzq
 * @projectName com.lzq.dawn.view
 * @date : Created by Lzq on 2023/12/25 16:27
 * @description: 占位的弹窗
 */
class ShapeLoadingView : View {
    private var mShape = Shape.SHAPE_CIRCLE
    private val mArgbEvaluator = ArgbEvaluator()
    private var mTriangleColor = 0
    private var mCircleColor = 0
    private var mRectColor = 0
    private var mIsLoading = false
    private var mPaint: Paint? = null
    private var triangPath: Path? = null
    private var circlePath: Path? = null
    private var rectPath: Path? = null
    private var mControlX = 0f
    private var mControlY = 0f
    private var mAnimPercent = 0f

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(
        context,
        attrs,
        defStyleAttr,
        defStyleRes
    ) {
        init(context)
    }

    private fun init(context: Context) {
        mTriangleColor = getColor(context, R.color.dawn_triangle)
        mCircleColor = getColor(context, R.color.dawn_circle)
        mRectColor = getColor(context, R.color.dawn_rect)
        mPaint = Paint()
        mPaint!!.color = mTriangleColor
        mPaint!!.isAntiAlias = true
        mPaint!!.style = Paint.Style.FILL_AND_STROKE
        triangPath = Path()
        circlePath = Path()
        rectPath = Path()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (visibility == GONE) {
            return
        }
        when (mShape) {
            Shape.SHAPE_TRIANGLE -> drawTriangle(canvas)
            Shape.SHAPE_CIRCLE -> drawCircle(canvas)
            Shape.SHAPE_RECT -> drawRect(canvas)
        }
    }

    private fun drawTriangle(canvas: Canvas) {
        if (mIsLoading) {
            mAnimPercent += 0.1611113.toFloat()
            val color = mArgbEvaluator.evaluate(mAnimPercent, mTriangleColor, mCircleColor) as Int
            mPaint!!.color = color
            triangPath!!.moveTo(relativeXFromView(0.5f), relativeYFromView(0f))
            if (mAnimPercent >= 1) {
                mShape = Shape.SHAPE_CIRCLE
                mIsLoading = false
                mAnimPercent = 1f
            }
            val controlX = mControlX - relativeXFromView(mAnimPercent * M_TRIANGLE2_CIRCLE) * GEN_HAO3
            val controlY = mControlY - relativeYFromView(mAnimPercent * M_TRIANGLE2_CIRCLE)
            triangPath!!.quadTo(
                relativeXFromView(1f) - controlX,
                controlY,
                relativeXFromView(0.5f + GEN_HAO3 / 4),
                relativeYFromView(0.75f)
            )
            triangPath!!.quadTo(
                relativeXFromView(0.5f),
                relativeYFromView(0.75f + 2 * mAnimPercent * M_TRIANGLE2_CIRCLE),
                relativeXFromView(0.5f - GEN_HAO3 / 4),
                relativeYFromView(0.75f)
            )
            triangPath!!.quadTo(controlX, controlY, relativeXFromView(0.5f), relativeYFromView(0f))
            triangPath!!.close()
            canvas.drawPath(triangPath!!, mPaint!!)
            invalidate()
        } else {
            mPaint!!.color = mTriangleColor
            triangPath!!.moveTo(relativeXFromView(0.5f), relativeYFromView(0f))
            triangPath!!.lineTo(relativeXFromView(1f), relativeYFromView(GEN_HAO3 / 2f))
            triangPath!!.lineTo(relativeXFromView(0f), relativeYFromView(GEN_HAO3 / 2f))
            mControlX = relativeXFromView(0.5f - GEN_HAO3 / 8.0f)
            mControlY = relativeYFromView(3 / 8.0f)
            mAnimPercent = 0f
            triangPath!!.close()
            canvas.drawPath(triangPath!!, mPaint!!)
        }
    }

    private fun drawCircle(canvas: Canvas) {
        if (mIsLoading) {
            val magicNumber = M_MAGIC_NUMBER + mAnimPercent
            mAnimPercent += 0.12.toFloat()
            if (magicNumber + mAnimPercent >= 1.9f) {
                mShape = Shape.SHAPE_RECT
                mIsLoading = false
            }
            val color = mArgbEvaluator.evaluate(mAnimPercent, mCircleColor, mRectColor) as Int
            mPaint!!.color = color
            circlePath!!.moveTo(relativeXFromView(0.5f), relativeYFromView(0f))
            circlePath!!.cubicTo(
                relativeXFromView(0.5f + magicNumber / 2),
                relativeYFromView(0f),
                relativeXFromView(1f),
                relativeYFromView(0.5f - magicNumber / 2),
                relativeXFromView(1f),
                relativeYFromView(0.5f)
            )
            circlePath!!.cubicTo(
                relativeXFromView(1f),
                relativeXFromView(0.5f + magicNumber / 2),
                relativeXFromView(0.5f + magicNumber / 2),
                relativeYFromView(1f),
                relativeXFromView(0.5f),
                relativeYFromView(1f)
            )
            circlePath!!.cubicTo(
                relativeXFromView(0.5f - magicNumber / 2),
                relativeXFromView(1f),
                relativeXFromView(0f),
                relativeYFromView(0.5f + magicNumber / 2),
                relativeXFromView(0f),
                relativeYFromView(0.5f)
            )
            circlePath!!.cubicTo(
                relativeXFromView(0f),
                relativeXFromView(0.5f - magicNumber / 2),
                relativeXFromView(0.5f - magicNumber / 2),
                relativeYFromView(0f),
                relativeXFromView(0.5f),
                relativeYFromView(0f)
            )
            circlePath!!.close()
            canvas.drawPath(circlePath!!, mPaint!!)
            invalidate()
        } else {
            mPaint!!.color = mCircleColor
            val magicNumber = M_MAGIC_NUMBER
            circlePath!!.moveTo(relativeXFromView(0.5f), relativeYFromView(0f))
            circlePath!!.cubicTo(
                relativeXFromView(0.5f + magicNumber / 2),
                0f,
                relativeXFromView(1f),
                relativeYFromView(magicNumber / 2),
                relativeXFromView(1f),
                relativeYFromView(0.5f)
            )
            circlePath!!.cubicTo(
                relativeXFromView(1f),
                relativeXFromView(0.5f + magicNumber / 2),
                relativeXFromView(0.5f + magicNumber / 2),
                relativeYFromView(1f),
                relativeXFromView(0.5f),
                relativeYFromView(1f)
            )
            circlePath!!.cubicTo(
                relativeXFromView(0.5f - magicNumber / 2),
                relativeXFromView(1f),
                relativeXFromView(0f),
                relativeYFromView(0.5f + magicNumber / 2),
                relativeXFromView(0f),
                relativeYFromView(0.5f)
            )
            circlePath!!.cubicTo(
                relativeXFromView(0f),
                relativeXFromView(0.5f - magicNumber / 2),
                relativeXFromView(0.5f - magicNumber / 2),
                relativeYFromView(0f),
                relativeXFromView(0.5f),
                relativeYFromView(0f)
            )
            mAnimPercent = 0f
            circlePath!!.close()
            canvas.drawPath(circlePath!!, mPaint!!)
        }
    }

    private fun drawRect(canvas: Canvas) {
        if (mIsLoading) {
            mAnimPercent += 0.15.toFloat()
            if (mAnimPercent >= 1) {
                mShape = Shape.SHAPE_TRIANGLE
                mIsLoading = false
                mAnimPercent = 1f
            }
            val color = mArgbEvaluator.evaluate(mAnimPercent, mRectColor, mTriangleColor) as Int
            mPaint!!.color = color
            rectPath!!.moveTo(relativeXFromView(0.5f * mAnimPercent), 0f)
            rectPath!!.lineTo(relativeYFromView(1 - 0.5f * mAnimPercent), 0f)
            val distanceX = mControlX * mAnimPercent
            val distanceY = (relativeYFromView(1f) - mControlY) * mAnimPercent
            rectPath!!.lineTo(relativeXFromView(1f) - distanceX, relativeYFromView(1f) - distanceY)
            rectPath!!.lineTo(relativeXFromView(0f) + distanceX, relativeYFromView(1f) - distanceY)
            rectPath!!.close()
            canvas.drawPath(rectPath!!, mPaint!!)
            invalidate()
        } else {
            mPaint!!.color = mRectColor
            mControlX = relativeXFromView(0.5f - GEN_HAO3 / 4)
            mControlY = relativeYFromView(0.75f)
            rectPath!!.moveTo(relativeXFromView(0f), relativeYFromView(0f))
            rectPath!!.lineTo(relativeXFromView(1f), relativeYFromView(0f))
            rectPath!!.lineTo(relativeXFromView(1f), relativeYFromView(1f))
            rectPath!!.lineTo(relativeXFromView(0f), relativeYFromView(1f))
            rectPath!!.close()
            mAnimPercent = 0f
            canvas.drawPath(rectPath!!, mPaint!!)
        }
    }

    private fun relativeXFromView(percent: Float): Float {
        return width * percent
    }

    private fun relativeYFromView(percent: Float): Float {
        return height * percent
    }

    fun changeShape() {
        mIsLoading = true
        invalidate()
    }

    enum class Shape {
        /**
         * 形状三角形
         */
        SHAPE_TRIANGLE,

        /**
         * 形状矩形
         */
        SHAPE_RECT,

        /**
         * 形状圆
         */
        SHAPE_CIRCLE
    }

    override fun setVisibility(visibility: Int) {
        super.setVisibility(visibility)
        if (visibility == VISIBLE) {
            invalidate()
        }
    }

    var shape: Shape
        get() = mShape
        set(shape) {
            mIsLoading = true
            mShape = shape
            invalidate()
        }

    private fun getColor(context: Context, id: Int): Int {
        return context.getColor(id)
    }

    companion object {
        /**
         * 用赛贝尔曲线画圆
         */
        private const val M_MAGIC_NUMBER = 0.5522848f
        private const val GEN_HAO3 = 1.7320508f
        private const val M_TRIANGLE2_CIRCLE = 0.25555554f
    }
}