package com.lzq.dawn.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import com.lzq.dawn.R
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertArrayEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Robolectric
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

/** 验证加载动画中的装饰阴影不产生无意义的无障碍播报。 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [26, 34])
class LoadingViewResourcesTest {
    @Test fun legacyConstructorsAndStyleableRemainAvailable() {
        val context = RuntimeEnvironment.getApplication()
        val signatures = listOf(
            arrayOf(Context::class.java) to arrayOf<Any?>(context),
            arrayOf(Context::class.java, AttributeSet::class.java) to arrayOf<Any?>(context, null),
            arrayOf(Context::class.java, AttributeSet::class.java, Int::class.javaPrimitiveType!!) to
                arrayOf<Any?>(context, null, 0),
            arrayOf(Context::class.java, AttributeSet::class.java, Int::class.javaPrimitiveType!!, Int::class.javaPrimitiveType!!) to
                arrayOf<Any?>(context, null, 0, 0),
        )

        for ((signature, arguments) in signatures) {
            val view = LoadingView::class.java.getConstructor(*signature).newInstance(*arguments)
            assertEquals(80, view.delay)
            view.loadingText = "加载中"
            assertEquals("加载中", view.loadingText.toString())
        }
        assertArrayEquals(R.styleable.dawn_LoadingView, R.styleable.DawnLoadingView)
    }

    @Test fun legacyLayoutTagReadsTextAppearanceAndDelayFromTheirOwnAttributes() {
        val context = RuntimeEnvironment.getApplication()
        val attributes = Robolectric.buildAttributeSet()
            .addAttribute(R.attr.dawn_loadingText, "加载中")
            .addAttribute(R.attr.dawn_delay, "125")
            .addAttribute(R.attr.dawn_loadingTextAppearance, "@android:style/TextAppearance.Large")
            .build()
        val expectedText = TextView(context).apply { setTextAppearance(android.R.style.TextAppearance_Large) }

        val view = LayoutInflater.from(context)
            .createView(LoadingView::class.java.name, null, attributes) as LoadingView

        assertEquals(125, view.delay)
        assertEquals("加载中", view.loadingText.toString())
        assertEquals(expectedText.textSize, view.findViewById<TextView>(R.id.dawn_tv_prompt).textSize, 0.01f)
    }

    @Test fun decorativeShadowIsExcludedFromAccessibility() {
        val context = RuntimeEnvironment.getApplication()
        val parent = FrameLayout(context)

        LayoutInflater.from(context).inflate(R.layout.dawn_load_view, parent, true)
        val shadow = parent.findViewById<View>(R.id.dawn_iv_indication)

        assertNull(shadow.contentDescription)
        assertEquals(View.IMPORTANT_FOR_ACCESSIBILITY_NO, shadow.importantForAccessibility)
        assertEquals(3, parent.childCount)
    }
}
