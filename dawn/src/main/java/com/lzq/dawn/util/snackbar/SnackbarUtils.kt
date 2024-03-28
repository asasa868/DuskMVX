package com.lzq.dawn.util.snackbar

import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.widget.FrameLayout
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.IntDef
import androidx.annotation.IntRange
import androidx.annotation.LayoutRes
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.snackbar.Snackbar
import java.lang.ref.WeakReference

/**
 * @Name :SnackbarUtils
 * @Time :2022/8/30 11:43
 * @Author :  Lzq
 * @Desc : Snackbar
 */
class SnackbarUtils private constructor(parent: View) {
    @IntDef(LENGTH_INDEFINITE, LENGTH_SHORT, LENGTH_LONG)
    @Retention(AnnotationRetention.SOURCE)
    annotation class Duration

    private val view: View
    private var message: CharSequence? = null
    private var messageColor = 0
    private var bgColor = 0
    private var bgResource = 0
    private var duration = 0
    private var actionText: CharSequence? = null
    private var actionTextColor = 0
    private var actionListener: View.OnClickListener? = null
    private var bottomMargin = 0

    init {
        setDefault()
        view = parent
    }

    private fun setDefault() {
        message = ""
        messageColor = COLOR_DEFAULT
        bgColor = COLOR_DEFAULT
        bgResource = -1
        duration = LENGTH_SHORT
        actionText = ""
        actionTextColor = COLOR_DEFAULT
        bottomMargin = 0
    }

    /**
     * 设置消息。
     *
     * @param msg message.
     * @return [SnackbarUtils] instance
     */
    fun setMessage(msg: CharSequence): SnackbarUtils {
        message = msg
        return this
    }

    /**
     * 设置消息的颜色。
     *
     * @param color color
     * @return [SnackbarUtils] instance
     */
    fun setMessageColor(
        @ColorInt color: Int,
    ): SnackbarUtils {
        messageColor = color
        return this
    }

    /**
     * 设置背景颜色。
     *
     * @param color color
     * @return{@link SnackbarUtils} instance
     */
    fun setBgColor(
        @ColorInt color: Int,
    ): SnackbarUtils {
        bgColor = color
        return this
    }

    /**
     * 设置背景资源。
     *
     * @param bgResource 背景资源。
     * @return [SnackbarUtils] instance
     */
    fun setBgResource(
        @DrawableRes bgResource: Int,
    ): SnackbarUtils {
        this.bgResource = bgResource
        return this
    }

    /**
     * 设置持续时间。
     *
     * @param duration 持续时间
     *
     *   [SnackbarUtils.LENGTH_INDEFINITE]
     *   [SnackbarUtils.LENGTH_SHORT]
     *   [SnackbarUtils.LENGTH_LONG]
     *
     * @return [SnackbarUtils] instance
     */
    fun setDuration(
        @Duration duration: Int,
    ): SnackbarUtils {
        this.duration = duration
        return this
    }

    /**
     * 设置动作。
     *
     * @param text     text.
     * @param listener 单击侦听器。
     * @return [SnackbarUtils] instance
     */
    fun setAction(
        text: CharSequence,
        listener: View.OnClickListener,
    ): SnackbarUtils {
        return setAction(text, COLOR_DEFAULT, listener)
    }

    /**
     * 设置动作
     *
     * @param text     text.
     * @param color    文本的颜色。
     * @param listener 单击侦听器
     * @return [SnackbarUtils] instance
     */
    fun setAction(
        text: CharSequence,
        @ColorInt color: Int,
        listener: View.OnClickListener,
    ): SnackbarUtils {
        actionText = text
        actionTextColor = color
        actionListener = listener
        return this
    }

    /**
     * 设置底部边距。
     *
     * @param bottomMargin 底部边距的大小，以像素为单位。
     */
    fun setBottomMargin(
        @IntRange(from = 1) bottomMargin: Int,
    ): SnackbarUtils {
        this.bottomMargin = bottomMargin
        return this
    }

    /**
     * 显示snackbar。
     *
     * @param isShowTop 如果为True，则在顶部显示 snackbar，否则为false。
     */
    @JvmOverloads
    fun show(isShowTop: Boolean = false): Snackbar {
        var view: View = view
        if (isShowTop) {
            val suitableParent = findSuitableParentCopyFromSnackbar(view)
            var topSnackBarContainer = suitableParent!!.findViewWithTag<View>("topSnackBarCoordinatorLayout")
            if (topSnackBarContainer == null) {
                val topSnackBarCoordinatorLayout = CoordinatorLayout(view.context)
                topSnackBarCoordinatorLayout.tag = "topSnackBarCoordinatorLayout"
                topSnackBarCoordinatorLayout.rotation = 180f
                // bring to front
                topSnackBarCoordinatorLayout.elevation = 100f
                suitableParent.addView(
                    topSnackBarCoordinatorLayout,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                )
                topSnackBarContainer = topSnackBarCoordinatorLayout
            }
            view = topSnackBarContainer
        }
        sWeakSnackbar =
            if (messageColor != COLOR_DEFAULT) {
                val spannableString = SpannableString(message)
                val colorSpan = ForegroundColorSpan(messageColor)
                spannableString.setSpan(
                    colorSpan,
                    0,
                    spannableString.length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE,
                )
                WeakReference(Snackbar.make(view, spannableString, duration))
            } else {
                WeakReference(Snackbar.make(view, message!!, duration))
            }
        val snackbar = sWeakSnackbar!!.get()
        val snackbarView = snackbar!!.view as Snackbar.SnackbarLayout
        if (isShowTop) {
            for (i in 0 until snackbarView.childCount) {
                val child = snackbarView.getChildAt(i)
                child.rotation = 180f
            }
        }
        if (bgResource != -1) {
            snackbarView.setBackgroundResource(bgResource)
        } else if (bgColor != COLOR_DEFAULT) {
            snackbarView.setBackgroundColor(bgColor)
        }
        if (bottomMargin != 0) {
            val params = snackbarView.layoutParams as MarginLayoutParams
            params.bottomMargin = bottomMargin
        }
        if (actionText!!.isNotEmpty() && actionListener != null) {
            if (actionTextColor != COLOR_DEFAULT) {
                snackbar.setActionTextColor(actionTextColor)
            }
            snackbar.setAction(actionText, actionListener)
        }
        snackbar.show()
        return snackbar
    }

