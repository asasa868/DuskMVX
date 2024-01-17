package com.lzq.dawn;

import static android.Manifest.permission.CALL_PHONE;

import android.app.Activity;
import android.app.Application;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Parcelable;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.annotation.RequiresPermission;
import androidx.annotation.StringRes;
import androidx.core.app.NotificationCompat;

import com.google.gson.Gson;
import com.hjq.toast.Toaster;
import com.lzq.dawn.util.activity.ActivityLifecycleCallbacks;
import com.lzq.dawn.util.activity.ActivityLifecycleImpl;
import com.lzq.dawn.util.activity.ActivityUtils;
import com.lzq.dawn.util.activity.OnAppStatusChangedListener;
import com.lzq.dawn.util.app.AppUtils;
import com.lzq.dawn.util.convert.ConvertUtils;
import com.lzq.dawn.util.encode.EncodeUtils;
import com.lzq.dawn.util.encrypt.EncryptUtils;
import com.lzq.dawn.util.file.FileIOUtils;
import com.lzq.dawn.util.file.FileUtils;
import com.lzq.dawn.util.gson.GsonUtils;
import com.lzq.dawn.util.image.ImageUtils;
import com.lzq.dawn.util.intent.IntentUtils;
import com.lzq.dawn.util.notification.ChannelConfig;
import com.lzq.dawn.util.notification.NotificationUtils;
import com.lzq.dawn.util.process.ProcessUtils;
import com.lzq.dawn.util.rom.RomUtils;
import com.lzq.dawn.util.screen.ScreenUtils;
import com.lzq.dawn.util.sdcard.SDCardUtils;
import com.lzq.dawn.util.service.ServiceUtils;
import com.lzq.dawn.util.shell.CommandResult;
import com.lzq.dawn.util.shell.ShellUtils;
import com.lzq.dawn.util.size.SizeUtils;
import com.lzq.dawn.util.string.StringUtils;
import com.lzq.dawn.util.thread.SimpleTask;
import com.lzq.dawn.util.thread.ThreadUtils;
import com.lzq.dawn.util.throwable.ThrowableUtils;
import com.lzq.dawn.util.time.TimeUtils;
import com.lzq.dawn.util.uri.UriUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @Name :DawnBridge
 * @Time :2022/7/12 14:21
 * @Author :  Lzq
 * @Desc : Dawn框架
 */
public class DawnBridge {
    private static Application mApp;

    /**
     * 初始化工具类
     *
     * @param app app application
     */
    public static void init(Application app) {
        if (app == null) {
            Log.e("DawnUtil", "app is null.");
            return;
        }
        if (mApp == null) {
            mApp = app;
            ActivityLifecycleImpl.INSTANCE.init(mApp);
            preLoad();
            return;
        }
        if (!Toaster.isInit()){
            Toaster.init(mApp);
        }
        if (mApp.equals(app)) {
            return;
        }
        ActivityLifecycleImpl.INSTANCE.unInit(mApp);
        mApp = app;
        ActivityLifecycleImpl.INSTANCE.init(mApp);
    }

    public static Application getApp() {
        if (mApp != null) {
            return mApp;
        } else {
            throw new NullPointerException("DawnUtil not init");
        }
    }


    ///////////////////////////////////////////////////////////////////////////
    // UtilsActivityLifecycleImpl
    ///////////////////////////////////////////////////////////////////////////
    public static Activity getTopActivity() {
        return ActivityLifecycleImpl.INSTANCE.getTopActivity();
    }

    public static void addOnAppStatusChangedListener(final OnAppStatusChangedListener listener) {
        ActivityLifecycleImpl.INSTANCE.addOnAppStatusChangedListener(listener);
    }

    public static void removeOnAppStatusChangedListener(final OnAppStatusChangedListener listener) {
        ActivityLifecycleImpl.INSTANCE.removeOnAppStatusChangedListener(listener);
    }

    public static void addActivityLifecycleCallbacks(final ActivityLifecycleCallbacks callbacks) {
        ActivityLifecycleImpl.INSTANCE.addActivityLifecycleCallbacks(callbacks);
    }

    public static void removeActivityLifecycleCallbacks(final ActivityLifecycleCallbacks callbacks) {
        ActivityLifecycleImpl.INSTANCE.removeActivityLifecycleCallbacks(callbacks);
    }

    public static void addActivityLifecycleCallbacks(final Activity activity,
                                                     final ActivityLifecycleCallbacks callbacks) {
        ActivityLifecycleImpl.INSTANCE.addActivityLifecycleCallbacks(activity, callbacks);
    }

