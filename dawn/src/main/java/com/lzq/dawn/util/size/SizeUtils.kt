package com.lzq.dawn.util.size

import android.content.res.Resources
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup

/**
 * @Name :SizeUtils
 * @Time :2022/8/30 11:31
 * @Author :  Lzq
 * @Desc : size
 */
object SizeUtils {
    /**
     * dp to  px.
     *
     * @param dpValue dp的值。
     * @return value of px
     */
    @JvmStatic
    fun dp2px(dpValue: Float): Int {
        val scale = Resources.getSystem().displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

    /**
     * px to  dp.
     *
     * @param pxValue px的值
     * @return value of dp
     */
    @JvmStatic
    fun px2dp(pxValue: Float): Int {
        val scale = Resources.getSystem().displayMetrics.density
        return (pxValue / scale + 0.5f).toInt()
    }

    /**
     * sp to   px.
     *
     * @param spValue sp的值
     * @return value of px
     */
    @JvmStatic
    fun sp2px(spValue: Float): Int {
        val fontScale = Resources.getSystem().displayMetrics.scaledDensity
        return (spValue * fontScale + 0.5f).toInt()
    }

    /**
     * px to  sp.
     *
     * @param pxValue px的值
     * @return value of sp
     */
    @JvmStatic
    fun px2sp(pxValue: Float): Int {
        val fontScale = Resources.getSystem().displayMetrics.scaledDensity
        return (pxValue / fontScale + 0.5f).toInt()
    }

    /**
     * 将包含维度的未打包复杂数据值转换为其最终浮点值。
     * The two parameters <var>unit</var> and <var>value</var>
     * are as in [TypedValue.TYPE_DIMENSION].
     *
     * @param value 要将单位应用到的值。
     * @param unit  要从中转换的单位。
     * @return 复浮点值乘以取决于其单位的适当度量。
     */
    fun applyDimension(
        value: Float,
        unit: Int,
    ): Float {
        val metrics = Resources.getSystem().displayMetrics
        return when (unit) {
            TypedValue.COMPLEX_UNIT_PX -> value
            TypedValue.COMPLEX_UNIT_DIP -> value * metrics.density
            TypedValue.COMPLEX_UNIT_SP -> value * metrics.scaledDensity
            TypedValue.COMPLEX_UNIT_PT -> value * metrics.xdpi * (1.0f / 72)
            TypedValue.COMPLEX_UNIT_IN -> value * metrics.xdpi
            TypedValue.COMPLEX_UNIT_MM -> value * metrics.xdpi * (1.0f / 25.4f)
            else -> 0F
        }
    }

    /**
     * 强制获取视图的大小。
     *
     * e.g.
     * <pre>
     * SizeUtils.forceGetViewSize(view, new SizeUtils.OnGetSizeListener() {
     * Override
     * public void onGetSize(final View view) {
     * view.getWidth();
     * }
     * });
     </pre> *
     *
     * @param view     view.
     * @param listener 获取大小侦听器。
     */
    fun forceGetViewSize(
        view: View,
        listener: OnGetSizeListener?,
    ) {
        view.post { listener?.onGetSize(view) }
    }

    /**
     * 返回视图的宽度。
     *
     * @param view  view.
     * @return 视图的宽度。
     */
    fun getMeasuredWidth(view: View): Int {
        return measureView(view)[0]
    }

    /**
     * 返回视图的高度。
     *
     * @param view  view.
     * @return 视图的高度。
     */
    fun getMeasuredHeight(view: View): Int {
        return measureView(view)[1]
    }

    /**
     * 测量视图。
     *
     * @param view  view.
     * @return arr[0]: view's width, arr[1]: view's height
     */
    fun measureView(view: View): IntArray {
        var lp = view.layoutParams
        if (lp == null) {
            lp =
                ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                )
        }
        val widthSpec = ViewGroup.getChildMeasureSpec(0, 0, lp.width)
        val lpHeight = lp.height
        val heightSpec: Int =
            if (lpHeight > 0) {
                View.MeasureSpec.makeMeasureSpec(lpHeight, View.MeasureSpec.EXACTLY)
            } else {
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            }
        view.measure(widthSpec, heightSpec)
        return intArrayOf(view.measuredWidth, view.measuredHeight)
    }

    interface OnGetSizeListener {
        fun onGetSize(view: View?)
    }
}
