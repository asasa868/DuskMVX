package com.lzq.dawn.view

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.lzq.dawn.R

/**
 * @author Lzq
 * @projectName com.lzq.dawn.view
 * @date : Created by Lzq on 2023/12/25 16:27
 * @description: 占位的弹窗
 */
class LoadingView : LinearLayout {
    private var mShapeLoadingView: ShapeLoadingView? = null
    private var mIndicationIm: ImageView? = null
    private var mLoadTextView: TextView? = null
    private var mUpAnimatorSet: AnimatorSet? = null
    private var mDownAnimatorSet: AnimatorSet? = null
    private var mStopped = false
    var delay = 0

    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(
        context,
        attrs,
        defStyleAttr,
        defStyleRes
    ) {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        orientation = VERTICAL
        mDistance = dip2px(context).toFloat()
        LayoutInflater.from(context).inflate(R.layout.dawn_load_view, this, true)
        mShapeLoadingView = findViewById(R.id.dawn_loading_dialog)
        mIndicationIm = findViewById(R.id.dawn_iv_indication)
        mLoadTextView = findViewById(R.id.dawn_tv_prompt)
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.dawn_LoadingView)
        val loadText = typedArray.getString(R.styleable.dawn_LoadingView_dawn_loadingText)
        val textAppearance = typedArray.getResourceId(R.styleable.dawn_LoadingView_dawn_loadingText, -1)
        delay = typedArray.getInteger(R.styleable.dawn_LoadingView_dawn_delay, 80)
        typedArray.recycle()
        if (textAppearance != -1) {
            mLoadTextView?.setTextAppearance(textAppearance)
        }
        loadingText = loadText
    }

    private fun dip2px(context: Context): Int {
        val scale = context.resources.displayMetrics.density
        return (54.0.toFloat() * scale + 0.5f).toInt()
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        if (visibility == VISIBLE) {
            startLoading(delay.toLong())
        }
    }

    private val mFreeFallRunnable = Runnable {
        mStopped = false
        freeFall()
    }

    private fun startLoading(delay: Long) {
        if (mDownAnimatorSet != null && mDownAnimatorSet!!.isRunning) {
            return
        }
        removeCallbacks(mFreeFallRunnable)
        if (delay > 0) {
            postDelayed(mFreeFallRunnable, delay)
        } else {
            post(mFreeFallRunnable)
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        stopLoading()
    }

    private fun stopLoading() {
        mStopped = true
        if (mUpAnimatorSet != null) {
            if (mUpAnimatorSet!!.isRunning) {
                mUpAnimatorSet!!.cancel()
            }
            mUpAnimatorSet!!.removeAllListeners()
            for (animator in mUpAnimatorSet!!.childAnimations) {
                animator.removeAllListeners()
            }
            mUpAnimatorSet = null
        }
        if (mDownAnimatorSet != null) {
            if (mDownAnimatorSet!!.isRunning) {
                mDownAnimatorSet!!.cancel()
            }
            mDownAnimatorSet!!.removeAllListeners()
            for (animator in mDownAnimatorSet!!.childAnimations) {
                animator.removeAllListeners()
            }
            mDownAnimatorSet = null
        }
        removeCallbacks(mFreeFallRunnable)
    }

    override fun setVisibility(visibility: Int) {
        this.setVisibility(visibility, delay)
    }

    private fun setVisibility(visibility: Int, delay: Int) {
        super.setVisibility(visibility)
        if (visibility == VISIBLE) {
            startLoading(delay.toLong())
        } else {
            stopLoading()
        }
    }

    var loadingText: CharSequence?
        get() = mLoadTextView!!.text
        set(loadingText) {
            if (TextUtils.isEmpty(loadingText)) {
                mLoadTextView!!.visibility = GONE
            } else {
                mLoadTextView!!.visibility = VISIBLE
            }
            mLoadTextView!!.text = loadingText
        }

    /**
     * 上抛
     */
    fun upThrow() {
        if (mUpAnimatorSet == null) {
            val objectAnimator = ObjectAnimator.ofFloat(mShapeLoadingView!!, "translationY", mDistance, 0f)
            val scaleIndication = ObjectAnimator.ofFloat(mIndicationIm!!, "scaleX", 1f, 0.2f)
            val objectAnimator1 = ObjectAnimator.ofFloat(mShapeLoadingView!!, "rotation", 0f, 180f)
            mUpAnimatorSet = AnimatorSet()
            mUpAnimatorSet!!.playTogether(objectAnimator, objectAnimator1, scaleIndication)
            mUpAnimatorSet!!.duration = ANIMATION_DURATION.toLong()
            mUpAnimatorSet!!.interpolator = DecelerateInterpolator(FACTOR)
            mUpAnimatorSet!!.addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {}
                override fun onAnimationEnd(animation: Animator) {
                    if (!mStopped) {
                        freeFall()
                    }
                }

                override fun onAnimationCancel(animation: Animator) {}
                override fun onAnimationRepeat(animation: Animator) {}
            })
        }
        mUpAnimatorSet!!.start()
    }

    /**
     * 下落
     */
    fun freeFall() {
        if (mDownAnimatorSet == null) {
            val objectAnimator = ObjectAnimator.ofFloat(mShapeLoadingView!!, "translationY", 0f, mDistance)
            val scaleIndication = ObjectAnimator.ofFloat(mIndicationIm!!, "scaleX", 0.2f, 1f)
            mDownAnimatorSet = AnimatorSet()
            mDownAnimatorSet!!.playTogether(objectAnimator, scaleIndication)
            mDownAnimatorSet!!.duration = ANIMATION_DURATION.toLong()
            mDownAnimatorSet!!.interpolator = AccelerateInterpolator(FACTOR)
            mDownAnimatorSet!!.addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {}
                override fun onAnimationEnd(animation: Animator) {
                    if (!mStopped) {
                        mShapeLoadingView!!.changeShape()
                        upThrow()
                    }
                }

                override fun onAnimationCancel(animation: Animator) {}
                override fun onAnimationRepeat(animation: Animator) {}
            })
        }
        mDownAnimatorSet!!.start()
    }

    companion object {
        private const val ANIMATION_DURATION = 500
        private const val FACTOR = 1.2f
        private var mDistance = 200f
    }
}