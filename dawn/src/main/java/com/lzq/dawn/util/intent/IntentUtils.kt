package com.lzq.dawn.util.intent

import android.Manifest.permission
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import androidx.annotation.RequiresPermission
import androidx.core.content.FileProvider
import com.lzq.dawn.DawnBridge
import java.io.File
import java.util.LinkedList

/**
 * @Name :IntentDawnBridge
 * @Time :2022/8/2 10:34
 * @Author :  Lzq
 * @Desc : intent
 */
object IntentUtils {
    /**
     * 返回意图是否可用。
     *
     * @param intent intent.
     * @return `true`: yes<br></br>`false`: no
     */
    @JvmStatic
    fun isIntentAvailable(intent: Intent?): Boolean {
        return DawnBridge.getApp().packageManager.queryIntentActivities(
                intent!!,
                PackageManager.MATCH_DEFAULT_ONLY
            ).size > 0
    }

    /**
     * 返回安装应用程序的意图。
     *
     * 大于 25 的目标 API 必须持有
     * `<uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES" />`
     *
     * @param filePath 文件的路径。
     * @return 安装应用程序的意图
     */
    fun getInstallAppIntent(filePath: String?): Intent? {
        return getInstallAppIntent(DawnBridge.getFileByPath(filePath))
    }

