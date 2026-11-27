package com.lzq.dawn.util.size

import android.app.Application
import android.content.res.Resources
import android.util.TypedValue
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

/** 验证字体单位转换在最低支持版本和新系统上保留舍入与边界约定。 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [26, 34, 37], qualifiers = "mdpi", application = Application::class, manifest = Config.NONE)
class SizeUnitConversionCompatibilityTest {
    @Before
    fun resetFontScale() {
        RuntimeEnvironment.setFontScale(1f)
    }

    @Test
    fun defaultFontScaleKeepsFractionalValuesAndIntegerRounding() {
        assertEquals(12.25f, SizeUtils.applyDimension(12.25f, TypedValue.COMPLEX_UNIT_SP), 0.001f)
        assertEquals(13, SizeUtils.sp2px(12.6f))
        assertEquals(13, SizeUtils.px2sp(12.6f))
    }

    @Test
    fun zeroAndNegativeDimensionsKeepTheExistingRounding() {
        assertEquals(0, SizeUtils.sp2px(0f))
        assertEquals(0, SizeUtils.px2sp(0f))
        assertEquals(-1, SizeUtils.sp2px(-1.8f))
        assertEquals(-1, SizeUtils.px2sp(-1.8f))
    }

    @Test
    fun unsupportedUnitKeepsTheExistingZeroResult() {
        assertEquals(0f, SizeUtils.applyDimension(20f, Int.MAX_VALUE), 0f)
    }

    @Test
    fun enlargedFontsUseSystemConversionAndRoundTripInBothDirections() {
        RuntimeEnvironment.setFontScale(2f)
        val metrics = Resources.getSystem().displayMetrics

        for (fontSize in listOf(12f, 48f)) {
            val expectedPixels = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, fontSize, metrics)

            assertEquals(expectedPixels, SizeUtils.applyDimension(fontSize, TypedValue.COMPLEX_UNIT_SP), 0.001f)
            assertEquals((expectedPixels + 0.5f).toInt(), SizeUtils.sp2px(fontSize))
            assertEquals(fontSize.toInt(), SizeUtils.px2sp(expectedPixels))
        }
    }
}
