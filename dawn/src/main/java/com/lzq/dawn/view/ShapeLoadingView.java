package com.lzq.dawn.view;

import android.animation.ArgbEvaluator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import com.lzq.dawn.R;


/**
 * @author Lzq
 * @projectName com.lzq.dawn.view
 * @date : Created by Lzq on 2023/12/25 16:27
 * @description: 占位的弹窗
 */
public class ShapeLoadingView extends View {
    /**
     * 用赛贝尔曲线画圆
     */
    private static final float M_MAGIC_NUMBER = 0.55228475f;
    private static final float GEN_HAO3 = 1.7320508075689f;
    private static final float M_TRIANGLE2_CIRCLE = 0.25555555f;
    private Shape mShape = Shape.SHAPE_CIRCLE;
    private final ArgbEvaluator mArgbEvaluator = new ArgbEvaluator();
    private int mTriangleColor;
    private int mCircleColor;
    private int mRectColor;
    public boolean mIsLoading = false;
    private Paint mPaint;
    private Path triangPath;
    private Path circlePath;
    private Path rectPath;
    private float mControlX = 0;
    private float mControlY = 0;
    private float mAnimPercent;


    public ShapeLoadingView(Context context) {
        super(context);
        init(context);
    }

    public ShapeLoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ShapeLoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public ShapeLoadingView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        mTriangleColor = getColor(context, R.color.dawn_triangle);
        mCircleColor = getColor(context, R.color.dawn_circle);
        mRectColor = getColor(context, R.color.dawn_rect);
        mPaint = new Paint();
        mPaint.setColor(mTriangleColor);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        triangPath = new Path();
        circlePath = new Path();
        rectPath = new Path();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (getVisibility() == GONE) {
            return;
        }

