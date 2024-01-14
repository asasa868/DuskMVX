package com.lzq.dawn.util.intent;

import static android.Manifest.permission.CALL_PHONE;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresPermission;
import androidx.core.content.FileProvider;

import com.lzq.dawn.DawnBridge;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @Name :IntentDawnBridge
 * @Time :2022/8/2 10:34
 * @Author :  Lzq
 * @Desc : intent
 */
public class IntentUtils {
    private IntentUtils() {
    }

    /**
     * 返回意图是否可用。
     *
     * @param intent intent.
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isIntentAvailable(final Intent intent) {
        return DawnBridge.getApp()
                .getPackageManager()
                .queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
                .size() > 0;
    }

    /**
     * 返回安装应用程序的意图。
     * <p>大于 25 的目标 API 必须持有
     * {@code <uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES" />}</p>
     *
     * @param filePath 文件的路径。
     * @return 安装应用程序的意图
     */
    public static Intent getInstallAppIntent(final String filePath) {
        return getInstallAppIntent(DawnBridge.getFileByPath(filePath));
    }


    /**
     * 返回安装应用程序的意图。
     * * <p>大于 25 的目标 API 必须持有
     * {@code <uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES" />}</p>
     *
     * @param file file.
     * @return 安装应用程序的意图
     */
    public static Intent getInstallAppIntent(final File file) {
        if (!DawnBridge.isFileExists(file)) {
            return null;
        }
        Uri uri;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            uri = Uri.fromFile(file);
        } else {
            String authority = DawnBridge.getApp().getPackageName() + ".fileprovider";
            uri = FileProvider.getUriForFile(DawnBridge.getApp(), authority, file);
        }
        return getInstallAppIntent(uri);
    }

    /**
     * 返回安装应用程序的意图。
     * <p>大于 25 的目标 API 必须持有
     * {@code <uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES" />}</p>
     *
     * @param uri uri.
     * @return 安装应用程序的意图。
     */
    public static Intent getInstallAppIntent(final Uri uri) {
        if (uri == null) {
            return null;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        String type = "application/vnd.android.package-archive";
        intent.setDataAndType(uri, type);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        return intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    /**
     * 返回卸载应用程序的意图。
     * <p>大于 25 的目标 API 必须持有
     * Must hold {@code <uses-permission android:name="android.permission.REQUEST_DELETE_PACKAGES" />}</p>
     *
     * @param pkgName 包名
     * @return v
     */
    public static Intent getUninstallAppIntent(final String pkgName) {
        Intent intent = new Intent(Intent.ACTION_DELETE);
        intent.setData(Uri.parse("package:" + pkgName));
        return intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    /**
     * 返回启动应用程序的意图。
     *
     * @param pkgName 包名
     * @return 返回启动应用程序的意图。
     */
    public static Intent getLaunchAppIntent(final String pkgName) {
        String launcherActivity = DawnBridge.getLauncherActivity(pkgName);
        if (DawnBridge.isSpace(launcherActivity)) {
            return null;
        }
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setClassName(pkgName, launcherActivity);
        return intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    /**
     * 返回启动应用详细信息设置的意图。
     *
     * @param pkgName 包名
     * @return 返回启动应用详细信息设置的意图。
     */
    public static Intent getLaunchAppDetailsSettingsIntent(final String pkgName) {
        return getLaunchAppDetailsSettingsIntent(pkgName, false);
    }


    /**
     * 返回启动应用详细信息设置的意图。
     *
     * @param pkgName 包名
     * @return 返回启动应用详细信息设置的意图
     */
    public static Intent getLaunchAppDetailsSettingsIntent(final String pkgName, final boolean isNewTask) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + pkgName));
        return getIntent(intent, isNewTask);
    }

    /**
     * 返回分享文本的意图。
     *
     * @param content content.
     * @return 返回分享文本的意图。
     */
    public static Intent getShareTextIntent(final String content) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, content);
        intent = Intent.createChooser(intent, "");
        return getIntent(intent, true);
    }

    /**
     * 返回分享图片的意图。
     *
     * @param imagePath 图像的路径。
     * @return 分享图片的意图。
     */
    public static Intent getShareImageIntent(final String imagePath) {
        return getShareTextImageIntent("", imagePath);
    }

    /**
     * 返回分享图片的意图。
     *
     * @param imageFile 图像的文件
     * @return 分享图片的意图。
     */
    public static Intent getShareImageIntent(final File imageFile) {
        return getShareTextImageIntent("", imageFile);
    }

    /**
     * 返回分享图片的意图
     *
     * @param imageUri 图片的uri
     * @return 返回分享图片的意图
     */
    public static Intent getShareImageIntent(final Uri imageUri) {
        return getShareTextImageIntent("", imageUri);
    }

    /**
     * 返回分享图片的意图
     *
     * @param content   content.
     * @param imagePath 图像的路径。
     * @return 返回分享图片的意图
     */
    public static Intent getShareTextImageIntent(@Nullable final String content, final String imagePath) {
        return getShareTextImageIntent(content, DawnBridge.getFileByPath(imagePath));
    }

    /**
     * 返回分享图片的意图
     *
     * @param content   content.
     * @param imageFile 图像的文件
     * @return 返回分享图片的意图
     */
    public static Intent getShareTextImageIntent(@Nullable final String content, final File imageFile) {
        return getShareTextImageIntent(content, DawnBridge.file2Uri(imageFile));
    }

    /**
     * 返回分享图片的意图
     *
     * @param content  content.
     * @param imageUri 图片的uri
     * @return 返回分享图片的意图
     */
    public static Intent getShareTextImageIntent(@Nullable final String content, final Uri imageUri) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, content);
        intent.putExtra(Intent.EXTRA_STREAM, imageUri);
        intent.setType("image/*");
        intent = Intent.createChooser(intent, "");
        return getIntent(intent, true);
    }

    /**
     * 返回分享图片的意图
     *
     * @param imagePaths 图片的路径
     * @return 返回分享图片的意图
     */
    public static Intent getShareImageIntent(final LinkedList<String> imagePaths) {
        return getShareTextImageIntent("", imagePaths);
    }

    /**
     * 返回分享图片的意图
     *
     * @param images 图片的文件
     * @return 返回分享图片的意图
     */
    public static Intent getShareImageIntent(final List<File> images) {
        return getShareTextImageIntent("", images);
    }

    /**
     * 返回分享图片的意图
     *
     * @param uris 图片的uri
     * @return 返回分享图片的意图
     */
    public static Intent getShareImageIntent(final ArrayList<Uri> uris) {
        return getShareTextImageIntent("", uris);
    }

    /**
     * 返回分享图片的意图
     *
     * @param content    content.
     * @param imagePaths 图像的路径。
     * @return 返回分享图片的意图
     */
    public static Intent getShareTextImageIntent(@Nullable final String content,
                                                 final LinkedList<String> imagePaths) {
        List<File> files = new ArrayList<>();
        if (imagePaths != null) {
            for (String imagePath : imagePaths) {
                File file = DawnBridge.getFileByPath(imagePath);
                if (file != null) {
                    files.add(file);
                }
            }
        }
        return getShareTextImageIntent(content, files);
    }

    /**
     * 返回分享图片的意图
     *
     * @param content content.
     * @param images  图片的文件
     * @return 返回分享图片的意图
     */
    public static Intent getShareTextImageIntent(@Nullable final String content, final List<File> images) {
        ArrayList<Uri> uris = new ArrayList<>();
        if (images != null) {
            for (File image : images) {
                Uri uri = DawnBridge.file2Uri(image);
                if (uri != null) {
                    uris.add(uri);
                }
            }
        }
        return getShareTextImageIntent(content, uris);
    }

    /**
     * 返回分享图片的意图
     *
     * @param content content.
     * @param uris    图片的uri
     * @return 返回分享图片的意图
     */
    public static Intent getShareTextImageIntent(@Nullable final String content, final ArrayList<Uri> uris) {
        Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        intent.putExtra(Intent.EXTRA_TEXT, content);
        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
        intent.setType("image/*");
        intent = Intent.createChooser(intent, "");
        return getIntent(intent, true);
    }

    /**
     * 返回组件的意图
     *
     * @param pkgName   包名
     * @param className 类名
     * @return 返回组件的意图
     */
    public static Intent getComponentIntent(final String pkgName, final String className) {
        return getComponentIntent(pkgName, className, null, false);
    }

    /**
     * 返回组件的意图
     *
     * @param pkgName   包名
     * @param className 类名
     * @param isNewTask True 添加新任务的标志，否则为 false。
     * @return 返回组件的意图
     */
    public static Intent getComponentIntent(final String pkgName,
                                            final String className,
                                            final boolean isNewTask) {
        return getComponentIntent(pkgName, className, null, isNewTask);
    }

    /**
     * 返回组件的意图
     *
     * @param pkgName   包名
     * @param className 类名
     * @param bundle    附加到此意图的附加组件。
     * @return 返回组件的意图
     */
    public static Intent getComponentIntent(final String pkgName,
                                            final String className,
                                            final Bundle bundle) {
        return getComponentIntent(pkgName, className, bundle, false);
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
    public static Intent getComponentIntent(final String pkgName,
                                            final String className,
                                            final Bundle bundle,
                                            final boolean isNewTask) {
        Intent intent = new Intent();
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        ComponentName cn = new ComponentName(pkgName, className);
        intent.setComponent(cn);
        return getIntent(intent, isNewTask);
    }

    /**
     * 返回关闭的意图。
     * <p>需要root权限或持有 {@code android:sharedUserId="android.uid.system"},
     * {@code <uses-permission android:name="android.permission.SHUTDOWN" />}
     * in manifest.</p>
     *
     * @return 返回关闭的意图
     */
    public static Intent getShutdownIntent() {
        Intent intent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            intent = new Intent("com.android.internal.intent.action.REQUEST_SHUTDOWN");
        } else {
            intent = new Intent("android.intent.action.ACTION_REQUEST_SHUTDOWN");
        }
        intent.putExtra("android.intent.extra.KEY_CONFIRM", false);
        return intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    /**
     * 返回拨号的意图。
     *
     * @param phoneNumber 手机号
     * @return 返回拨号的意图。
     */
    public static Intent getDialIntent(@NonNull final String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + Uri.encode(phoneNumber)));
        return getIntent(intent, true);
    }

    /**
     * 返回通话的意图。
     * <p>必须持有{@code <uses-permission android:name="android.permission.CALL_PHONE" />}</p>
     *
     * @param phoneNumber phoneNumber
     * @return 通话的意图
     */
    @RequiresPermission(CALL_PHONE)
    public static Intent getCallIntent(@NonNull final String phoneNumber) {
        Intent intent = new Intent("android.intent.action.CALL", Uri.parse("tel:" + Uri.encode(phoneNumber)));
        return getIntent(intent, true);
    }

    /**
     * 返回发送短信的意图。
     *
     * @param phoneNumber 手机号
     * @param content     内容
     * @return 返回发送短信的意图。
     */
    public static Intent getSendSmsIntent(@NonNull final String phoneNumber, final String content) {
        Uri uri = Uri.parse("smsto:" + Uri.encode(phoneNumber));
        Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
        intent.putExtra("sms_body", content);
        return getIntent(intent, true);
    }

    /**
     * 返回捕获的意图。
     *
     * @param outUri 输出的uri。
     * @return 返回捕获的意图
     */
    public static Intent getCaptureIntent(final Uri outUri) {
        return getCaptureIntent(outUri, false);
    }

    /**
     * 返回捕获的意图。
     *
     * @param outUri    输出的uri
     * @param isNewTask True 添加新任务的标志，否则为 false。
     * @return 返回捕获的意图
     */
    public static Intent getCaptureIntent(final Uri outUri, final boolean isNewTask) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outUri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        return getIntent(intent, isNewTask);
    }

    private static Intent getIntent(final Intent intent, final boolean isNewTask) {
        return isNewTask ? intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) : intent;
    }


}
