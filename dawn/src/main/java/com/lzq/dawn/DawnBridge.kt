package com.lzq.dawn

import android.Manifest.permission
import android.app.Activity
import android.app.Application
import android.app.Notification
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Parcelable
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.annotation.StringRes
import androidx.core.app.NotificationCompat
import com.google.gson.Gson
import com.hjq.toast.Toaster
import com.lzq.dawn.util.activity.ActivityLifecycleCallbacks
import com.lzq.dawn.util.activity.ActivityLifecycleImpl
import com.lzq.dawn.util.activity.ActivityUtils
import com.lzq.dawn.util.activity.OnAppStatusChangedListener
import com.lzq.dawn.util.app.AppUtils
import com.lzq.dawn.util.app.AppUtils.appVersionCode
import com.lzq.dawn.util.app.AppUtils.appVersionName
import com.lzq.dawn.util.convert.ConvertUtils
import com.lzq.dawn.util.convert.ConvertUtils.bytes2String
import com.lzq.dawn.util.encode.EncodeUtils
import com.lzq.dawn.util.encrypt.EncryptUtils
import com.lzq.dawn.util.file.FileIOUtils
import com.lzq.dawn.util.file.FileIOUtils.readFile2BytesByChannel
import com.lzq.dawn.util.file.FileIOUtils.writeFileFromBytesByChannel
import com.lzq.dawn.util.file.FileUtils
import com.lzq.dawn.util.gson.GsonUtils
import com.lzq.dawn.util.image.ImageUtils
import com.lzq.dawn.util.intent.IntentUtils
import com.lzq.dawn.util.notification.ChannelConfig
import com.lzq.dawn.util.notification.NotificationUtils
import com.lzq.dawn.util.process.ProcessUtils
import com.lzq.dawn.util.rom.RomUtils
import com.lzq.dawn.util.rom.RomUtils.romInfo
import com.lzq.dawn.util.screen.ScreenUtils
import com.lzq.dawn.util.sdcard.SDCardUtils
import com.lzq.dawn.util.service.ServiceUtils
import com.lzq.dawn.util.shell.CommandResult
import com.lzq.dawn.util.shell.ShellUtils
import com.lzq.dawn.util.size.SizeUtils
import com.lzq.dawn.util.string.StringUtils
import com.lzq.dawn.util.thread.SimpleTask
import com.lzq.dawn.util.thread.ThreadUtils
import com.lzq.dawn.util.throwable.ThrowableUtils
import com.lzq.dawn.util.time.TimeUtils
import com.lzq.dawn.util.uri.UriUtils
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream
import java.io.Serializable
import java.lang.reflect.Type

/**
 * @Name :DawnBridge
 * @Time :2022/7/12 14:21
 * @Author :  Lzq
 * @Desc : Dawn框架
 */
object DawnBridge {
    private var mApp: Application? = null

    /**
     * 初始化工具类
     *
     * @param app app application
     */
    fun init(app: Application?) {
        if (app == null) {
            Log.e("DawnUtil", "app is null.")
            return
        }
        if (mApp == null) {
            mApp = app
            ActivityLifecycleImpl.INSTANCE.init(mApp!!)
            return
        }
        if (!Toaster.isInit()) {
            Toaster.init(mApp)
        }
        if (mApp == app) {
            return
        }
        ActivityLifecycleImpl.INSTANCE.unInit(mApp!!)
        mApp = app
        ActivityLifecycleImpl.INSTANCE.init(mApp!!)
    }

    @JvmStatic
    val app: Application
        get() =  mApp!!
    val topActivity: Activity?
        ///////////////////////////////////////////////////////////////////////////
        get() = ActivityLifecycleImpl.INSTANCE.topActivity

    fun addOnAppStatusChangedListener(listener: OnAppStatusChangedListener) {
        ActivityLifecycleImpl.INSTANCE.addOnAppStatusChangedListener(listener)
    }

    fun removeOnAppStatusChangedListener(listener: OnAppStatusChangedListener) {
        ActivityLifecycleImpl.INSTANCE.removeOnAppStatusChangedListener(listener)
    }

    fun addActivityLifecycleCallbacks(callbacks: ActivityLifecycleCallbacks?) {
        ActivityLifecycleImpl.INSTANCE.addActivityLifecycleCallbacks(callbacks)
    }

    fun removeActivityLifecycleCallbacks(callbacks: ActivityLifecycleCallbacks?) {
        ActivityLifecycleImpl.INSTANCE.removeActivityLifecycleCallbacks(callbacks)
    }

