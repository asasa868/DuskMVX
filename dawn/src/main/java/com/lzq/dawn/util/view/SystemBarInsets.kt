package com.lzq.dawn.util.view

import android.os.Build
import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

/** 给尚未自行处理安全区域的页面根视图应用系统栏和刘海区域。 */
object SystemBarInsets {
    /**
     * 仅为 Android 15 及以上、targetSdk 35 及以上的页面安装监听，返回是否已安装。
     *
     * 在设置初始 padding 后调用一次。使用父视图分发后剩余的 insets，
     * 保留初始 padding，后续分发不会累计；不会修改旧系统或旧 target 的已有监听。
     */
    @JvmStatic
    fun applyTo(view: View): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.VANILLA_ICE_CREAM ||
            view.context.applicationInfo.targetSdkVersion < Build.VERSION_CODES.VANILLA_ICE_CREAM
        ) {
            return false
        }
        val initialLeft = view.paddingLeft
        val initialTop = view.paddingTop
        val initialRight = view.paddingRight
        val initialBottom = view.paddingBottom
        ViewCompat.setOnApplyWindowInsetsListener(view) { root, insets ->
            val safeArea = insets.getInsets(
                WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout()
            )
            root.setPadding(
                initialLeft + safeArea.left,
                initialTop + safeArea.top,
                initialRight + safeArea.right,
                initialBottom + safeArea.bottom
            )
            insets
        }
        ViewCompat.requestApplyInsets(view)
        return true
    }
}