        switch (mShape) {
            case SHAPE_TRIANGLE:
                drawTriangle(canvas);
                break;
            case SHAPE_CIRCLE:
                drawCircle(canvas);
                break;
            case SHAPE_RECT:
                drawRect(canvas);
                break;
            default:
                break;
        }
    }

    private void drawTriangle(Canvas canvas){
        if (mIsLoading) {
            mAnimPercent += 0.1611113;
            int color = (int) mArgbEvaluator.evaluate(mAnimPercent, mTriangleColor, mCircleColor);
            mPaint.setColor(color);
            triangPath.moveTo(relativeXFromView(0.5f), relativeYFromView(0f));
            if (mAnimPercent >= 1) {
                mShape = Shape.SHAPE_CIRCLE;
                mIsLoading = false;
                mAnimPercent = 1;
            }
            float controlX = mControlX - relativeXFromView(mAnimPercent * M_TRIANGLE2_CIRCLE) * GEN_HAO3;
            float controlY = mControlY - relativeYFromView(mAnimPercent * M_TRIANGLE2_CIRCLE);
            triangPath.quadTo(relativeXFromView(1) - controlX, controlY, relativeXFromView(0.5f + GEN_HAO3 / 4), relativeYFromView(0.75f));
            triangPath.quadTo(relativeXFromView(0.5f), relativeYFromView(0.75f + 2 * mAnimPercent * M_TRIANGLE2_CIRCLE), relativeXFromView(0.5f - GEN_HAO3 / 4), relativeYFromView(0.75f));
            triangPath.quadTo(controlX, controlY, relativeXFromView(0.5f), relativeYFromView(0f));
            triangPath.close();

            canvas.drawPath(triangPath, mPaint);
            invalidate();
        } else {
            mPaint.setColor(mTriangleColor);
            triangPath.moveTo(relativeXFromView(0.5f), relativeYFromView(0f));
            triangPath.lineTo(relativeXFromView(1), relativeYFromView(GEN_HAO3 / 2f));
            triangPath.lineTo(relativeXFromView(0), relativeYFromView(GEN_HAO3 / 2f));
            mControlX = relativeXFromView(0.5f - GEN_HAO3 / 8.0f);
            mControlY = relativeYFromView(3 / 8.0f);
            mAnimPercent = 0;
            triangPath.close();
            canvas.drawPath(triangPath, mPaint);
        }
    }

    private void drawCircle(Canvas canvas){
        if (mIsLoading) {
            float magicNumber = M_MAGIC_NUMBER + mAnimPercent;
            mAnimPercent += 0.12;
            if (magicNumber + mAnimPercent >= 1.9f) {
                mShape = Shape.SHAPE_RECT;
                mIsLoading = false;
            }
            int color = (int) mArgbEvaluator.evaluate(mAnimPercent, mCircleColor, mRectColor);
            mPaint.setColor(color);
            circlePath.moveTo(relativeXFromView(0.5f), relativeYFromView(0f));
            circlePath.cubicTo(relativeXFromView(0.5f + magicNumber / 2), relativeYFromView(0f),
                    relativeXFromView(1), relativeYFromView(0.5f - magicNumber / 2),
                    relativeXFromView(1f), relativeYFromView(0.5f));
            circlePath.cubicTo(
                    relativeXFromView(1), relativeXFromView(0.5f + magicNumber / 2),
                    relativeXFromView(0.5f + magicNumber / 2), relativeYFromView(1f),
                    relativeXFromView(0.5f), relativeYFromView(1f));
            circlePath.cubicTo(relativeXFromView(0.5f - magicNumber / 2), relativeXFromView(1f),
                    relativeXFromView(0), relativeYFromView(0.5f + magicNumber / 2),
                    relativeXFromView(0f), relativeYFromView(0.5f));
            circlePath.cubicTo(relativeXFromView(0f), relativeXFromView(0.5f - magicNumber / 2),
                    relativeXFromView(0.5f - magicNumber / 2), relativeYFromView(0),
                    relativeXFromView(0.5f), relativeYFromView(0f));
            circlePath.close();
            canvas.drawPath(circlePath, mPaint);
            invalidate();
        } else {
            mPaint.setColor(mCircleColor);
            float magicNumber = M_MAGIC_NUMBER;
            circlePath.moveTo(relativeXFromView(0.5f), relativeYFromView(0f));
            circlePath.cubicTo(relativeXFromView(0.5f + magicNumber / 2), 0,
                    relativeXFromView(1), relativeYFromView(magicNumber / 2),
                    relativeXFromView(1f), relativeYFromView(0.5f));
            circlePath.cubicTo(
                    relativeXFromView(1), relativeXFromView(0.5f + magicNumber / 2),
                    relativeXFromView(0.5f + magicNumber / 2), relativeYFromView(1f),
                    relativeXFromView(0.5f), relativeYFromView(1f));
            circlePath.cubicTo(relativeXFromView(0.5f - magicNumber / 2), relativeXFromView(1f),
                    relativeXFromView(0), relativeYFromView(0.5f + magicNumber / 2),
                    relativeXFromView(0f), relativeYFromView(0.5f));
            circlePath.cubicTo(relativeXFromView(0f), relativeXFromView(0.5f - magicNumber / 2),
                    relativeXFromView(0.5f - magicNumber / 2), relativeYFromView(0),
                    relativeXFromView(0.5f), relativeYFromView(0f));
            mAnimPercent = 0;
            circlePath.close();
            canvas.drawPath(circlePath, mPaint);
        }
    }

    private void drawRect(Canvas canvas){
        if (mIsLoading) {
            mAnimPercent += 0.15;
            if (mAnimPercent >= 1) {
                mShape = Shape.SHAPE_TRIANGLE;
                mIsLoading = false;
                mAnimPercent = 1;
            }
            int color = (int) mArgbEvaluator.evaluate(mAnimPercent, mRectColor, mTriangleColor);
            mPaint.setColor(color);
            rectPath.moveTo(relativeXFromView(0.5f * mAnimPercent), 0);
            rectPath.lineTo(relativeYFromView(1 - 0.5f * mAnimPercent), 0);
            float distanceX = (mControlX) * mAnimPercent;
            float distanceY = (relativeYFromView(1f) - mControlY) * mAnimPercent;
            rectPath.lineTo(relativeXFromView(1f) - distanceX, relativeYFromView(1f) - distanceY);
            rectPath.lineTo(relativeXFromView(0f) + distanceX, relativeYFromView(1f) - distanceY);
            rectPath.close();
            canvas.drawPath(rectPath, mPaint);
            invalidate();
        } else {
            mPaint.setColor(mRectColor);
            mControlX = relativeXFromView(0.5f - GEN_HAO3 / 4);
            mControlY = relativeYFromView(0.75f);
            rectPath.moveTo(relativeXFromView(0f), relativeYFromView(0f));
            rectPath.lineTo(relativeXFromView(1f), relativeYFromView(0f));
            rectPath.lineTo(relativeXFromView(1f), relativeYFromView(1f));
            rectPath.lineTo(relativeXFromView(0f), relativeYFromView(1f));
            rectPath.close();
            mAnimPercent = 0;
            canvas.drawPath(rectPath, mPaint);
        }
    }


    private float relativeXFromView(float percent) {
        return getWidth() * percent;
    }

    private float relativeYFromView(float percent) {
        return getHeight() * percent;
    }


    public void changeShape() {
        mIsLoading = true;
        invalidate();
    }

    public void setShape(Shape shape) {
        mIsLoading = true;
        mShape = shape;
        invalidate();
    }

    public enum Shape {
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


    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);

        if (visibility == VISIBLE) {
            invalidate();
        }
    }

    public Shape getShape() {
        return mShape;
    }

    private int getColor(Context context, int id) {
        return context.getColor(id);
    }
}