    fun addActivityLifecycleCallbacks(
        activity: Activity?, callbacks: ActivityLifecycleCallbacks?
    ) {
        ActivityLifecycleImpl.INSTANCE.addActivityLifecycleCallbacks(activity, callbacks)
    }

    fun removeActivityLifecycleCallbacks(activity: Activity?) {
        ActivityLifecycleImpl.INSTANCE.removeActivityLifecycleCallbacks(activity)
    }

    fun removeActivityLifecycleCallbacks(
        activity: Activity?, callbacks: ActivityLifecycleCallbacks?
    ) {
        ActivityLifecycleImpl.INSTANCE.removeActivityLifecycleCallbacks(activity, callbacks)
    }

    val activityList: List<Activity>
        get() = ActivityLifecycleImpl.INSTANCE.activityList
    val applicationByReflect: Application?
        get() = ActivityLifecycleImpl.INSTANCE.applicationByReflect
    val isAppForeground: Boolean
        get() = ActivityLifecycleImpl.INSTANCE.isAppForeground

    ///////////////////////////////////////////////////////////////////////////
    // ActivityUtils
    ///////////////////////////////////////////////////////////////////////////
    fun isActivityAlive(activity: Activity?): Boolean {
        return ActivityUtils.isActivityAlive(activity)
    }

    fun getLauncherActivity(pkg: String?): String {
        return ActivityUtils.getLauncherActivity(pkg!!)
    }

    fun getActivityByContext(context: Context?): Activity? {
        return ActivityUtils.getActivityByContext(context)
    }

    fun startHomeActivity() {
        ActivityUtils.startHomeActivity()
    }

    fun finishAllActivities() {
        ActivityUtils.finishAllActivities()
    }

    ///////////////////////////////////////////////////////////////////////////
    // AppUtils
    ///////////////////////////////////////////////////////////////////////////
    fun isAppInstalled(pkgName: String?): Boolean {
        return AppUtils.isAppInstalled(pkgName)
    }

    val isAppDebug: Boolean
        get() = AppUtils.isAppDebug

    fun relaunchApp() {
        AppUtils.relaunchApp()
    }

    ///////////////////////////////////////////////////////////////////////////
    // ConvertUtils
    ///////////////////////////////////////////////////////////////////////////
    fun bytes2HexString(bytes: ByteArray?): String {
        return ConvertUtils.bytes2HexString(bytes)
    }

    fun hexString2Bytes(hexString: String?): ByteArray {
        return ConvertUtils.hexString2Bytes(hexString!!)
    }

    fun string2Bytes(string: String?): ByteArray? {
        return ConvertUtils.string2Bytes(string)
    }

    fun bytes2String(bytes: ByteArray?): String? {
        return bytes2String(bytes!!, "")
    }

    fun jsonObject2Bytes(jsonObject: JSONObject?): ByteArray? {
        return ConvertUtils.jsonObject2Bytes(jsonObject)
    }

    fun bytes2JSONObject(bytes: ByteArray?): JSONObject? {
        return ConvertUtils.bytes2JSONObject(bytes)
    }

    fun jsonArray2Bytes(jsonArray: JSONArray?): ByteArray? {
        return ConvertUtils.jsonArray2Bytes(jsonArray)
    }

    fun bytes2JSONArray(bytes: ByteArray?): JSONArray? {
        return ConvertUtils.bytes2JSONArray(bytes)
    }

    fun parcelable2Bytes(parcelable: Parcelable?): ByteArray? {
        return ConvertUtils.parcelable2Bytes(parcelable)
    }

    fun <T> bytes2Parcelable(
        bytes: ByteArray?, creator: Parcelable.Creator<T?>
    ): T? {
        return ConvertUtils.bytes2Parcelable(bytes, creator)
    }

    fun serializable2Bytes(serializable: Serializable?): ByteArray? {
        return ConvertUtils.serializable2Bytes(serializable)
    }

    fun bytes2Object(bytes: ByteArray?): Any? {
        return ConvertUtils.bytes2Object(bytes)
    }

    fun byte2FitMemorySize(byteSize: Long): String {
        return ConvertUtils.byte2FitMemorySize(byteSize)
    }

    fun inputStream2Bytes(`is`: InputStream?): ByteArray? {
        return ConvertUtils.inputStream2Bytes(`is`)
    }

    fun input2OutputStream(`is`: InputStream?): ByteArrayOutputStream? {
        return ConvertUtils.input2OutputStream(`is`)
    }

    fun inputStream2Lines(`is`: InputStream?, charsetName: String?): List<String>? {
        return ConvertUtils.inputStream2Lines(`is`, charsetName!!)
    }

