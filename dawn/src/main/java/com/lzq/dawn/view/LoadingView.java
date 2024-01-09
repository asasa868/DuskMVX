package com.lzq.dawn.view;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.lzq.dawn.R;


/**
 * @author Lzq
 * @projectName com.lzq.dawn.view
 * @date : Created by Lzq on 2023/12/25 16:27
 * @description: 占位的弹窗
 */
public class LoadingView extends LinearLayout {

    private static final int ANIMATION_DURATION = 500;

    private static final float FACTOR = 1.2f;

    private static float mDistance = 200;

    private ShapeLoadingView mShapeLoadingView;

    private ImageView mIndicationIm;

    private TextView mLoadTextView;

    private AnimatorSet mUpAnimatorSet;

    private AnimatorSet mDownAnimatorSet;

    private boolean mStopped = false;

    private int mDelay;

    public LoadingView(Context context) {
        super(context);
        init(context, null);
    }

    public LoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);

    }


    public LoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public LoadingView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {

        setOrientation(VERTICAL);
        mDistance = dip2px(context);
        LayoutInflater.from(context).inflate(R.layout.dawn_load_view, this, true);
        mShapeLoadingView = findViewById(R.id.shapeLoadingView);
        mIndicationIm = findViewById(R.id.indication);
        mLoadTextView = findViewById(R.id.promptTV);


        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.dawn_LoadingView);
        String loadText = typedArray.getString(R.styleable.dawn_LoadingView_dawn_loadingText);
        int textAppearance = typedArray.getResourceId(R.styleable.dawn_LoadingView_dawn_loadingText, -1);
        mDelay = typedArray.getInteger(R.styleable.dawn_LoadingView_dawn_delay, 80);
        typedArray.recycle();

        if (textAppearance != -1) {
            mLoadTextView.setTextAppearance(textAppearance);
        }
        setLoadingText(loadText);
    }

    private int dip2px(Context context) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) ((float) 54.0 * scale + 0.5f);
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getVisibility() == VISIBLE) {
            startLoading(mDelay);
        }
    }

    private final Runnable mFreeFallRunnable = () -> {
        mStopped = false;
        freeFall();
    };

    private void startLoading(long delay) {
        if (mDownAnimatorSet != null && mDownAnimatorSet.isRunning()) {
            return;
        }
        this.removeCallbacks(mFreeFallRunnable);
        if (delay > 0) {
            this.postDelayed(mFreeFallRunnable, delay);
        } else {
            this.post(mFreeFallRunnable);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopLoading();
    }

    private void stopLoading() {
        mStopped = true;
        if (mUpAnimatorSet != null) {
            if (mUpAnimatorSet.isRunning()) {
                mUpAnimatorSet.cancel();
            }
            mUpAnimatorSet.removeAllListeners();
            for (Animator animator : mUpAnimatorSet.getChildAnimations()) {
                animator.removeAllListeners();
            }
            mUpAnimatorSet = null;
        }
        if (mDownAnimatorSet != null) {
            if (mDownAnimatorSet.isRunning()) {
                mDownAnimatorSet.cancel();
            }
            mDownAnimatorSet.removeAllListeners();
            for (Animator animator : mDownAnimatorSet.getChildAnimations()) {
                animator.removeAllListeners();
            }
            mDownAnimatorSet = null;
        }
        this.removeCallbacks(mFreeFallRunnable);
    }

    @Override
    public void setVisibility(int visibility) {
        this.setVisibility(visibility, mDelay);
    }

    public void setVisibility(int visibility, int delay) {
        super.setVisibility(visibility);
        if (visibility == View.VISIBLE) {
            startLoading(delay);
        } else {
            stopLoading();
        }
    }

    public void setDelay(int delay) {
        mDelay = delay;
    }

    public int getDelay() {
        return mDelay;
    }

    public void setLoadingText(CharSequence loadingText) {
        if (TextUtils.isEmpty(loadingText)) {
            mLoadTextView.setVisibility(GONE);
        } else {
            mLoadTextView.setVisibility(VISIBLE);
        }
        mLoadTextView.setText(loadingText);
    }

    public CharSequence getLoadingText() {
        return mLoadTextView.getText();
    }

    /**
     * 上抛
     */
    public void upThrow() {
        if (mUpAnimatorSet == null) {
            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(mShapeLoadingView, "translationY", mDistance, 0);
            ObjectAnimator scaleIndication = ObjectAnimator.ofFloat(mIndicationIm, "scaleX", 1f, 0.2f);
            ObjectAnimator objectAnimator1 = ObjectAnimator.ofFloat(mShapeLoadingView, "rotation", 0, 180);

            mUpAnimatorSet = new AnimatorSet();
            mUpAnimatorSet.playTogether(objectAnimator, objectAnimator1, scaleIndication);

            mUpAnimatorSet.setDuration(ANIMATION_DURATION);
            mUpAnimatorSet.setInterpolator(new DecelerateInterpolator(FACTOR));

            mUpAnimatorSet.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(@NonNull Animator animation) {

                }

                @Override
                public void onAnimationEnd(@NonNull Animator animation) {
                    if (!mStopped) {
                        freeFall();
                    }

                }

                @Override
                public void onAnimationCancel(@NonNull Animator animation) {

                }

                @Override
                public void onAnimationRepeat(@NonNull Animator animation) {

                }
            });
        }
        mUpAnimatorSet.start();
    }

    /**
     * 下落
     */
    public void freeFall() {
        if (mDownAnimatorSet == null) {
            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(mShapeLoadingView, "translationY", 0, mDistance);
            ObjectAnimator scaleIndication = ObjectAnimator.ofFloat(mIndicationIm, "scaleX", 0.2f, 1f);

            mDownAnimatorSet = new AnimatorSet();
            mDownAnimatorSet.playTogether(objectAnimator, scaleIndication);
            mDownAnimatorSet.setDuration(ANIMATION_DURATION);
            mDownAnimatorSet.setInterpolator(new AccelerateInterpolator(FACTOR));
            mDownAnimatorSet.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(@NonNull Animator animation) {

                }

                @Override
                public void onAnimationEnd(@NonNull Animator animation) {
                    if (!mStopped) {
                        mShapeLoadingView.changeShape();
                        upThrow();
                    }
                }

                @Override
                public void onAnimationCancel(@NonNull Animator animation) {

                }

                @Override
                public void onAnimationRepeat(@NonNull Animator animation) {

                }
            });
        }
        mDownAnimatorSet.start();
    }

}