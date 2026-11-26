package com.lzq.dawn.lifecycle

import android.app.Application
import android.content.res.Configuration
import android.os.Looper
import android.view.View
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.viewbinding.ViewBinding
import com.lzq.dawn.DawnBridge
import com.lzq.dawn.base.model.BaseRootRepository
import com.lzq.dawn.base.state.BaseViewState
import com.lzq.dawn.base.view.BaseRootActivity
import com.lzq.dawn.mvi.view.i.BaseMviIntent
import com.lzq.dawn.mvi.view.i.IMviViewStateFlowResult
import com.lzq.dawn.mvi.view.v.BaseMviFragment
import com.lzq.dawn.mvi.view.v.IBaseMviViewModel
import com.lzq.dawn.mvvm.v.BaseMvvmFragment
import com.lzq.dawn.mvvm.vm.BaseMvvmViewModel
import com.lzq.dawn.util.network.NetworkChangedReceiver
import com.lzq.dawn.util.network.NetworkType
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.android.controller.ActivityController
import org.robolectric.annotation.Config

/**
 * @projectName com.lzq.dawn.lifecycle.PageLifecycleTest
 * @author Lzq
 * @date : Created by Lzq on 2026/11/25 13:57:22
 * @version 0.0.30
 * @description: 验证页面初始化顺序、视图重建及生命周期资源释放
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28], application = Application::class, manifest = Config.NONE)
class PageLifecycleTest {
    private val controllers = mutableListOf<ActivityController<out FragmentActivity>>()

    @Before
    fun initializeApplication() {
        DawnBridge.init(org.robolectric.RuntimeEnvironment.getApplication())
    }

    @After
    fun destroyHosts() {
        controllers.forEach { it.pause().stop().destroy() }
        shadowOf(Looper.getMainLooper()).idle()
    }

    @Test
    fun mvvmFirstEntryObservesOnlyAfterViewExistsAndReleasesOnExit() {
        val host = host()
        val fragment = MvvmPage()

        host.supportFragmentManager.beginTransaction().add(CONTAINER, fragment).commitNow()
        assertTrue(fragment.observerInstalledBeforeInitialization)
        fragment.vm.getViewState().value = BaseViewState.Loading
        assertEquals(1, fragment.loads)
        assertTrue(fragment.vm.getViewState().hasActiveObservers())
        assertTrue(NetworkChangedReceiver.instance.isRegistered(fragment))

        host.supportFragmentManager.beginTransaction().remove(fragment).commitNow()
        shadowOf(Looper.getMainLooper()).idle()
        assertFalse(fragment.vm.getViewState().hasObservers())
        assertFalse(NetworkChangedReceiver.instance.isRegistered(fragment))
        assertThrows(IllegalStateException::class.java) { fragment.binding() }
        fragment.vm.getViewState().value = BaseViewState.Loading
        assertEquals(1, fragment.loads)
    }

    @Test
    fun retainedFragmentGetsNewBindingAndOneObserverAfterViewRecreation() {
        val host = host()
        val fragment = MvvmPage()
        host.supportFragmentManager.beginTransaction().add(CONTAINER, fragment).commitNow()
        val first = fragment.binding().root

        host.supportFragmentManager.beginTransaction().detach(fragment).commitNow()
        assertFalse(fragment.vm.getViewState().hasObservers())
        assertFalse(NetworkChangedReceiver.instance.isRegistered(fragment))
        assertThrows(IllegalStateException::class.java) { fragment.binding() }
        host.supportFragmentManager.beginTransaction().attach(fragment).commitNow()
        val second = fragment.binding().root
        assertNotSame(first, second)
        fragment.vm.getViewState().value = BaseViewState.Loading
        assertEquals(1, fragment.loads)
        assertTrue(NetworkChangedReceiver.instance.isRegistered(fragment))
    }

    @Test
    fun backStackRecreatesViewAndReleasesOldOwner() {
        val host = host()
        val fragment = MvvmPage()
        host.supportFragmentManager.beginTransaction().add(CONTAINER, fragment).commitNow()
        val first = fragment.binding().root

        host.supportFragmentManager.beginTransaction().replace(CONTAINER, Fragment())
            .addToBackStack("page").commit()
        host.supportFragmentManager.executePendingTransactions()
        assertFalse(NetworkChangedReceiver.instance.isRegistered(fragment))
        assertFalse(fragment.vm.getViewState().hasObservers())
        assertThrows(IllegalStateException::class.java) { fragment.binding() }
        host.supportFragmentManager.popBackStackImmediate()

        assertNotSame(first, fragment.binding().root)
        fragment.vm.getViewState().value = BaseViewState.Loading
        assertEquals(1, fragment.loads)
    }

    @Test
    fun rotationReleasesOldActivityAndFragmentListeners() {
        val controller = Robolectric.buildActivity(RootPage::class.java).setup()
        val previous = controller.get()
        val fragment = MvvmPage()
        previous.supportFragmentManager.beginTransaction().add(CONTAINER, fragment).commitNow()

        controller.configurationChange(Configuration(previous.resources.configuration).apply {
            orientation = Configuration.ORIENTATION_LANDSCAPE
        })
        shadowOf(Looper.getMainLooper()).idle()
        assertFalse(NetworkChangedReceiver.instance.isRegistered(previous))
        assertFalse(NetworkChangedReceiver.instance.isRegistered(fragment))
        assertTrue(NetworkChangedReceiver.instance.isRegistered(controller.get()))
        controller.pause().stop().destroy()
        assertFalse(NetworkChangedReceiver.instance.isRegistered(controller.get()))
    }

    @Test
    fun destroyedActivityReleasesLoadingWindow() {
        val controller = Robolectric.buildActivity(RootPage::class.java).setup()
        val activity = controller.get()
        activity.showLoading("测试加载")
        val dialog = org.robolectric.shadows.ShadowDialog.getLatestDialog()
        assertTrue(dialog.isShowing)

        controller.pause().stop().destroy()

        assertFalse(dialog.isShowing)
        assertFalse(NetworkChangedReceiver.instance.isRegistered(activity))
    }

    @Test
    fun mviRecreatedViewHasOneCollectorAndDoesNotReplayEvent() {
        val host = host()
        val fragment = MviPage()
        host.supportFragmentManager.beginTransaction().add(CONTAINER, fragment).commitNow()
        shadowOf(Looper.getMainLooper()).idle()
        assertEquals(1, fragment.vm.subscribers)
        fragment.vm.emit(BaseMviIntent())
        shadowOf(Looper.getMainLooper()).idle()
        assertEquals(1, fragment.outputs)

        host.supportFragmentManager.beginTransaction().detach(fragment).commitNow()
        shadowOf(Looper.getMainLooper()).idle()
        assertEquals(0, fragment.vm.subscribers)
        assertThrows(IllegalStateException::class.java) { fragment.binding() }
        fragment.vm.emit(BaseMviIntent())
        host.supportFragmentManager.beginTransaction().attach(fragment).commitNow()
        shadowOf(Looper.getMainLooper()).idle()
        assertEquals(1, fragment.vm.subscribers)
        assertEquals(1, fragment.outputs)
        fragment.onOutput()
        shadowOf(Looper.getMainLooper()).idle()
        assertEquals(1, fragment.vm.subscribers)
        fragment.vm.emit(BaseMviIntent())
        shadowOf(Looper.getMainLooper()).idle()
        assertEquals(2, fragment.outputs)
    }

    @Test
    fun mviViewInitializationAlreadyHasDefaultOutputSubscriber() {
        val host = host()
        val fragment = MviPage().also { it.emitDuringInitialization = true }

        host.supportFragmentManager.beginTransaction().add(CONTAINER, fragment).commitNow()
        shadowOf(Looper.getMainLooper()).idle()

        assertTrue(fragment.subscribedDuringInitialization)
        assertEquals(1, fragment.outputs)
    }

    @Test
    fun startedCollectorPausesAndResumesWithoutReplayingStoppedEvents() {
        val controller = Robolectric.buildActivity(RootPage::class.java).setup()
        controllers += controller
        val fragment = MviPage()
        controller.get().supportFragmentManager.beginTransaction().add(CONTAINER, fragment).commitNow()
        fragment.onOutput(Lifecycle.State.STARTED)
        shadowOf(Looper.getMainLooper()).idle()
        assertEquals(1, fragment.vm.subscribers)

        controller.pause().stop()
        shadowOf(Looper.getMainLooper()).idle()
        assertEquals(0, fragment.vm.subscribers)
        fragment.vm.emit(BaseMviIntent())
        controller.restart().start().resume()
        shadowOf(Looper.getMainLooper()).idle()
        assertEquals(1, fragment.vm.subscribers)
        assertEquals(0, fragment.outputs)
        fragment.vm.emit(BaseMviIntent())
        shadowOf(Looper.getMainLooper()).idle()
        assertEquals(1, fragment.outputs)
    }

    private fun host(): RootPage {
        val controller = Robolectric.buildActivity(RootPage::class.java).setup()
        controllers += controller
        return controller.get()
    }

    /**
     * @projectName com.lzq.dawn.lifecycle.PageLifecycleTest.RootPage
     * @author Lzq
     * @date : Created by Lzq on 2026/11/25 13:57:22
     * @version 0.0.30
     * @description: 记录根页面初始化顺序并提供测试视图容器
     */
    class RootPage : BaseRootActivity() {
        override fun onCreate(savedInstanceState: android.os.Bundle?) {
            setTheme(androidx.appcompat.R.style.Theme_AppCompat)
            super.onCreate(savedInstanceState)
        }
        override fun getLayoutId() = 0
        override fun initView() { setContentView(FrameLayout(this).also { it.id = CONTAINER }) }
        override fun initData() = Unit
        override fun initRequest() = Unit
        override fun initObserver() = Unit
    }

    /**
     * @projectName com.lzq.dawn.lifecycle.PageLifecycleTest.TestBinding
     * @author Lzq
     * @date : Created by Lzq on 2026/11/25 13:57:22
     * @version 0.0.30
     * @description: 为页面生命周期测试提供最小视图绑定
     */
    class TestBinding(private val view: View) : ViewBinding {
        override fun getRoot(): View = view
    }

    /**
     * @projectName com.lzq.dawn.lifecycle.PageLifecycleTest.TestRepository
     * @author Lzq
     * @date : Created by Lzq on 2026/11/25 13:57:22
     * @version 0.0.30
     * @description: 为页面生命周期测试提供仓库类型
     */
    class TestRepository : BaseRootRepository()
    /**
     * @projectName com.lzq.dawn.lifecycle.PageLifecycleTest.TestMvvmModel
     * @author Lzq
     * @date : Created by Lzq on 2026/11/25 13:57:22
     * @version 0.0.30
     * @description: 为视图绑定生命周期测试创建仓库
     */
    class TestMvvmModel : BaseMvvmViewModel<TestRepository>() {
        override fun createModel() = TestRepository()
    }

    /**
     * @projectName com.lzq.dawn.lifecycle.PageLifecycleTest.MvvmPage
     * @author Lzq
     * @date : Created by Lzq on 2026/11/25 13:57:22
     * @version 0.0.30
     * @description: 暴露视图绑定和模型以验证页面视图生命周期
     */
    class MvvmPage : BaseMvvmFragment<TestBinding, TestMvvmModel>() {
        val vm = TestMvvmModel()
        var loads = 0
        var observerInstalledBeforeInitialization = false
        override fun createBinding() = TestBinding(FrameLayout(requireContext()))
        override fun createViewModel() = vm
        fun binding() = mViewBinding
        override fun initView() { observerInstalledBeforeInitialization = vm.getViewState().hasObservers() }
        override fun initData() = Unit
        override fun initRequest() = Unit
        override fun initObserver() = Unit
        override fun getNetConnected(): Boolean? = null
        override fun onConnected(networkType: NetworkType?) = Unit
        override fun onDisconnected() = Unit
        override fun showLoading(content: String?) { loads++ }
        override fun hideLoading() = Unit
    }

    /**
     * @projectName com.lzq.dawn.lifecycle.PageLifecycleTest.EventModel
     * @author Lzq
     * @date : Created by Lzq on 2026/11/25 13:57:22
     * @version 0.0.30
     * @description: 模拟意图输出以验证页面订阅的生命周期边界
     */
    class EventModel : IBaseMviViewModel<BaseMviIntent, TestRepository> {
        private val events = MutableSharedFlow<BaseMviIntent>(extraBufferCapacity = 8)
        var subscribers = 0
        fun emit(intent: BaseMviIntent) { events.tryEmit(intent) }
        override fun inputIntent(intent: BaseMviIntent) = emit(intent)
        override fun createModel() = TestRepository()
        override suspend fun outputIntent(result: IMviViewStateFlowResult<BaseMviIntent>) {
            subscribers++
            try { events.collect { result.onResult(it) } } finally { subscribers-- }
        }
    }

    /**
     * @projectName com.lzq.dawn.lifecycle.PageLifecycleTest.MviPage
     * @author Lzq
     * @date : Created by Lzq on 2026/11/25 13:57:22
     * @version 0.0.30
     * @description: 统计意图处理结果以验证页面订阅与重建行为
     */
    class MviPage : BaseMviFragment<TestRepository, EventModel, TestBinding, BaseMviIntent>() {
        val vm = EventModel()
        var outputs = 0
        var emitDuringInitialization = false
        var subscribedDuringInitialization = false
        override fun createBinding() = TestBinding(FrameLayout(requireContext()))
        override fun createViewModel() = vm
        fun binding() = mViewBinding
        override fun outputViewIntent(intent: BaseMviIntent) { outputs++ }
        override fun initView() {
            subscribedDuringInitialization = vm.subscribers == 1
            if (emitDuringInitialization) vm.emit(BaseMviIntent())
        }
        override fun initData() = Unit
        override fun initRequest() = Unit
        override fun initObserver() = Unit
        override fun getNetConnected(): Boolean? = null
        override fun onConnected(networkType: NetworkType?) = Unit
        override fun onDisconnected() = Unit
        override fun showLoading(content: String?) = Unit
        override fun hideLoading() = Unit
    }

    /**
     * @projectName com.lzq.dawn.lifecycle.PageLifecycleTest.Companion
     * @author Lzq
     * @date : Created by Lzq on 2026/11/25 13:57:22
     * @version 0.0.30
     * @description: 保存页面生命周期测试共用的容器标识
     */
    companion object { private const val CONTAINER = 123456 }
}