    ///////////////////////////////////////////////////////////////////////////
    // EncodeUtils
    ///////////////////////////////////////////////////////////////////////////
    fun base64Encode(input: ByteArray?): ByteArray {
        return EncodeUtils.base64Encode(input)
    }

    fun base64Decode(input: ByteArray?): ByteArray {
        return EncodeUtils.base64Decode(input)
    }

    ///////////////////////////////////////////////////////////////////////////
    // EncryptUtils
    ///////////////////////////////////////////////////////////////////////////
    fun hashTemplate(data: ByteArray?, algorithm: String?): ByteArray? {
        return EncryptUtils.hashTemplate(data, algorithm)
    }

    ///////////////////////////////////////////////////////////////////////////
    // FileIOUtils
    ///////////////////////////////////////////////////////////////////////////
    fun writeFileFromBytes(
        file: File?, bytes: ByteArray?
    ): Boolean {
        return writeFileFromBytesByChannel(file!!, bytes, true)
    }

    fun readFile2Bytes(file: File?): ByteArray? {
        return readFile2BytesByChannel(file)
    }

    fun writeFileFromString(filePath: String?, content: String?, append: Boolean): Boolean {
        return FileIOUtils.writeFileFromString(filePath, content, append)
    }

    fun writeFileFromIS(filePath: String?, `is`: InputStream?): Boolean {
        return FileIOUtils.writeFileFromIS(filePath, `is`)
    }

    ///////////////////////////////////////////////////////////////////////////
    // FileUtils
    ///////////////////////////////////////////////////////////////////////////
    fun isFileExists(file: File?): Boolean {
        return FileUtils.isFileExists(file)
    }

    fun getFileByPath(filePath: String?): File? {
        return FileUtils.getFileByPath(filePath)
    }

    fun deleteAllInDir(dir: File?): Boolean {
        return FileUtils.deleteAllInDir(dir)
    }

    fun createOrExistsFile(file: File?): Boolean {
        return FileUtils.createOrExistsFile(file)
    }

    fun createOrExistsDir(file: File?): Boolean {
        return FileUtils.createOrExistsDir(file)
    }

    fun createFileByDeleteOldFile(file: File?): Boolean {
        return FileUtils.createFileByDeleteOldFile(file)
    }

    fun getFsTotalSize(path: String?): Long {
        return FileUtils.getFsTotalSize(path)
    }

    fun getFsAvailableSize(path: String?): Long {
        return FileUtils.getFsAvailableSize(path)
    }

    fun notifySystemToScan(file: File?) {
        FileUtils.notifySystemToScan(file)
    }

    ///////////////////////////////////////////////////////////////////////////
    // GsonUtils
    ///////////////////////////////////////////////////////////////////////////
    fun toJson(`object`: Any?): String {
        return GsonUtils.toJson(`object`)
    }

    fun <T> fromJson(json: String?, type: Type?): T {
        return GsonUtils.fromJson(json, type!!)
    }

    val gson4LogUtils: Gson?
        get() = GsonUtils.gson4LogUtils

    ///////////////////////////////////////////////////////////////////////////
    // ImageUtils
    ///////////////////////////////////////////////////////////////////////////
    fun bitmap2Bytes(bitmap: Bitmap?): ByteArray? {
        return ImageUtils.bitmap2Bytes(bitmap)
    }

    fun bitmap2Bytes(bitmap: Bitmap?, format: CompressFormat?, quality: Int): ByteArray? {
        return ImageUtils.bitmap2Bytes(bitmap, format!!, quality)
    }

    fun bytes2Bitmap(bytes: ByteArray?): Bitmap? {
        return ImageUtils.bytes2Bitmap(bytes)
    }

    fun drawable2Bytes(drawable: Drawable?): ByteArray? {
        return ImageUtils.drawable2Bytes(drawable)
    }

    fun drawable2Bytes(drawable: Drawable?, format: CompressFormat?, quality: Int): ByteArray? {
        return ImageUtils.drawable2Bytes(drawable, format!!, quality)
    }

    fun bytes2Drawable(bytes: ByteArray?): Drawable? {
        return ImageUtils.bytes2Drawable(bytes)
    }

    fun drawable2Bitmap(drawable: Drawable?): Bitmap? {
        return ImageUtils.drawable2Bitmap(drawable)
    }

    fun bitmap2Drawable(bitmap: Bitmap?): Drawable? {
        return ImageUtils.bitmap2Drawable(bitmap)
    }

    ///////////////////////////////////////////////////////////////////////////
    // IntentUtils
    ///////////////////////////////////////////////////////////////////////////
    fun isIntentAvailable(intent: Intent?): Boolean {
        return IntentUtils.isIntentAvailable(intent)
    }

