package com.lzq.dawn.lifecycle

import android.os.Bundle
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.lzq.dawn.DawnBridge
import com.lzq.dawn.base.model.BaseRootRepository
import com.lzq.dawn.base.view.BaseRootActivity
import com.lzq.dawn.mvvm.v.BaseMvvmFragment
import com.lzq.dawn.mvvm.vm.BaseMvvmViewModel
import com.lzq.dawn.util.network.NetworkChangedReceiver
import com.lzq.dawn.util.network.NetworkType
import androidx.viewbinding.ViewBinding
import android.view.View
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

/**
 * @projectName com.lzq.dawn.lifecycle.DevicePageLifecycleTest
 * @author Lzq
 * @date : Created by Lzq on 2026/11/25 14:07:36
 * @version 0.0.30
 * @description: 在设备上验证页面重建、旋转及退出时的生命周期资源释放
 */
@RunWith(AndroidJUnit4::class)
class DevicePageLifecycleTest {
    @Test
    fun firstEntryBackStackViewRebuildRotationAndExitReleasePageReferences() {
        var firstActivity: DeviceHost? = null
        var firstFragment: DevicePage? = null
        ActivityScenario.launch(DeviceHost::class.java).use { scenario ->
            scenario.onActivity { activity ->
                firstActivity = activity
                val page = DevicePage()
                firstFragment = page
                activity.supportFragmentManager.beginTransaction().add(CONTAINER, page).commitNow()
                assertTrue(NetworkChangedReceiver.instance.isRegistered(activity))
                assertTrue(NetworkChangedReceiver.instance.isRegistered(page))
                val firstView = page.binding().root
                activity.supportFragmentManager.beginTransaction().replace(CONTAINER, Fragment())
                    .addToBackStack("page").commit()
                activity.supportFragmentManager.executePendingTransactions()
                assertFalse(NetworkChangedReceiver.instance.isRegistered(page))
                assertThrows(IllegalStateException::class.java) { page.binding() }
                activity.supportFragmentManager.popBackStackImmediate()
                assertNotSame(firstView, page.binding().root)
                assertTrue(NetworkChangedReceiver.instance.isRegistered(page))
            }
            scenario.recreate()
            scenario.onActivity { activity ->
                assertNotSame(firstActivity, activity)
                assertFalse(NetworkChangedReceiver.instance.isRegistered(firstActivity))
                assertFalse(NetworkChangedReceiver.instance.isRegistered(firstFragment))
                assertTrue(NetworkChangedReceiver.instance.isRegistered(activity))
            }
        }
        assertFalse(NetworkChangedReceiver.instance.isRegistered(firstActivity))
        assertFalse(NetworkChangedReceiver.instance.isRegistered(firstFragment))
    }

    /**
     * @projectName com.lzq.dawn.lifecycle.DevicePageLifecycleTest.DeviceHost
     * @author Lzq
     * @date : Created by Lzq on 2026/11/25 14:07:36
     * @version 0.0.30
     * @description: 为设备生命周期测试提供页面容器与应用初始化
     */
    class DeviceHost : BaseRootActivity() {
        override fun onCreate(savedInstanceState: Bundle?) {
            setTheme(androidx.appcompat.R.style.Theme_AppCompat)
            DawnBridge.init(application)
            super.onCreate(savedInstanceState)
        }
        override fun getLayoutId() = 0
        override fun initView() { setContentView(FrameLayout(this).also { it.id = CONTAINER }) }
        override fun initData() = Unit
        override fun initRequest() = Unit
        override fun initObserver() = Unit
    }

    /**
     * @projectName com.lzq.dawn.lifecycle.DevicePageLifecycleTest.DeviceRepository
     * @author Lzq
     * @date : Created by Lzq on 2026/11/25 14:07:36
     * @version 0.0.30
     * @description: 为设备生命周期测试提供仓库类型
     */
    class DeviceRepository : BaseRootRepository()
    /**
     * @projectName com.lzq.dawn.lifecycle.DevicePageLifecycleTest.DeviceModel
     * @author Lzq
     * @date : Created by Lzq on 2026/11/25 14:07:36
     * @version 0.0.30
     * @description: 为设备页面创建并持有测试仓库
     */
    class DeviceModel : BaseMvvmViewModel<DeviceRepository>() {
        override fun createModel() = DeviceRepository()
    }
    /**
     * @projectName com.lzq.dawn.lifecycle.DevicePageLifecycleTest.DeviceBinding
     * @author Lzq
     * @date : Created by Lzq on 2026/11/25 14:07:36
     * @version 0.0.30
     * @description: 为设备测试页面提供视图绑定入口
     */
    class DeviceBinding(private val view: View) : ViewBinding {
        override fun getRoot() = view
    }
    /**
     * @projectName com.lzq.dawn.lifecycle.DevicePageLifecycleTest.DevicePage
     * @author Lzq
     * @date : Created by Lzq on 2026/11/25 14:07:36
     * @version 0.0.30
     * @description: 暴露设备测试页面的绑定以验证视图重建与释放
     */
    class DevicePage : BaseMvvmFragment<DeviceBinding, DeviceModel>() {
        override fun createBinding() = DeviceBinding(FrameLayout(requireContext()))
        override fun createViewModel() = DeviceModel()
        fun binding() = mViewBinding
        override fun initView() = Unit
        override fun initData() = Unit
        override fun initRequest() = Unit
        override fun initObserver() = Unit
        override fun getNetConnected(): Boolean? = null
        override fun onConnected(networkType: NetworkType?) = Unit
        override fun onDisconnected() = Unit
    }

    /**
     * @projectName com.lzq.dawn.lifecycle.DevicePageLifecycleTest.Companion
     * @author Lzq
     * @date : Created by Lzq on 2026/11/25 14:07:36
     * @version 0.0.30
     * @description: 保存设备生命周期测试页面共用的容器标识
     */
    companion object { private const val CONTAINER = 234567 }
}