    public static void removeActivityLifecycleCallbacks(final Activity activity) {
        ActivityLifecycleImpl.INSTANCE.removeActivityLifecycleCallbacks(activity);
    }

    public static void removeActivityLifecycleCallbacks(final Activity activity,
                                                        final ActivityLifecycleCallbacks callbacks) {
        ActivityLifecycleImpl.INSTANCE.removeActivityLifecycleCallbacks(activity, callbacks);
    }

    public static List<Activity> getActivityList() {
        return ActivityLifecycleImpl.INSTANCE.getActivityList();
    }

    public static Application getApplicationByReflect() {
        return ActivityLifecycleImpl.INSTANCE.getApplicationByReflect();
    }

    public static boolean isAppForeground() {
        return ActivityLifecycleImpl.INSTANCE.isAppForeground();
    }

    ///////////////////////////////////////////////////////////////////////////
    // ActivityUtils
    ///////////////////////////////////////////////////////////////////////////
    public static boolean isActivityAlive(final Activity activity) {
        return ActivityUtils.isActivityAlive(activity);
    }

    public static String getLauncherActivity(final String pkg) {
        return ActivityUtils.getLauncherActivity(pkg);
    }

    public static Activity getActivityByContext(Context context) {
        return ActivityUtils.getActivityByContext(context);
    }

    public static void startHomeActivity() {
        ActivityUtils.startHomeActivity();
    }

    public static void finishAllActivities() {
        ActivityUtils.finishAllActivities();
    }

    ///////////////////////////////////////////////////////////////////////////
    // AppUtils
    ///////////////////////////////////////////////////////////////////////////
    public static boolean isAppRunning(@NonNull final String pkgName) {
        return AppUtils.isAppRunning(pkgName);
    }

    public static boolean isAppInstalled(final String pkgName) {
        return AppUtils.isAppInstalled(pkgName);
    }

    public static boolean isAppDebug() {
        return AppUtils.isAppDebug();
    }

    public static void relaunchApp() {
        AppUtils.relaunchApp();
    }

    ///////////////////////////////////////////////////////////////////////////
    // ConvertUtils
    ///////////////////////////////////////////////////////////////////////////
    public static String bytes2HexString(final byte[] bytes) {
        return ConvertUtils.bytes2HexString(bytes);
    }

    public static byte[] hexString2Bytes(String hexString) {
        return ConvertUtils.hexString2Bytes(hexString);
    }

    public static byte[] string2Bytes(final String string) {
        return ConvertUtils.string2Bytes(string);
    }

    public static String bytes2String(final byte[] bytes) {
        return ConvertUtils.bytes2String(bytes,"");
    }

    public static byte[] jsonObject2Bytes(final JSONObject jsonObject) {
        return ConvertUtils.jsonObject2Bytes(jsonObject);
    }

    public static JSONObject bytes2JSONObject(final byte[] bytes) {
        return ConvertUtils.bytes2JSONObject(bytes);
    }

    public static byte[] jsonArray2Bytes(final JSONArray jsonArray) {
        return ConvertUtils.jsonArray2Bytes(jsonArray);
    }

    public static JSONArray bytes2JSONArray(final byte[] bytes) {
        return ConvertUtils.bytes2JSONArray(bytes);
    }

    public static byte[] parcelable2Bytes(final Parcelable parcelable) {
        return ConvertUtils.parcelable2Bytes(parcelable);
    }

    public static <T> T bytes2Parcelable(final byte[] bytes,
                                         final Parcelable.Creator<T> creator) {
        return ConvertUtils.bytes2Parcelable(bytes, creator);
    }

    public static byte[] serializable2Bytes(final Serializable serializable) {
        return ConvertUtils.serializable2Bytes(serializable);
    }

    public static Object bytes2Object(final byte[] bytes) {
        return ConvertUtils.bytes2Object(bytes);
    }

    public static String byte2FitMemorySize(final long byteSize) {
        return ConvertUtils.byte2FitMemorySize(byteSize);
    }

    public static byte[] inputStream2Bytes(final InputStream is) {
        return ConvertUtils.inputStream2Bytes(is);
    }

    public static ByteArrayOutputStream input2OutputStream(final InputStream is) {
        return ConvertUtils.input2OutputStream(is);
    }

    public static List<String> inputStream2Lines(final InputStream is, final String charsetName) {
        return ConvertUtils.inputStream2Lines(is, charsetName);
    }

    ///////////////////////////////////////////////////////////////////////////
    // EncodeUtils
    ///////////////////////////////////////////////////////////////////////////
    public static byte[] base64Encode(final byte[] input) {
        return EncodeUtils.base64Encode(input);
    }