    fun getLaunchAppIntent(pkgName: String?): Intent? {
        return IntentUtils.getLaunchAppIntent(pkgName)
    }

    fun getInstallAppIntent(file: File?): Intent? {
        return IntentUtils.getInstallAppIntent(file)
    }

    fun getInstallAppIntent(uri: Uri?): Intent? {
        return IntentUtils.getInstallAppIntent(uri)
    }

    fun getUninstallAppIntent(pkgName: String?): Intent {
        return IntentUtils.getUninstallAppIntent(pkgName!!)
    }

    fun getDialIntent(phoneNumber: String?): Intent {
        return IntentUtils.getDialIntent(phoneNumber!!)
    }

    @RequiresPermission(permission.CALL_PHONE)
    fun getCallIntent(phoneNumber: String?): Intent {
        return IntentUtils.getCallIntent(phoneNumber!!)
    }

    fun getSendSmsIntent(phoneNumber: String?, content: String?): Intent {
        return IntentUtils.getSendSmsIntent(phoneNumber!!, content)
    }

    fun getLaunchAppDetailsSettingsIntent(pkgName: String?, isNewTask: Boolean): Intent {
        return IntentUtils.getLaunchAppDetailsSettingsIntent(pkgName!!, isNewTask)
    }

    ///////////////////////////////////////////////////////////////////////////
    // NotificationUtils
    ///////////////////////////////////////////////////////////////////////////
    fun getNotification(
        channelConfig: ChannelConfig?, consumer: Consumer<NotificationCompat.Builder?>?
    ): Notification {
        return NotificationUtils.getNotification(channelConfig!!, consumer)
    }

    val isGrantedDrawOverlays: Boolean
        get() = Settings.canDrawOverlays(app)
    val isMainProcess: Boolean
        ///////////////////////////////////////////////////////////////////////////
        get() = ProcessUtils.isMainProcess
    val foregroundProcessName: String?
        get() = ProcessUtils.foregroundProcessName
    val currentProcessName: String
        get() = ProcessUtils.currentProcessName
    val isSamsung: Boolean
        ///////////////////////////////////////////////////////////////////////////
        get() = RomUtils.isSamsung
    val appScreenWidth: Int
        ///////////////////////////////////////////////////////////////////////////
        get() = ScreenUtils.appScreenWidth
    val isSDCardEnableByEnvironment: Boolean
        ///////////////////////////////////////////////////////////////////////////
        get() = SDCardUtils.isSDCardEnableByEnvironment

    ///////////////////////////////////////////////////////////////////////////
    // ServiceUtils
    ///////////////////////////////////////////////////////////////////////////
    fun isServiceRunning(className: String?): Boolean {
        return ServiceUtils.isServiceRunning(className!!)
    }

    ///////////////////////////////////////////////////////////////////////////
    // ShellUtils
    ///////////////////////////////////////////////////////////////////////////
    fun execCmd(command: String?, isRooted: Boolean): CommandResult {
        return ShellUtils.execCmd(command, isRooted)
    }

    ///////////////////////////////////////////////////////////////////////////
    // SizeUtils
    ///////////////////////////////////////////////////////////////////////////
    fun dp2px(dpValue: Float): Int {
        return SizeUtils.dp2px(dpValue)
    }

    fun px2dp(pxValue: Float): Int {
        return SizeUtils.px2dp(pxValue)
    }

    fun sp2px(spValue: Float): Int {
        return SizeUtils.sp2px(spValue)
    }

    fun px2sp(pxValue: Float): Int {
        return SizeUtils.px2sp(pxValue)
    }

    ///////////////////////////////////////////////////////////////////////////
    // StringUtils
    ///////////////////////////////////////////////////////////////////////////
    fun isSpace(s: String?): Boolean {
        return StringUtils.isSpace(s)
    }

    @JvmStatic
    fun equals(s1: CharSequence?, s2: CharSequence?): Boolean {
        return StringUtils.equals(s1, s2)
    }

    fun getString(@StringRes id: Int): String? {
        return StringUtils.getString(id)
    }

    fun getString(@StringRes id: Int, vararg formatArgs: Any?): String? {
        return StringUtils.getString(id, *formatArgs)
    }

    fun format(str: String?, vararg args: Any?): String? {
        return StringUtils.format(str, *args)
    }

    ///////////////////////////////////////////////////////////////////////////
    // ThreadUtils
    ///////////////////////////////////////////////////////////////////////////
    @JvmStatic
    fun <T> doAsync(task: Task<T>): Task<T> {
        ThreadUtils.cachedPool.execute(task)
        return task
    }

