package com.lzq.dawn.util.permission.request

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.lzq.dawn.util.permission.PermissionBuilder
import com.lzq.dawn.util.permission.PermissionL
import com.lzq.dawn.util.permission.task.ChainTask

import com.lzq.dawn.util.permission.util.AndroidVersion
import com.lzq.dawn.util.permission.task.BackgroundLocationTask


/**
 * className :PermissionFragment
 * createTime :2022/5/18 13:57
 * @Author :  Lzq
 * 权限请求fragment
 */
class PermissionFragment : Fragment() {


    private var handler = Handler(Looper.getMainLooper())

    /**
     * 请求权限的构造对象
     */
    private lateinit var builder: PermissionBuilder

    /**
     * 请求权限的任务
     */
    private lateinit var task: ChainTask

    /**
     * 请求普通权限
     */
    private val requestNormalLaunch =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { grantResults ->
            normalResult(grantResults)
        }

    /**
     * 请求安卓10以上的后台定位权限
     */
    private val requestBackgroundLocationLaunch =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            backgroundLocationResult(granted)
        }

    /**
     * 请求系统悬浮框权限
     */
    private val requestSystemWindowLaunch =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            systemWindowResult()
        }

    /**
     * 请求写入权限
     */
    private val requestWriterLaunch =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            writerResult()
        }

    /**
     * 请求管理外部存储权限
     */
    private val requestExternalStorageLaunch =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            externalStorageResult()
        }

    /**
     * 请求安装安装包权限
     */
    private val requestInstallLaunch =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            installResult()
        }

    /**
     * 请求安装通知权限
     */
    private val requestNotificationLaunch =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            postResult {
                notificationResult()
            }
        }

    /**
     * 用户从设置页面回来结果
     */
    private val requestToAppSettingsLaunch =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (checkForGC()) {
                task.requestAgain(ArrayList(builder.toSettingPermissions))
            }
        }

    /**
     * 前往设置里的app权限设置页
     */
    fun toAppSetting() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", requireActivity().packageName, null)
        intent.data = uri
        requestToAppSettingsLaunch.launch(intent)
    }

    /**
     * 请求权限
     * @param builder PermissionBuilder 实例
     * @param task 当前task
     * @param permissions 要请求的权限
     */
    fun requestNormal(builder: PermissionBuilder, task: ChainTask, permissions: Set<String>) {
        this.builder = builder
        this.task = task
        requestNormalLaunch.launch(permissions.toTypedArray())
    }

    /**
     * 请求后台定位权限
     * @param builder PermissionBuilder 实例
     * @param task 当前task
     */
    fun requestBackgroundLocation(builder: PermissionBuilder, task: ChainTask) {
        this.builder = builder
        this.task = task
        requestBackgroundLocationLaunch.launch(BackgroundLocationTask.ACCESS_BACKGROUND_LOCATION)
    }

    /**
     * 请求悬浮框权限
     */
    fun requestSystemWindow(builder: PermissionBuilder, task: ChainTask) {
        this.builder = builder
        this.task = task
        if (!Settings.canDrawOverlays(context)) {
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
            intent.data = Uri.parse("package:${requireActivity().packageName}")
            requestSystemWindowLaunch.launch(intent)
        } else {
            systemWindowResult()
        }
    }

    /**
     * 请求写入设置权限
     */
    fun requestWriteSetting(builder: PermissionBuilder, task: ChainTask) {
        this.builder = builder
        this.task = task
        if (!Settings.System.canWrite(context)) {
            val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
            intent.data = Uri.parse("package:${requireActivity().packageName}")
            requestWriterLaunch.launch(intent)
        } else {
            writerResult()
        }
    }

    /**
     * 请求外部存储读写权限
     */
    fun requestExternalStorage(builder: PermissionBuilder, task: ChainTask) {
        this.builder = builder
        this.task = task
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && !Environment.isExternalStorageManager()) {
            val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
            requestExternalStorageLaunch.launch(intent)
        } else {
            externalStorageResult()
        }
    }

    /**
     * 请求安装app权限
     */
    fun requestInstall(builder: PermissionBuilder, task: ChainTask) {
        this.builder = builder
        this.task = task
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES)
            intent.data = Uri.parse("package:${requireActivity().packageName}")
            requestInstallLaunch.launch(intent)
        } else {
            installResult()
        }
    }

    /**
     * 请求通知权限。在Android O及以上版本上，它是由设置请求的。ACTION_APP_NOTIFICATION_SETTINGS带意图
     */
    fun requestNotificationPermissionNow(
        permissionBuilder: PermissionBuilder,
        chainTask: ChainTask
    ) {
        builder = permissionBuilder
        task = chainTask
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, requireActivity().packageName)
            requestNotificationLaunch.launch(intent)
        } else {
            installResult()
        }
    }


    /**
     * 普通权限结果处理
     */
    private fun normalResult(grantResults: Map<String, Boolean>) {
        if (checkForGC()) {
            //先清空，确保权限结果是正确的
            builder.grantedPermissions.clear()
            //被拒绝的权限
            val showReasonList: MutableList<String> = ArrayList()
            //被永久拒绝的权限
            val forwardList: MutableList<String> = ArrayList()
            for ((permission, granted) in grantResults) {
                if (granted) {
                    //同意授予的权限
                    builder.grantedPermissions.add(permission)
                    builder.deniedPermissions.remove(permission)
                    builder.permanentDeniedPermissions.remove(permission)
                } else {
                    //拒绝授予的权限
                    //shouldShowRequestPermissionRationale方法的作用是 需要在申请权限的结果中判断，单独判断 没有什么意义
                    //1，没有申请过权限，申请就是了，所以返回false；
                    //2，申请了用户拒绝了，那你就要提示用户了，所以返回true；
                    //3，用户选择了拒绝并且不再提示，那你也不要申请了，也不要提示用户了，所以返回false；
                    //4，已经允许了，不需要申请也不需要提示，所以返回false；
                    val shouldShowRationale = ActivityCompat.shouldShowRequestPermissionRationale(
                        requireActivity(),
                        permission
                    )
                    if (shouldShowRationale) {
                        showReasonList.add(permission)
                        builder.deniedPermissions.add(permission)
                    } else {
                        forwardList.add(permission)
                        builder.permanentDeniedPermissions.add(permission)
                        builder.deniedPermissions.remove(permission)
                    }


                }
            }
            //验证失败的权限是否真的失败了
            val deniedPermissions: MutableList<String> = ArrayList()
            deniedPermissions.addAll(builder.deniedPermissions)
            deniedPermissions.addAll(builder.grantedPermissions)
            for (permission in deniedPermissions) {
                if (PermissionL.isGranted(requireContext(), permission)) {
                    builder.deniedPermissions.remove(permission)
                    builder.grantedPermissions.add(permission)
                }
            }
            val allGranted = builder.grantedPermissions.size == builder.normalPermissions.size

            if (allGranted) {
                task.finish()
            } else {
                if (showReasonList.isNotEmpty()) {
                    task.requestAgain(showReasonList)
                } else {
                    task.finish()
                }
            }
        }
    }

    /**
     * 后台定位权限结果处理
     */
    private fun backgroundLocationResult(granted: Boolean) {
        if (checkForGC()) {
            postResult {
                if (granted) {
                    //权限已授予
                    builder.grantedPermissions.add(BackgroundLocationTask.ACCESS_BACKGROUND_LOCATION)
                    builder.deniedPermissions.remove(BackgroundLocationTask.ACCESS_BACKGROUND_LOCATION)
                    builder.permanentDeniedPermissions.remove(BackgroundLocationTask.ACCESS_BACKGROUND_LOCATION)
                    task.finish()
                } else {
                    //拒绝授予的权限
                    //shouldShowRequestPermissionRationale方法的作用是 需要在申请权限的结果中判断，单独判断 没有什么意义
                    //1，没有申请过权限，申请就是了，所以返回false；
                    //2，申请了用户拒绝了，那你就要提示用户了，所以返回true；
                    //3，用户选择了拒绝并且不再提示，那你也不要申请了，也不要提示用户了，所以返回false；
                    //4，已经允许了，不需要申请也不需要提示，所以返回false；
                    val shouldShowRationale = ActivityCompat.shouldShowRequestPermissionRationale(
                        requireActivity(),
                        BackgroundLocationTask.ACCESS_BACKGROUND_LOCATION
                    )
                    if (shouldShowRationale) {
                        builder.deniedPermissions.add(BackgroundLocationTask.ACCESS_BACKGROUND_LOCATION)
                        task.requestAgain(listOf(BackgroundLocationTask.ACCESS_BACKGROUND_LOCATION))
                    } else {
                        builder.deniedPermissions.remove(BackgroundLocationTask.ACCESS_BACKGROUND_LOCATION)
                        builder.permanentDeniedPermissions.add(BackgroundLocationTask.ACCESS_BACKGROUND_LOCATION)
                        task.finish()
                    }

                }
            }
        }
    }

    /**
     * 悬浮框权限请求
     */
    private fun systemWindowResult() {
        if (checkForGC()) {
            postResult {
                if (AndroidVersion.isAndroid6()) {
                    if (Settings.canDrawOverlays(context)) {
                        task.finish()
                    } else {
                        //拒绝授予的权限
                        //shouldShowRequestPermissionRationale方法的作用是 需要在申请权限的结果中判断，单独判断 没有什么意义
                        //1，没有申请过权限，申请就是了，所以返回false；
                        //2，申请了用户拒绝了，那你就要提示用户了，所以返回true；
                        //3，用户选择了拒绝并且不再提示，那你也不要申请了，也不要提示用户了，所以返回false；
                        //4，已经允许了，不需要申请也不需要提示，所以返回false；
                        val shouldShowRationale =
                            ActivityCompat.shouldShowRequestPermissionRationale(
                                requireActivity(),
                                Manifest.permission.SYSTEM_ALERT_WINDOW
                            )
                        if (shouldShowRationale) {
                            builder.deniedPermissions.add(Manifest.permission.SYSTEM_ALERT_WINDOW)
                            task.requestAgain(listOf(Manifest.permission.SYSTEM_ALERT_WINDOW))
                        } else {
                            builder.permanentDeniedPermissions.add(Manifest.permission.SYSTEM_ALERT_WINDOW)
                            builder.deniedPermissions.remove(Manifest.permission.SYSTEM_ALERT_WINDOW)
                            task.finish()
                        }

                    }
                } else {
                    task.finish()
                }
            }
        }
    }

    /**
     * 写入权限请求结果
     */
    private fun writerResult() {
        if (checkForGC()) {
            postResult {
                if (AndroidVersion.isAndroid6()) {
                    if (Settings.System.canWrite(context)) {
                        task.finish()
                    } else {
                        //拒绝授予的权限
                        //shouldShowRequestPermissionRationale方法的作用是 需要在申请权限的结果中判断，单独判断 没有什么意义
                        //1，没有申请过权限，申请就是了，所以返回false；
                        //2，申请了用户拒绝了，那你就要提示用户了，所以返回true；
                        //3，用户选择了拒绝并且不再提示，那你也不要申请了，也不要提示用户了，所以返回false；
                        //4，已经允许了，不需要申请也不需要提示，所以返回false；
                        val shouldShowRationale =
                            ActivityCompat.shouldShowRequestPermissionRationale(
                                requireActivity(),
                                Manifest.permission.WRITE_SETTINGS
                            )
                        if (shouldShowRationale) {
                            builder.deniedPermissions.add(Manifest.permission.WRITE_SETTINGS)
                            task.requestAgain(listOf(Manifest.permission.WRITE_SETTINGS))
                        } else {
                            builder.permanentDeniedPermissions.add(Manifest.permission.WRITE_SETTINGS)
                            builder.deniedPermissions.remove(Manifest.permission.WRITE_SETTINGS)
                            task.finish()
                        }

                    }
                } else {
                    task.finish()
                }
            }
        }
    }

    /**
     * 操作sd卡权限结果
     */
    private fun externalStorageResult() {
        if (checkForGC()) {
            postResult {
                if (AndroidVersion.isAndroid11()) {
                    if (Environment.isExternalStorageManager()) {
                        task.finish()
                    } else {
                        //拒绝授予的权限
                        //shouldShowRequestPermissionRationale方法的作用是 需要在申请权限的结果中判断，单独判断 没有什么意义
                        //1，没有申请过权限，申请就是了，所以返回false；
                        //2，申请了用户拒绝了，那你就要提示用户了，所以返回true；
                        //3，用户选择了拒绝并且不再提示，那你也不要申请了，也不要提示用户了，所以返回false；
                        //4，已经允许了，不需要申请也不需要提示，所以返回false；
                        val shouldShowRationale =
                            ActivityCompat.shouldShowRequestPermissionRationale(
                                requireActivity(),
                                Manifest.permission.MANAGE_EXTERNAL_STORAGE
                            )
                        if (shouldShowRationale) {
                            builder.deniedPermissions.add(Manifest.permission.MANAGE_EXTERNAL_STORAGE)
                            task.requestAgain(listOf(Manifest.permission.MANAGE_EXTERNAL_STORAGE))
                        } else {
                            builder.permanentDeniedPermissions.add(Manifest.permission.MANAGE_EXTERNAL_STORAGE)
                            builder.deniedPermissions.remove(Manifest.permission.MANAGE_EXTERNAL_STORAGE)
                            task.finish()
                        }
                    }
                } else {
                    task.finish()
                }
            }
        }
    }

    /**
     * 安装其他程序权限结果
     */
    private fun installResult() {
        if (checkForGC()) {
            postResult {
                if (AndroidVersion.isAndroid9()) {
                    if (requireActivity().packageManager.canRequestPackageInstalls()) {
                        task.finish()
                    } else {
                        //拒绝授予的权限
                        //shouldShowRequestPermissionRationale方法的作用是 需要在申请权限的结果中判断，单独判断 没有什么意义
                        //1，没有申请过权限，申请就是了，所以返回false；
                        //2，申请了用户拒绝了，那你就要提示用户了，所以返回true；
                        //3，用户选择了拒绝并且不再提示，那你也不要申请了，也不要提示用户了，所以返回false；
                        //4，已经允许了，不需要申请也不需要提示，所以返回false；
                        val shouldShowRationale =
                            ActivityCompat.shouldShowRequestPermissionRationale(
                                requireActivity(),
                                Manifest.permission.REQUEST_INSTALL_PACKAGES
                            )
                        if (shouldShowRationale) {
                            builder.deniedPermissions.add(Manifest.permission.REQUEST_INSTALL_PACKAGES)
                            task.requestAgain(listOf(Manifest.permission.REQUEST_INSTALL_PACKAGES))
                        } else {
                            builder.permanentDeniedPermissions.add(Manifest.permission.REQUEST_INSTALL_PACKAGES)
                            builder.deniedPermissions.remove(Manifest.permission.REQUEST_INSTALL_PACKAGES)
                            task.finish()
                        }
                    }
                } else {
                    task.finish()
                }
            }
        }
    }


    /**
     * 处理通知权限请求的结果。
     */
    private fun notificationResult() {
        if (checkForGC()) {
            postResult {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    if (PermissionL.areNotificationsEnabled(requireContext())) {
                        task.finish()
                    } else {
                        //拒绝授予的权限
                        //shouldShowRequestPermissionRationale方法的作用是 需要在申请权限的结果中判断，单独判断 没有什么意义
                        //1，没有申请过权限，申请就是了，所以返回false；
                        //2，申请了用户拒绝了，那你就要提示用户了，所以返回true；
                        //3，用户选择了拒绝并且不再提示，那你也不要申请了，也不要提示用户了，所以返回false；
                        //4，已经允许了，不需要申请也不需要提示，所以返回false；
                        val shouldShowRationale =
                            ActivityCompat.shouldShowRequestPermissionRationale(
                                requireActivity(),
                                PermissionL.Permission.POST_NOTIFICATIONS
                            )
                        if (shouldShowRationale) {
                            builder.deniedPermissions.add(PermissionL.Permission.POST_NOTIFICATIONS)
                            task.requestAgain(listOf(PermissionL.Permission.POST_NOTIFICATIONS))
                        } else {
                            builder.permanentDeniedPermissions.add(PermissionL.Permission.POST_NOTIFICATIONS)
                            builder.deniedPermissions.remove(PermissionL.Permission.POST_NOTIFICATIONS)
                            task.finish()
                        }
                    }
                } else {
                    task.finish()
                }
            }
        }
    }


    /**
     * 检查是否被gc回收
     */
    private fun checkForGC(): Boolean {
        if (!::builder.isInitialized || !::task.isInitialized) {
            Log.w(
                "PermissionL",
                "builder 和 task 已经被回收."
            )
            return false
        }
        return true
    }

    /**
     * 确保主线程 执行
     */
    private fun postResult(callBlack: () -> Unit) {
        handler.post {
            callBlack()
        }
    }
}