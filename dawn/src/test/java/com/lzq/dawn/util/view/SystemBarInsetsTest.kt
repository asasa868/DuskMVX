package com.lzq.dawn.util.view

import android.app.Application
import android.view.View
import android.widget.FrameLayout
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35, 37], application = Application::class, manifest = Config.NONE)
class SystemBarInsetsTest {
    private val application get() = RuntimeEnvironment.getApplication()
    private var originalTarget = 0

    @Before
    fun useTargetWithEnforcedEdgeToEdge() {
        originalTarget = application.applicationInfo.targetSdkVersion
        application.applicationInfo.targetSdkVersion = 37
    }

    @After
    fun restoreTarget() {
        application.applicationInfo.targetSdkVersion = originalTarget
    }

    @Test
    fun target35AddsReceivedSystemBarsToInitialPadding() {
        application.applicationInfo.targetSdkVersion = 35
        val root = paddedView()

        assertTrue(SystemBarInsets.applyTo(root))
        val insets = bars(top = 24, bottom = 18, left = 3, right = 5)
        val returned = ViewCompat.dispatchApplyWindowInsets(root, insets)

        assertPadding(root, 5, 28, 11, 26)
        assertEquals(
            insets.getInsets(WindowInsetsCompat.Type.systemBars()),
            returned.getInsets(WindowInsetsCompat.Type.systemBars())
        )
    }

    @Test
    fun repeatedOrChangedInsetsNeverAccumulatePadding() {
        val root = paddedView()
        SystemBarInsets.applyTo(root)

        repeat(3) { ViewCompat.dispatchApplyWindowInsets(root, bars(top = 24, bottom = 18)) }
        assertPadding(root, 2, 28, 6, 26)

        ViewCompat.dispatchApplyWindowInsets(root, bars(top = 10, bottom = 30))
        assertPadding(root, 2, 14, 6, 38)

        ViewCompat.dispatchApplyWindowInsets(root, bars(top = 0, bottom = 0))
        assertPadding(root, 2, 4, 6, 8)
    }

    @Test
    fun displayCutoutAndSystemBarsUseTheLargestInsetOnEachEdge() {
        val root = paddedView()
        SystemBarInsets.applyTo(root)
        val insets = WindowInsetsCompat.Builder(bars(top = 24, bottom = 18))
            .setInsets(WindowInsetsCompat.Type.displayCutout(), Insets.of(40, 12, 0, 0))
            .build()

        ViewCompat.dispatchApplyWindowInsets(root, insets)

        assertPadding(root, 42, 28, 6, 26)
    }

    @Test
    fun oldTargetKeepsPaddingAndThePreviouslyInstalledListener() {
        application.applicationInfo.targetSdkVersion = 34
        val root = paddedView()
        var previousListenerCalled = false
        ViewCompat.setOnApplyWindowInsetsListener(root) { _, insets ->
            previousListenerCalled = true
            insets
        }

        assertFalse(SystemBarInsets.applyTo(root))
        ViewCompat.dispatchApplyWindowInsets(root, bars(top = 24, bottom = 18))

        assertTrue(previousListenerCalled)
        assertPadding(root, 2, 4, 6, 8)
    }

    @Test
    @Config(sdk = [26, 34])
    fun oldAndroidWithTarget37KeepsPaddingAndThePreviouslyInstalledListener() {
        val root = paddedView()
        var previousListenerCalled = false
        ViewCompat.setOnApplyWindowInsetsListener(root) { _, insets ->
            previousListenerCalled = true
            insets
        }

        assertFalse(SystemBarInsets.applyTo(root))
        ViewCompat.dispatchApplyWindowInsets(root, bars(top = 24, bottom = 18))

        assertTrue(previousListenerCalled)
        assertPadding(root, 2, 4, 6, 8)
    }

    @Test
    fun parentConsumedStatusBarDoesNotBecomeExtraTopPadding() {
        val parent = FrameLayout(application)
        val root = paddedView()
        parent.addView(root)
        ViewCompat.setOnApplyWindowInsetsListener(parent) { _, insets ->
            WindowInsetsCompat.Builder(insets)
                .setInsets(WindowInsetsCompat.Type.statusBars(), Insets.NONE)
                .build()
        }
        SystemBarInsets.applyTo(root)

        ViewCompat.dispatchApplyWindowInsets(parent, bars(top = 24, bottom = 18))

        assertPadding(root, 2, 4, 6, 26)
    }

    private fun paddedView() = View(application).apply { setPadding(2, 4, 6, 8) }

    private fun bars(top: Int, bottom: Int, left: Int = 0, right: Int = 0): WindowInsetsCompat =
        WindowInsetsCompat.Builder()
            .setInsets(WindowInsetsCompat.Type.statusBars(), Insets.of(0, top, 0, 0))
            .setInsets(WindowInsetsCompat.Type.navigationBars(), Insets.of(left, 0, right, bottom))
            .build()

    private fun assertPadding(view: View, left: Int, top: Int, right: Int, bottom: Int) {
        assertEquals(left, view.paddingLeft)
        assertEquals(top, view.paddingTop)
        assertEquals(right, view.paddingRight)
        assertEquals(bottom, view.paddingBottom)
    }
}