    public static byte[] base64Decode(final byte[] input) {
        return EncodeUtils.base64Decode(input);
    }

    ///////////////////////////////////////////////////////////////////////////
    // EncryptUtils
    ///////////////////////////////////////////////////////////////////////////
    public static byte[] hashTemplate(final byte[] data, final String algorithm) {
        return EncryptUtils.hashTemplate(data, algorithm);
    }

    ///////////////////////////////////////////////////////////////////////////
    // FileIOUtils
    ///////////////////////////////////////////////////////////////////////////
    public static boolean writeFileFromBytes(final File file,
                                             final byte[] bytes) {
        return FileIOUtils.writeFileFromBytesByChannel(file, bytes, true);
    }

    public static byte[] readFile2Bytes(final File file) {
        return FileIOUtils.readFile2BytesByChannel(file);
    }

    public static boolean writeFileFromString(final String filePath, final String content, final boolean append) {
        return FileIOUtils.writeFileFromString(filePath, content, append);
    }

    public static boolean writeFileFromIS(final String filePath, final InputStream is) {
        return FileIOUtils.writeFileFromIS(filePath, is);
    }

    ///////////////////////////////////////////////////////////////////////////
    // FileUtils
    ///////////////////////////////////////////////////////////////////////////
    public static boolean isFileExists(final File file) {
        return FileUtils.isFileExists(file);
    }

    public static File getFileByPath(final String filePath) {
        return FileUtils.getFileByPath(filePath);
    }

    public static boolean deleteAllInDir(final File dir) {
        return FileUtils.deleteAllInDir(dir);
    }

    public static boolean createOrExistsFile(final File file) {
        return FileUtils.createOrExistsFile(file);
    }

    public static boolean createOrExistsDir(final File file) {
        return FileUtils.createOrExistsDir(file);
    }

    public static boolean createFileByDeleteOldFile(final File file) {
        return FileUtils.createFileByDeleteOldFile(file);
    }

    public static long getFsTotalSize(String path) {
        return FileUtils.getFsTotalSize(path);
    }

    public static long getFsAvailableSize(String path) {
        return FileUtils.getFsAvailableSize(path);
    }

    public static void notifySystemToScan(File file) {
        FileUtils.notifySystemToScan(file);
    }

    ///////////////////////////////////////////////////////////////////////////
    // GsonUtils
    ///////////////////////////////////////////////////////////////////////////
    public static String toJson(final Object object) {
        return GsonUtils.toJson(object);
    }

    public static <T> T fromJson(final String json, final Type type) {
        return GsonUtils.fromJson(json, type);
    }

    public static Gson getGson4LogUtils() {
        return GsonUtils.getGson4LogUtils();
    }

    ///////////////////////////////////////////////////////////////////////////
    // ImageUtils
    ///////////////////////////////////////////////////////////////////////////
    public static byte[] bitmap2Bytes(final Bitmap bitmap) {
        return ImageUtils.bitmap2Bytes(bitmap);
    }

    public static byte[] bitmap2Bytes(final Bitmap bitmap, final Bitmap.CompressFormat format, int quality) {
        return ImageUtils.bitmap2Bytes(bitmap, format, quality);
    }

    public static Bitmap bytes2Bitmap(final byte[] bytes) {
        return ImageUtils.bytes2Bitmap(bytes);
    }

    public static byte[] drawable2Bytes(final Drawable drawable) {
        return ImageUtils.drawable2Bytes(drawable);
    }

    public static byte[] drawable2Bytes(final Drawable drawable, final Bitmap.CompressFormat format, int quality) {
        return ImageUtils.drawable2Bytes(drawable, format, quality);
    }

    public static Drawable bytes2Drawable(final byte[] bytes) {
        return ImageUtils.bytes2Drawable(bytes);
    }


    public static Bitmap drawable2Bitmap(final Drawable drawable) {
        return ImageUtils.drawable2Bitmap(drawable);
    }

    public static Drawable bitmap2Drawable(final Bitmap bitmap) {
        return ImageUtils.bitmap2Drawable(bitmap);
    }

    ///////////////////////////////////////////////////////////////////////////
    // IntentUtils
    ///////////////////////////////////////////////////////////////////////////
    public static boolean isIntentAvailable(final Intent intent) {
        return IntentUtils.isIntentAvailable(intent);
    }

    public static Intent getLaunchAppIntent(final String pkgName) {
        return IntentUtils.getLaunchAppIntent(pkgName);
    }

    public static Intent getInstallAppIntent(final File file) {
        return IntentUtils.getInstallAppIntent(file);
    }