    @JvmStatic
    fun runOnUiThread(runnable: Runnable) {
        ThreadUtils.runOnUiThread(runnable)
    }

    @JvmStatic
    fun runOnUiThreadDelayed(runnable: Runnable?, delayMillis: Long) {
        ThreadUtils.runOnUiThreadDelayed(runnable, delayMillis)
    }

    ///////////////////////////////////////////////////////////////////////////
    // ThrowableUtils
    ///////////////////////////////////////////////////////////////////////////
    fun getFullStackTrace(throwable: Throwable?): String {
        return ThrowableUtils.getFullStackTrace(throwable)
    }

    ///////////////////////////////////////////////////////////////////////////
    // TimeUtils
    ///////////////////////////////////////////////////////////////////////////
    fun millis2FitTimeSpan(millis: Long, precision: Int): String? {
        return TimeUtils.millis2FitTimeSpan(millis, precision)
    }



    ///////////////////////////////////////////////////////////////////////////
    // UriUtils
    ///////////////////////////////////////////////////////////////////////////
    fun file2Uri(file: File?): Uri? {
        return UriUtils.file2Uri(file)
    }

    fun uri2File(uri: Uri?): File? {
        return UriUtils.uri2File(uri)
    }

    @Throws(ClassNotFoundException::class)
    fun getClassName(paramType: String?): Class<*>? {
        return when (paramType) {
            "boolean" -> Boolean::class.javaPrimitiveType
            "int" -> Int::class.javaPrimitiveType
            "long" -> Long::class.javaPrimitiveType
            "short" -> Short::class.javaPrimitiveType
            "byte" -> Byte::class.javaPrimitiveType
            "double" -> Double::class.javaPrimitiveType
            "float" -> Float::class.javaPrimitiveType
            "char" -> Char::class.javaPrimitiveType
            else -> paramType?.let { Class.forName(it) }
        }
    }

    abstract class Task<Result>(private val mConsumer: Consumer<Result>?) : SimpleTask<Result>() {
        override fun onSuccess(result: Result) {
            mConsumer?.accept(result)
        }
    }

    interface Consumer<T> {
        fun accept(t: T)
    }

    interface Supplier<T> {
        fun get(): T
    }

    interface Func1<Ret, Par> {
        fun call(param: Par): Ret
    }

    ///////////////////////////////////////////////////////////////////////////
    // Common
    ///////////////////////////////////////////////////////////////////////////
    class FileHead(private val mName: String) {
        private val mFirst = LinkedHashMap<String, String>()
        private val mLast = LinkedHashMap<String, String>()
        fun addFirst(key: String, value: String) {
            append2Host(mFirst, key, value)
        }

        fun append(extra: Map<String, String>?) {
            append2Host(mLast, extra)
        }

        fun append(key: String, value: String) {
            append2Host(mLast, key, value)
        }

        private fun append2Host(host: MutableMap<String, String>, extra: Map<String, String>?) {
            if (extra.isNullOrEmpty()) {
                return
            }
            for ((key, value) in extra) {
                append2Host(host, key, value)
            }
        }

        private fun append2Host(host: MutableMap<String, String>, key: String, value: String) {
            var key = key
            if (TextUtils.isEmpty(key) || TextUtils.isEmpty(value)) {
                return
            }
            val delta = 19 - key.length // 19 is length of "Device Manufacturer"
            if (delta > 0) {
                key += "                   ".substring(0, delta)
            }
            host[key] = value
        }

        val appended: String
            get() {
                val sb = StringBuilder()
                for ((key, value) in mLast) {
                    sb.append(key).append(": ").append(value).append("\n")
                }
                return sb.toString()
            }

        override fun toString(): String {
            val sb = StringBuilder()
            val border = "************* $mName Head ****************\n"
            sb.append(border)
            for ((key, value) in mFirst) {
                sb.append(key).append(": ").append(value).append("\n")
            }
            sb.append("Rom Info           : ").append(romInfo).append("\n")
            sb.append("Device Manufacturer: ").append(Build.MANUFACTURER).append("\n")
            sb.append("Device Model       : ").append(Build.MODEL).append("\n")
            sb.append("Android Version    : ").append(Build.VERSION.RELEASE).append("\n")
            sb.append("Android SDK        : ").append(Build.VERSION.SDK_INT).append("\n")
            sb.append("App VersionName    : ").append(appVersionName).append("\n")
            sb.append("App VersionCode    : ").append(appVersionCode).append("\n")
            sb.append(appended)
            return sb.append(border).append("\n").toString()
        }
    }
}