    /**
     * 以成功风格显示snackbar。
     *
     * @param isShowTop 如果为True，则在顶部显示snackbar，否则为false。
     */
    @JvmOverloads
    fun showSuccess(isShowTop: Boolean = false) {
        bgColor = COLOR_SUCCESS
        messageColor = COLOR_MESSAGE
        actionTextColor = COLOR_MESSAGE
        show(isShowTop)
    }

    /**
     * 显示带有警告样式的snackbar。
     *
     * @param isShowTop 如果为True，则在顶部显示snackbar，否则为false。
     */
    @JvmOverloads
    fun showWarning(isShowTop: Boolean = false) {
        bgColor = COLOR_WARNING
        messageColor = COLOR_MESSAGE
        actionTextColor = COLOR_MESSAGE
        show(isShowTop)
    }

    /**
     * 显示带有错误样式的snackbar。
     *
     * @param isShowTop 如果为True，则在顶部显示snackbar，否则为false。
     */
    @JvmOverloads
    fun showError(isShowTop: Boolean = false) {
        bgColor = COLOR_ERROR
        messageColor = COLOR_MESSAGE
        actionTextColor = COLOR_MESSAGE
        show(isShowTop)
    }

    companion object {
        const val LENGTH_INDEFINITE = -2
        const val LENGTH_SHORT = -1
        const val LENGTH_LONG = 0
        private const val COLOR_DEFAULT = -0x1000001
        private const val COLOR_SUCCESS = -0xd44a00
        private const val COLOR_WARNING = -0x3f00
        private const val COLOR_ERROR = -0x10000
        private const val COLOR_MESSAGE = -0x1
        private var sWeakSnackbar: WeakReference<Snackbar?>? = null

        /**
         * 设置要从中查找父级的视图。
         *
         * @param view 从中查找父级的视图。
         * @return [SnackbarUtils] instance
         */
        fun with(view: View): SnackbarUtils {
            return SnackbarUtils(view)
        }

        /**
         * Dismiss snackbar.
         */
        fun dismiss() {
            if (sWeakSnackbar != null && sWeakSnackbar!!.get() != null) {
                sWeakSnackbar!!.get()!!.dismiss()
                sWeakSnackbar = null
            }
        }

        /**
         * 返回snackbar的视图。
         *
         * @return view
         */
        fun getView(): View? {
            val snackBar = sWeakSnackbar!!.get() ?: return null
            return snackBar.view
        }

        /**
         * 将视图添加到snackbar。
         *
         * 在[（）][.show]
         *
         *之后调用它
         *
         * @param layoutId 布局的id。
         * @param params   params.
         */
        fun addView(
            @LayoutRes layoutId: Int,
            params: ViewGroup.LayoutParams,
        ) {
            val view = getView()
            if (view != null) {
                view.setPadding(0, 0, 0, 0)
                val layout = view as Snackbar.SnackbarLayout
                val child = LayoutInflater.from(view.getContext()).inflate(layoutId, null)
                layout.addView(child, -1, params)
            }
        }

        /**
         * 将视图添加到snackbar。
         *
         * 在[（）][.show]
         *
         *之后调用它
         *
         * @param child view
         * @param params  params.
         */
        fun addView(
            child: View,
            params: ViewGroup.LayoutParams,
        ) {
            val view = getView()
            if (view != null) {
                view.setPadding(0, 0, 0, 0)
                val layout = view as Snackbar.SnackbarLayout
                layout.addView(child, params)
            }
        }

        private fun findSuitableParentCopyFromSnackbar(v: View): ViewGroup? {
            var view: View? = v
            var fallback: ViewGroup? = null
            do {
                if (view is CoordinatorLayout) {
                    return view
                }
                if (view is FrameLayout) {
                    if (view.getId() == android.R.id.content) {
                        return view
                    }
                    fallback = view
                }
                if (view != null) {
                    val parent = view.parent
                    view = if (parent is View) parent else null
                }
            } while (view != null)
            return fallback
        }
    }
}