    public static Intent getInstallAppIntent(final Uri uri) {
        return IntentUtils.getInstallAppIntent(uri);
    }

    public static Intent getUninstallAppIntent(final String pkgName) {
        return IntentUtils.getUninstallAppIntent(pkgName);
    }

    public static Intent getDialIntent(final String phoneNumber) {
        return IntentUtils.getDialIntent(phoneNumber);
    }

    @RequiresPermission(CALL_PHONE)
    public static Intent getCallIntent(final String phoneNumber) {
        return IntentUtils.getCallIntent(phoneNumber);
    }

    public static Intent getSendSmsIntent(final String phoneNumber, final String content) {
        return IntentUtils.getSendSmsIntent(phoneNumber, content);
    }

    public static Intent getLaunchAppDetailsSettingsIntent(final String pkgName, final boolean isNewTask) {
        return IntentUtils.getLaunchAppDetailsSettingsIntent(pkgName, isNewTask);
    }


    ///////////////////////////////////////////////////////////////////////////
    // NotificationUtils
    ///////////////////////////////////////////////////////////////////////////
    public static Notification getNotification(ChannelConfig channelConfig,
                                               Consumer<NotificationCompat.Builder> consumer) {
        return NotificationUtils.getNotification(channelConfig, consumer);
    }

