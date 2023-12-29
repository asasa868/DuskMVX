package com.lzq.dawn.util.permission

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Build
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.lzq.dawn.util.permission.callback.RequestCallback
import com.lzq.dawn.util.permission.request.PermissionFragment
import com.lzq.dawn.util.permission.task.WriterTask
import com.lzq.dawn.util.permission.task.BackgroundLocationTask
import com.lzq.dawn.util.permission.task.ExternalStorageTask
import com.lzq.dawn.util.permission.task.InstallTask
import com.lzq.dawn.util.permission.task.NormalTask
import com.lzq.dawn.util.permission.task.ChainTask
import com.lzq.dawn.util.permission.task.RequestChain
import com.lzq.dawn.util.permission.task.SystemWindowTask
import java.util.LinkedHashSet


/**
 * className :PermissionBuilder
 * createTime :2022/5/16 15:41
 * @Author :  Lzq
 *
 * 这里使用了lateinit
 * 它的作用：延迟初始化
 *  这里使用了 as
 * 它的作用：类似java的类型转换
 */
class PermissionBuilder(
    fragmentActivity: FragmentActivity?,
    fragment: Fragment?,
    normalPermissions: MutableSet<String>,
    specialPermissions: MutableSet<String>
) {

    /**
     * 请求权限的activity
     */
    lateinit var activity: FragmentActivity

    /**
     * 请求权限的fragment
     */
    private var fragment: Fragment? = null

    /**
     * 普通请求权限
     */
    @JvmField
    var normalPermissions: MutableSet<String>

    /**
     * 特殊请求权限
     */
    @JvmField
    var specialPermissions: MutableSet<String>

    /**
     * 屏幕方向,请求权限是需要锁住，避免发生一些错误
     */
    private var originRequestOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED

    /**
     * 获取fragmentManager
     */
    private val fragmentManager: FragmentManager
        get() = fragment?.childFragmentManager ?: activity.supportFragmentManager

    /**
     * 获取请求权限的fragment
     */
    private val requestFragment: PermissionFragment
        get() {
            val permissionFragment = fragmentManager.findFragmentByTag(FRAGMENT_TAG)
            return if (permissionFragment != null) {
                permissionFragment as PermissionFragment
            } else {
                val newFragment = PermissionFragment()
                fragmentManager
                    .beginTransaction()
                    .add(newFragment, FRAGMENT_TAG)
                    .commitNowAllowingStateLoss()
                newFragment
            }
        }

    /**
     * 将一些当前版本不需要请求的权限放在这里
     */
    @JvmField
    var permissionsWontRequest: MutableSet<String> = LinkedHashSet()

    /**
     * 请求成功的权限
     */
    @JvmField
    var grantedPermissions: MutableSet<String> = LinkedHashSet()

    /**
     * 请求失败的权限
     */
    @JvmField
    var deniedPermissions: MutableSet<String> = LinkedHashSet()

    /**
     * 请求永久失败的权限
     */
    @JvmField
    var permanentDeniedPermissions: MutableSet<String> = LinkedHashSet()

    /**
     * 需要去设置页面的权限
     */
    @JvmField
    var toSettingPermissions: MutableSet<String> = LinkedHashSet()

    /**
     * 请求结果
     */
    var result: RequestCallback? = null

    /**
     * 获取当前app的targetVersion
     */
    val targetSdkVersion: Int
        get() = activity.applicationInfo.targetSdkVersion


    fun request(callback: RequestCallback?) {
        this.result = callback
        startRequest()
    }

    /**
     * 请求普通权限
     * @param permissions 普通权限
     * @param chainTask 任务
     */
    fun requestNow(permissions: Set<String>, chainTask: ChainTask) {
        requestFragment.requestNormal(this, chainTask, permissions)
    }

    /**
     * 请求后台获取位置权限
     * @param chainTask 任务
     */
    fun requestBackgroundLocation(chainTask: ChainTask) {
        requestFragment.requestBackgroundLocation(this, chainTask)
    }

    /**
     * 请求悬浮框权限
     * @param chainTask 任务
     */
    fun requestSystemWindow(chainTask: ChainTask) {
        requestFragment.requestSystemWindow(this, chainTask)
    }

    /**
     * 请求写入权限
     * @param chainTask 任务
     */
    fun requestWriter(chainTask: ChainTask) {
        requestFragment.requestWriteSetting(this, chainTask)
    }

    /**
     * 请求操作sd卡权限
     * @param chainTask 任务
     */
    fun requestExternalStorage(chainTask: ChainTask) {
        requestFragment.requestExternalStorage(this, chainTask)
    }

    /**
     * 请求安装权限
     * @param chainTask 任务
     */
    fun requestInstall(chainTask: ChainTask) {
        requestFragment.requestInstall(this, chainTask)
    }

    /**
     * 请求通知权限
     *
     * @param chainTask 任务
     */
    fun requestNotificationPermissionNow(chainTask: ChainTask) {
        requestFragment.requestNotificationPermissionNow(this, chainTask)
    }

    /**
     * 是否需要请求后台获取位置权限
     */
    fun isRequestBackgroundLocationPermission(): Boolean {
        return specialPermissions.contains(BackgroundLocationTask.ACCESS_BACKGROUND_LOCATION)
    }

    /**
     * 是否需要请求悬浮框权限
     */
    fun isRequestSystemWindowPermission(): Boolean {
        return specialPermissions.contains(Manifest.permission.SYSTEM_ALERT_WINDOW)
    }

    /**
     * 是否需要请求写入权限
     */
    fun isRequestWriterPermission(): Boolean {
        return specialPermissions.contains(Manifest.permission.WRITE_SETTINGS)
    }

    /**
     * 是否需要请求操作sd卡权限
     */
    fun isRequestExternalStoragePermission(): Boolean {
        return specialPermissions.contains(ExternalStorageTask.MANAGE_EXTERNAL_STORAGE)
    }

    /**
     * 是否需要请求安装权限
     */
    fun isRequestInstallPermission(): Boolean {
        return specialPermissions.contains(InstallTask.REQUEST_INSTALL_PACKAGES)
    }


    /**
     * Should we request the specific special permission or not.
     *
     * @return True if specialPermissions contains POST_NOTIFICATIONS permission, false otherwise.
     */
    fun isRequestNotificationPermission(): Boolean {
        return specialPermissions.contains(PermissionL.Permission.POST_NOTIFICATIONS)
    }



    private fun startRequest() {
        //  如果在请求权限了 就不在请求
        if (inRequestFlow) return
        inRequestFlow = true
        //锁住屏幕 防止发生错误
        lockOrientation()
        // 构建请求权限链 先请求普通权限 在请求特殊权限，这种写法的好处是
        // 以后再有新的特殊权限 可以再接继承baseTask 编写自己的逻辑 然后加入当任务链当中
        // 很符合类的 单一指责
        val requestChain = RequestChain()
        requestChain.addTaskToChain(NormalTask(this))
        requestChain.addTaskToChain(BackgroundLocationTask(this))
        requestChain.addTaskToChain(SystemWindowTask(this))
        requestChain.addTaskToChain(WriterTask(this))
        requestChain.addTaskToChain(ExternalStorageTask(this))
        requestChain.addTaskToChain(InstallTask(this))
        requestChain.runTask()
    }

    /**
     * 请求结束
     */
    internal fun endRequest() {
        //移除fragment
        removeFragment()
        //接触屏幕锁定
        restoreOrientation()
        //请求权限流完成
        inRequestFlow = false
    }


    /**
     * 去系统app设置详情页
     */
    fun toSetting(permissions: List<String>) {
        toSettingPermissions.clear()
        toSettingPermissions.addAll(permissions)
        requestFragment.toAppSetting()
    }

    /**
     * 锁定屏幕方向
     */
    @SuppressLint("SourceLockedOrientationActivity")
    private fun lockOrientation() {
        if (Build.VERSION.SDK_INT != Build.VERSION_CODES.O) {
            originRequestOrientation = activity.requestedOrientation
            val orientation = activity.resources.configuration.orientation
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
            } else if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
            }
        }
    }

    /**
     * 解锁屏幕方向
     */
    private fun restoreOrientation() {
        if (Build.VERSION.SDK_INT != Build.VERSION_CODES.O) {
            activity.requestedOrientation = originRequestOrientation
        }
    }

    /**
     * 移除请求权限fragment
     */
    private fun removeFragment() {
        val existedFragment = fragmentManager.findFragmentByTag(FRAGMENT_TAG)
        if (existedFragment != null) {
            fragmentManager
                .beginTransaction()
                .remove(existedFragment)
                .commitNowAllowingStateLoss()
        }
    }

    companion object {

        /**
         * 请求权限的fragment的tag
         */
        private const val FRAGMENT_TAG = "PermissionFragment"

        /**
         * 当前请求流是否完成
         */
        private var inRequestFlow = false
    }

    init {
        if (fragmentActivity != null) {
            this.activity = fragmentActivity
        }
        if (fragmentActivity == null && fragment != null) {
            this.activity = fragment.requireActivity()
        }
        this.fragment = fragment
        this.normalPermissions = normalPermissions
        this.specialPermissions = specialPermissions

    }
}