    /**
     * 返回安装应用程序的意图。
     * *
     *
     *大于 25 的目标 API 必须持有
     * `<uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES" />`
     *
     * @param file file.
     * @return 安装应用程序的意图
     */
    @JvmStatic
    fun getInstallAppIntent(file: File?): Intent? {
        if (!DawnBridge.isFileExists(file)) {
            return null
        }
        val uri: Uri
        uri = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            Uri.fromFile(file)
        } else {
            val authority = DawnBridge.getApp().packageName + ".fileprovider"
            FileProvider.getUriForFile(DawnBridge.getApp(), authority, file!!)
        }
        return getInstallAppIntent(uri)
    }

    /**
     * 返回安装应用程序的意图。
     *
     * 大于 25 的目标 API 必须持有
     * `<uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES" />`
     *
     * @param uri uri.
     * @return 安装应用程序的意图。
     */
    @JvmStatic
    fun getInstallAppIntent(uri: Uri?): Intent? {
        if (uri == null) {
            return null
        }
        val intent = Intent(Intent.ACTION_VIEW)
        val type = "application/vnd.android.package-archive"
        intent.setDataAndType(uri, type)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        }
        return intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    /**
     * 返回卸载应用程序的意图。
     *
     * 大于 25 的目标 API 必须持有
     * Must hold `<uses-permission android:name="android.permission.REQUEST_DELETE_PACKAGES" />`
     *
     * @param pkgName 包名
     * @return v
     */
    @JvmStatic
    fun getUninstallAppIntent(pkgName: String): Intent {
        val intent = Intent(Intent.ACTION_DELETE)
        intent.data = Uri.parse("package:$pkgName")
        return intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    /**
     * 返回启动应用程序的意图。
     *
     * @param pkgName 包名
     * @return 返回启动应用程序的意图。
     */
    @JvmStatic
    fun getLaunchAppIntent(pkgName: String?): Intent? {
        val launcherActivity = DawnBridge.getLauncherActivity(pkgName)
        if (DawnBridge.isSpace(launcherActivity)) {
            return null
        }
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        intent.setClassName(pkgName!!, launcherActivity)
        return intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    /**
     * 返回启动应用详细信息设置的意图。
     *
     * @param pkgName 包名
     * @return 返回启动应用详细信息设置的意图。
     */
    fun getLaunchAppDetailsSettingsIntent(pkgName: String): Intent {
        return getLaunchAppDetailsSettingsIntent(pkgName, false)
    }

    /**
     * 返回启动应用详细信息设置的意图。
     *
     * @param pkgName 包名
     * @return 返回启动应用详细信息设置的意图
     */
    @JvmStatic
    fun getLaunchAppDetailsSettingsIntent(pkgName: String, isNewTask: Boolean): Intent {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.data = Uri.parse("package:$pkgName")
        return getIntent(intent, isNewTask)
    }

    /**
     * 返回分享文本的意图。
     *
     * @param content content.
     * @return 返回分享文本的意图。
     */
    fun getShareTextIntent(content: String?): Intent {
        var intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, content)
        intent = Intent.createChooser(intent, "")
        return getIntent(intent, true)
    }

    /**
     * 返回分享图片的意图。
     *
     * @param imagePath 图像的路径。
     * @return 分享图片的意图。
     */
    fun getShareImageIntent(imagePath: String?): Intent {
        return getShareTextImageIntent("", imagePath)
    }

    /**
     * 返回分享图片的意图。
     *
     * @param imageFile 图像的文件
     * @return 分享图片的意图。
     */
    fun getShareImageIntent(imageFile: File?): Intent {
        return getShareTextImageIntent("", imageFile)
    }

    /**
     * 返回分享图片的意图
     *
     * @param imageUri 图片的uri
     * @return 返回分享图片的意图
     */
    fun getShareImageIntent(imageUri: Uri?): Intent {
        return getShareTextImageIntent("", imageUri)
    }

    /**
     * 返回分享图片的意图
     *
     * @param content   content.
     * @param imagePath 图像的路径。
     * @return 返回分享图片的意图
     */
    fun getShareTextImageIntent(content: String?, imagePath: String?): Intent {
        return getShareTextImageIntent(content, DawnBridge.getFileByPath(imagePath))
    }

    /**
     * 返回分享图片的意图
     *
     * @param content   content.
     * @param imageFile 图像的文件
     * @return 返回分享图片的意图
     */
    fun getShareTextImageIntent(content: String?, imageFile: File?): Intent {
        return getShareTextImageIntent(content, DawnBridge.file2Uri(imageFile))
    }

    /**
     * 返回分享图片的意图
     *
     * @param content  content.
     * @param imageUri 图片的uri
     * @return 返回分享图片的意图
     */
    fun getShareTextImageIntent(content: String?, imageUri: Uri?): Intent {
        var intent = Intent(Intent.ACTION_SEND)
        intent.putExtra(Intent.EXTRA_TEXT, content)
        intent.putExtra(Intent.EXTRA_STREAM, imageUri)
        intent.type = "image/*"
        intent = Intent.createChooser(intent, "")
        return getIntent(intent, true)
    }

    /**
     * 返回分享图片的意图
     *
     * @param imagePaths 图片的路径
     * @return 返回分享图片的意图
     */
    fun getShareImageIntent(imagePaths: LinkedList<String?>?): Intent {
        return getShareTextImageIntent("", imagePaths)
    }

    /**
     * 返回分享图片的意图
     *
     * @param images 图片的文件
     * @return 返回分享图片的意图
     */
    fun getShareImageIntent(images: List<File?>?): Intent {
        return getShareTextImageIntent("", images)
    }

    /**
     * 返回分享图片的意图
     *
     * @param uris 图片的uri
     * @return 返回分享图片的意图
     */
    fun getShareImageIntent(uris: ArrayList<Uri?>?): Intent {
        return getShareTextImageIntent("", uris)
    }

    /**
     * 返回分享图片的意图
     *
     * @param content    content.
     * @param imagePaths 图像的路径。
     * @return 返回分享图片的意图
     */
    fun getShareTextImageIntent(
        content: String?, imagePaths: LinkedList<String?>?
    ): Intent {
        val files: MutableList<File?> = ArrayList()
        if (imagePaths != null) {
            for (imagePath in imagePaths) {
                val file = DawnBridge.getFileByPath(imagePath)
                if (file != null) {
                    files.add(file)
                }
            }
        }
        return getShareTextImageIntent(content, files)
    }

    /**
     * 返回分享图片的意图
     *
     * @param content content.
     * @param images  图片的文件
     * @return 返回分享图片的意图
     */
    fun getShareTextImageIntent(content: String?, images: List<File?>?): Intent {
        val uris = ArrayList<Uri?>()
        if (images != null) {
            for (image in images) {
                val uri = DawnBridge.file2Uri(image)
                if (uri != null) {
                    uris.add(uri)
                }
            }
        }
        return getShareTextImageIntent(content, uris)
    }

    /**
     * 返回分享图片的意图
     *
     * @param content content.
     * @param uris    图片的uri
     * @return 返回分享图片的意图
     */
    fun getShareTextImageIntent(content: String?, uris: ArrayList<Uri?>?): Intent {
        var intent = Intent(Intent.ACTION_SEND_MULTIPLE)
        intent.putExtra(Intent.EXTRA_TEXT, content)
        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris)
        intent.type = "image/*"
        intent = Intent.createChooser(intent, "")
        return getIntent(intent, true)
    }

    /**
     * 返回组件的意图
     *
     * @param pkgName   包名
     * @param className 类名
     * @return 返回组件的意图
     */
    fun getComponentIntent(pkgName: String?, className: String?): Intent {
        return getComponentIntent(pkgName, className, null, false)
    }

    /**
     * 返回组件的意图
     *
     * @param pkgName   包名
     * @param className 类名
     * @param isNewTask True 添加新任务的标志，否则为 false。
     * @return 返回组件的意图
     */
    fun getComponentIntent(
        pkgName: String?, className: String?, isNewTask: Boolean
    ): Intent {
        return getComponentIntent(pkgName, className, null, isNewTask)
    }

    /**
     * 返回组件的意图
     *
     * @param pkgName   包名
     * @param className 类名
     * @param bundle    附加到此意图的附加组件。
     * @return 返回组件的意图
     */
    fun getComponentIntent(
        pkgName: String?, className: String?, bundle: Bundle?
    ): Intent {
        return getComponentIntent(pkgName, className, bundle, false)
    }

    /**
     * 返回组件的意图
     *
     * @param pkgName   包名
     * @param className 类名
     * @param bundle    附加到此意图的附加组件。
     * @param isNewTask True 添加新任务的标志，否则为 false。
     * @return 返回组件的意图
     */
    fun getComponentIntent(
        pkgName: String?, className: String?, bundle: Bundle?, isNewTask: Boolean
    ): Intent {
        val intent = Intent()
        if (bundle != null) {
            intent.putExtras(bundle)
        }
        val cn = ComponentName(pkgName!!, className!!)
        intent.component = cn
        return getIntent(intent, isNewTask)
    }

    val shutdownIntent: Intent
        /**
         * 返回关闭的意图。
         *
         * 需要root权限或持有 `android:sharedUserId="android.uid.system"`,
         * `<uses-permission android:name="android.permission.SHUTDOWN" />`
         * in manifest.
         *
         * @return 返回关闭的意图
         */
        get() {
            val intent: Intent
            intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Intent("com.android.internal.intent.action.REQUEST_SHUTDOWN")
            } else {
                Intent("android.intent.action.ACTION_REQUEST_SHUTDOWN")
            }
            intent.putExtra("android.intent.extra.KEY_CONFIRM", false)
            return intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

    /**
     * 返回拨号的意图。
     *
     * @param phoneNumber 手机号
     * @return 返回拨号的意图。
     */
    @JvmStatic
    fun getDialIntent(phoneNumber: String): Intent {
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + Uri.encode(phoneNumber)))
        return getIntent(intent, true)
    }

    /**
     * 返回通话的意图。
     *
     * 必须持有`<uses-permission android:name="android.permission.CALL_PHONE" />`
     *
     * @param phoneNumber phoneNumber
     * @return 通话的意图
     */
    @JvmStatic
    @RequiresPermission(permission.CALL_PHONE)
    fun getCallIntent(phoneNumber: String): Intent {
        val intent = Intent("android.intent.action.CALL", Uri.parse("tel:" + Uri.encode(phoneNumber)))
        return getIntent(intent, true)
    }

    /**
     * 返回发送短信的意图。
     *
     * @param phoneNumber 手机号
     * @param content     内容
     * @return 返回发送短信的意图。
     */
    @JvmStatic
    fun getSendSmsIntent(phoneNumber: String, content: String?): Intent {
        val uri = Uri.parse("smsto:" + Uri.encode(phoneNumber))
        val intent = Intent(Intent.ACTION_SENDTO, uri)
        intent.putExtra("sms_body", content)
        return getIntent(intent, true)
    }

    /**
     * 返回捕获的意图。
     *
     * @param outUri 输出的uri。
     * @return 返回捕获的意图
     */
    fun getCaptureIntent(outUri: Uri?): Intent {
        return getCaptureIntent(outUri, false)
    }

    /**
     * 返回捕获的意图。
     *
     * @param outUri    输出的uri
     * @param isNewTask True 添加新任务的标志，否则为 false。
     * @return 返回捕获的意图
     */
    fun getCaptureIntent(outUri: Uri?, isNewTask: Boolean): Intent {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outUri)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        return getIntent(intent, isNewTask)
    }

    private fun getIntent(intent: Intent, isNewTask: Boolean): Intent {
        return if (isNewTask) intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) else intent
    }
}