    ///////////////////////////////////////////////////////////////////////////
    // PermissionUtils
    ///////////////////////////////////////////////////////////////////////////
    public static boolean isGranted(final String permissions) {
//        return PermissionL.Companion.isGranted(DawnUtil.getApp(), permissions);
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static boolean isGrantedDrawOverlays() {
        return Settings.canDrawOverlays(getApp());
    }

    ///////////////////////////////////////////////////////////////////////////
    // ProcessUtils
    ///////////////////////////////////////////////////////////////////////////
    public static boolean isMainProcess() {
        return ProcessUtils.isMainProcess();
    }

    public static String getForegroundProcessName() {
        return ProcessUtils.getForegroundProcessName();
    }

    public static String getCurrentProcessName() {
        return ProcessUtils.getCurrentProcessName();
    }

    ///////////////////////////////////////////////////////////////////////////
    // RomUtils
    ///////////////////////////////////////////////////////////////////////////
    public static boolean isSamsung() {
        return RomUtils.isSamsung();
    }

    ///////////////////////////////////////////////////////////////////////////
    // ScreenUtils
    ///////////////////////////////////////////////////////////////////////////
    public static int getAppScreenWidth() {
        return ScreenUtils.getAppScreenWidth();
    }

    ///////////////////////////////////////////////////////////////////////////
    // SDCardUtils
    ///////////////////////////////////////////////////////////////////////////
    public static boolean isSDCardEnableByEnvironment() {
        return SDCardUtils.isSDCardEnableByEnvironment();
    }

    ///////////////////////////////////////////////////////////////////////////
    // ServiceUtils
    ///////////////////////////////////////////////////////////////////////////
    public static boolean isServiceRunning(final String className) {
        return ServiceUtils.isServiceRunning(className);
    }

    ///////////////////////////////////////////////////////////////////////////
    // ShellUtils
    ///////////////////////////////////////////////////////////////////////////
    public static CommandResult execCmd(final String command, final boolean isRooted) {
        return ShellUtils.execCmd(command, isRooted);
    }

    ///////////////////////////////////////////////////////////////////////////
    // SizeUtils
    ///////////////////////////////////////////////////////////////////////////
    public static int dp2px(final float dpValue) {
        return SizeUtils.dp2px(dpValue);
    }

    public static int px2dp(final float pxValue) {
        return SizeUtils.px2dp(pxValue);
    }

    public static int sp2px(final float spValue) {
        return SizeUtils.sp2px(spValue);
    }

    public static int px2sp(final float pxValue) {
        return SizeUtils.px2sp(pxValue);
    }

    ///////////////////////////////////////////////////////////////////////////
    // StringUtils
    ///////////////////////////////////////////////////////////////////////////
    public static boolean isSpace(final String s) {
        return StringUtils.isSpace(s);
    }

    public static boolean equals(final CharSequence s1, final CharSequence s2) {
        return StringUtils.equals(s1, s2);
    }

    public static String getString(@StringRes int id) {
        return StringUtils.getString(id);
    }

    public static String getString(@StringRes int id, Object... formatArgs) {
        return StringUtils.getString(id, formatArgs);
    }

    public static String format(@Nullable String str, Object... args) {
        return StringUtils.format(str, args);
    }


    ///////////////////////////////////////////////////////////////////////////
    // ThreadUtils
    ///////////////////////////////////////////////////////////////////////////
    public static <T> Task<T> doAsync(final Task<T> task) {
        ThreadUtils.getCachedPool().execute(task);
        return task;
    }

    public static void runOnUiThread(final Runnable runnable) {
        ThreadUtils.runOnUiThread(runnable);
    }

    public static void runOnUiThreadDelayed(final Runnable runnable, long delayMillis) {
        ThreadUtils.runOnUiThreadDelayed(runnable, delayMillis);
    }

    ///////////////////////////////////////////////////////////////////////////
    // ThrowableUtils
    ///////////////////////////////////////////////////////////////////////////
    public static String getFullStackTrace(Throwable throwable) {
        return ThrowableUtils.getFullStackTrace(throwable);
    }

    ///////////////////////////////////////////////////////////////////////////
    // TimeUtils
    ///////////////////////////////////////////////////////////////////////////
    public static String millis2FitTimeSpan(long millis, int precision) {
        return TimeUtils.millis2FitTimeSpan(millis, precision);
    }


    public static void preLoad(final Runnable... runs) {
        for (final Runnable r : runs) {
            ThreadUtils.getCachedPool().execute(r);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // UriUtils
    ///////////////////////////////////////////////////////////////////////////
    public static Uri file2Uri(final File file) {
        return UriUtils.file2Uri(file);
    }

    public static File uri2File(final Uri uri) {
        return UriUtils.uri2File(uri);
    }



    public static Class getClassName(String paramType) throws ClassNotFoundException {
        switch (paramType) {
            case "boolean":
                return boolean.class;
            case "int":
                return int.class;
            case "long":
                return long.class;
            case "short":
                return short.class;
            case "byte":
                return byte.class;
            case "double":
                return double.class;
            case "float":
                return float.class;
            case "char":
                return char.class;
            default:
                return Class.forName(paramType);
        }
    }

    public abstract static class Task<Result> extends SimpleTask<Result> {

        private Consumer<Result> mConsumer;

        public Task(final Consumer<Result> consumer) {
            mConsumer = consumer;
        }

        @Override
        public void onSuccess(Result result) {
            if (mConsumer != null) {
                mConsumer.accept(result);
            }
        }
    }


    public interface Consumer<T> {
        void accept(T t);
    }

    public interface Supplier<T> {
        T get();
    }

    public interface Func1<Ret, Par> {
        Ret call(Par param);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Common
    ///////////////////////////////////////////////////////////////////////////
    public static final class FileHead {

        private final String mName;
        private final LinkedHashMap<String, String> mFirst = new LinkedHashMap<>();
        private final LinkedHashMap<String, String> mLast = new LinkedHashMap<>();

        public FileHead(String name) {
            mName = name;
        }

        public void addFirst(String key, String value) {
            append2Host(mFirst, key, value);
        }

        public void append(Map<String, String> extra) {
            append2Host(mLast, extra);
        }

        public void append(String key, String value) {
            append2Host(mLast, key, value);
        }

        private void append2Host(Map<String, String> host, Map<String, String> extra) {
            if (extra == null || extra.isEmpty()) {
                return;
            }
            for (Map.Entry<String, String> entry : extra.entrySet()) {
                append2Host(host, entry.getKey(), entry.getValue());
            }
        }

        private void append2Host(Map<String, String> host, String key, String value) {
            if (TextUtils.isEmpty(key) || TextUtils.isEmpty(value)) {
                return;
            }
            int delta = 19 - key.length(); // 19 is length of "Device Manufacturer"
            if (delta > 0) {
                key = key + "                   ".substring(0, delta);
            }
            host.put(key, value);
        }

        public String getAppended() {
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<String, String> entry : mLast.entrySet()) {
                sb.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
            }
            return sb.toString();
        }

        @NonNull
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            String border = "************* " + mName + " Head ****************\n";
            sb.append(border);
            for (Map.Entry<String, String> entry : mFirst.entrySet()) {
                sb.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
            }

            sb.append("Rom Info           : ").append(RomUtils.getRomInfo()).append("\n");
            sb.append("Device Manufacturer: ").append(Build.MANUFACTURER).append("\n");
            sb.append("Device Model       : ").append(Build.MODEL).append("\n");
            sb.append("Android Version    : ").append(Build.VERSION.RELEASE).append("\n");
            sb.append("Android SDK        : ").append(Build.VERSION.SDK_INT).append("\n");
            sb.append("App VersionName    : ").append(AppUtils.getAppVersionName()).append("\n");
            sb.append("App VersionCode    : ").append(AppUtils.getAppVersionCode()).append("\n");

            sb.append(getAppended());
            return sb.append(border).append("\n").toString();
        }
    }
}
