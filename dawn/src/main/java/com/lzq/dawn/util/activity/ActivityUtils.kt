package com.lzq.dawn.util.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.annotation.AnimRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;

import com.lzq.dawn.DawnBridge;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * className :ActivityUtils
 * createTime :2022/7/8 15:51
 *
 * @Author :  Lzq
 */
public final class ActivityUtils {

    private ActivityUtils() {
    }

    /**
     * Add callbacks of activity lifecycle.
     *
     * @param callbacks The callbacks.
     */
    public static void addActivityLifecycleCallbacks(@Nullable final ActivityLifecycleCallbacks callbacks) {
        DawnBridge.addActivityLifecycleCallbacks(callbacks);
    }

    /**
     * Add callbacks of activity lifecycle.
     *
     * @param activity  The activity.
     * @param callbacks The callbacks.
     */
    public static void addActivityLifecycleCallbacks(@Nullable final Activity activity,
                                                     @Nullable final ActivityLifecycleCallbacks callbacks) {
        DawnBridge.addActivityLifecycleCallbacks(activity, callbacks);
    }

    /**
     * Remove callbacks of activity lifecycle.
     *
     * @param callbacks The callbacks.
     */
    public static void removeActivityLifecycleCallbacks(@Nullable final ActivityLifecycleCallbacks callbacks) {
        DawnBridge.removeActivityLifecycleCallbacks(callbacks);
    }

    /**
     * Remove callbacks of activity lifecycle.
     *
     * @param activity The activity.
     */
    public static void removeActivityLifecycleCallbacks(@Nullable final Activity activity) {
        DawnBridge.removeActivityLifecycleCallbacks(activity);
    }

    /**
     * Remove callbacks of activity lifecycle.
     *
     * @param activity  The activity.
     * @param callbacks The callbacks.
     */
    public static void removeActivityLifecycleCallbacks(@Nullable final Activity activity,
                                                        @Nullable final ActivityLifecycleCallbacks callbacks) {
        DawnBridge.removeActivityLifecycleCallbacks(activity, callbacks);
    }

    /**
     * 返回活动是否存在。
     *
     * @param pkg 包名
     * @param cls activity类名.
     * @return {@code true}: 是<br>{@code false}: 否
     */
    public static boolean isActivityExists(@NonNull final String pkg,
                                           @NonNull final String cls) {
        Intent intent = new Intent();
        intent.setClassName(pkg, cls);
        PackageManager pm = DawnBridge.getApp().getPackageManager();
        return !(pm.resolveActivity(intent, 0) == null ||
                intent.resolveActivity(pm) == null ||
                pm.queryIntentActivities(intent, 0).size() == 0);
    }

    /**
     * 前往 activity
     *
     * @param clz 目标activity .
     */
    public static void startActivity(@NonNull final Class<? extends Activity> clz) {
        Context context = getTopActivityOrApp();
        startActivity(context, null, context.getPackageName(), clz.getName(), null);
    }

    /**
     * 前往 activity
     *
     * @param clz     目标activity .
     * @param options 跳转的动画. 使用{@link ActivityOptionsCompat}.toBundle 生成所需要的bundle
     */
    public static void startActivity(@NonNull final Class<? extends Activity> clz,
                                     @Nullable final Bundle options) {
        Context context = getTopActivityOrApp();
        startActivity(context, null, context.getPackageName(), clz.getName(), options);
    }

    /**
     * 前往 activity
     *
     * @param clz       activity class.
     * @param enterAnim 进入动画 使用r.anim.xxx 的资源
     * @param exitAnim  退出动画 使用r.anim.xxx 的资源
     */
    public static void startActivity(@NonNull final Class<? extends Activity> clz,
                                     @AnimRes final int enterAnim,
                                     @AnimRes final int exitAnim) {
        Context context = getTopActivityOrApp();
        startActivity(context, null, context.getPackageName(), clz.getName(),
                getOptionsBundle(context, enterAnim, exitAnim));
    }


    /**
     * 前往 activity
     *
     * @param activity activity.
     * @param clz      目标activity.class.
     */
    public static void startActivity(@NonNull final Activity activity,
                                     @NonNull final Class<? extends Activity> clz) {
        startActivity(activity, null, activity.getPackageName(), clz.getName(), null);
    }

    /**
     * 前往 activity
     *
     * @param activity activity.
     * @param clz      目标activity.class.
     * @param options  跳转的动画. 使用{@link ActivityOptionsCompat}.toBundle 生成所需要的bundle
     */
    public static void startActivity(@NonNull final Activity activity,
                                     @NonNull final Class<? extends Activity> clz,
                                     @Nullable final Bundle options) {
        startActivity(activity, null, activity.getPackageName(), clz.getName(), options);
    }

    /**
     * 前往 activity
     *
     * @param activity       activity.
     * @param clz            目标activity.class.
     * @param sharedElements 和目标的view联动的动画 比如平移啥的
     */
    public static void startActivity(@NonNull final Activity activity,
                                     @NonNull final Class<? extends Activity> clz,
                                     final View... sharedElements) {
        startActivity(activity, null, activity.getPackageName(), clz.getName(),
                getOptionsBundle(activity, sharedElements));
    }

    /**
     * 前往 activity
     *
     * @param activity  activity.
     * @param clz       目标activity class.
     * @param enterAnim 进入动画 使用r.anim.xxx 的资源
     * @param exitAnim  退出动画 使用r.anim.xxx 的资源
     */
    public static void startActivity(@NonNull final Activity activity,
                                     @NonNull final Class<? extends Activity> clz,
                                     @AnimRes final int enterAnim,
                                     @AnimRes final int exitAnim) {
        startActivity(activity, null, activity.getPackageName(), clz.getName(),
                getOptionsBundle(activity, enterAnim, exitAnim));
    }

    /**
     * 前往 activity
     *
     * @param extras 携带跳转的bundle数据.
     * @param clz    目标activity class.
     */
    public static void startActivity(@NonNull final Bundle extras,
                                     @NonNull final Class<? extends Activity> clz) {
        Context context = getTopActivityOrApp();
        startActivity(context, extras, context.getPackageName(), clz.getName(), null);
    }

    /**
     * 前往 activity
     *
     * @param extras  携带的bundle数据
     * @param clz     目标activity class.
     * @param options 跳转的动画. 使用{@link ActivityOptionsCompat}.toBundle 生成所需要的bundle
     */
    public static void startActivity(@NonNull final Bundle extras,
                                     @NonNull final Class<? extends Activity> clz,
                                     @Nullable final Bundle options) {
        Context context = getTopActivityOrApp();
        startActivity(context, extras, context.getPackageName(), clz.getName(), options);
    }

    /**
     * 前往 activity
     *
     * @param extras    携带的bundle数据
     * @param clz       目标activity class.
     * @param enterAnim 进入动画 使用r.anim.xxx 的资源
     * @param exitAnim  退出动画 使用r.anim.xxx 的资源
     */
    public static void startActivity(@NonNull final Bundle extras,
                                     @NonNull final Class<? extends Activity> clz,
                                     @AnimRes final int enterAnim,
                                     @AnimRes final int exitAnim) {
        Context context = getTopActivityOrApp();
        startActivity(context, extras, context.getPackageName(), clz.getName(),
                getOptionsBundle(context, enterAnim, exitAnim));
    }

    /**
     * 前往 activity
     *
     * @param extras   携带的bundle数据
     * @param activity 当前activity.
     * @param clz      目标activity class.
     */
    public static void startActivity(@NonNull final Bundle extras,
                                     @NonNull final Activity activity,
                                     @NonNull final Class<? extends Activity> clz) {
        startActivity(activity, extras, activity.getPackageName(), clz.getName(), null);
    }

    /**
     * 前往 activity
     *
     * @param extras   携带的bundle数据
     * @param activity 当前activity.
     * @param clz      目标activity class.
     * @param options  跳转的动画. 使用{@link ActivityOptionsCompat}.toBundle 生成所需要的bundle
     */
    public static void startActivity(@NonNull final Bundle extras,
                                     @NonNull final Activity activity,
                                     @NonNull final Class<? extends Activity> clz,
                                     @Nullable final Bundle options) {
        startActivity(activity, extras, activity.getPackageName(), clz.getName(), options);
    }

    /**
     * 前往 activity
     *
     * @param activity       activity.
     * @param clz            目标activity.class.
     * @param sharedElements 和目标的view联动的动画 比如平移啥的
     * @param extras         携带的bundle数据
     */
    public static void startActivity(@NonNull final Bundle extras,
                                     @NonNull final Activity activity,
                                     @NonNull final Class<? extends Activity> clz,
                                     final View... sharedElements) {
        startActivity(activity, extras, activity.getPackageName(), clz.getName(),
                getOptionsBundle(activity, sharedElements));
    }

    /**
     * 前往 activity
     *
     * @param extras    携带的bundle数据
     * @param activity  activity.
     * @param clz       目标activity.class.
     * @param enterAnim 进入动画 使用r.anim.xxx 的资源
     * @param exitAnim  退出动画 使用r.anim.xxx 的资源
     */
    public static void startActivity(@NonNull final Bundle extras,
                                     @NonNull final Activity activity,
                                     @NonNull final Class<? extends Activity> clz,
                                     @AnimRes final int enterAnim,
                                     @AnimRes final int exitAnim) {
        startActivity(activity, extras, activity.getPackageName(), clz.getName(),
                getOptionsBundle(activity, enterAnim, exitAnim));
    }

    /**
     * 前往 activity
     *
     * @param pkg 包名
     * @param cls 目标activity.class.
     */
    public static void startActivity(@NonNull final String pkg,
                                     @NonNull final String cls) {
        startActivity(getTopActivityOrApp(), null, pkg, cls, null);
    }

    /**
     * 前往 activity
     *
     * @param pkg     包名
     * @param cls     目标activity.class.
     * @param options 跳转的动画. 使用{@link ActivityOptionsCompat}.toBundle 生成所需要的bundle
     */
    public static void startActivity(@NonNull final String pkg,
                                     @NonNull final String cls,
                                     @Nullable final Bundle options) {
        startActivity(getTopActivityOrApp(), null, pkg, cls, options);
    }

    /**
     * 前往 activity
     *
     * @param pkg       包名
     * @param cls       目标activity.class.
     * @param enterAnim 进入动画 使用r.anim.xxx 的资源
     * @param exitAnim  退出动画 使用r.anim.xxx 的资源
     */
    public static void startActivity(@NonNull final String pkg,
                                     @NonNull final String cls,
                                     @AnimRes final int enterAnim,
                                     @AnimRes final int exitAnim) {
        Context context = getTopActivityOrApp();
        startActivity(context, null, pkg, cls, getOptionsBundle(context, enterAnim, exitAnim));
    }

    /**
     * 前往 activity
     *
     * @param pkg      包名
     * @param cls      目标activity.class.
     * @param activity 当前 activity.
     */
    public static void startActivity(@NonNull final Activity activity,
                                     @NonNull final String pkg,
                                     @NonNull final String cls) {
        startActivity(activity, null, pkg, cls, null);
    }

    /**
     * 前往 activity
     *
     * @param pkg      包名
     * @param cls      目标activity.class.
     * @param activity 当前 activity.
     * @param options  跳转的动画. 使用{@link ActivityOptionsCompat#toBundle()} 生成所需要的bundle
     */
    public static void startActivity(@NonNull final Activity activity,
                                     @NonNull final String pkg,
                                     @NonNull final String cls,
                                     @Nullable final Bundle options) {
        startActivity(activity, null, pkg, cls, options);
    }


    /**
     * 前往 activity
     *
     * @param activity       当前 activity.
     * @param pkg            包名
     * @param cls            目标activity.class.
     * @param sharedElements 和目标的view联动的动画 比如平移啥的
     */
    public static void startActivity(@NonNull final Activity activity,
                                     @NonNull final String pkg,
                                     @NonNull final String cls,
                                     final View... sharedElements) {
        startActivity(activity, null, pkg, cls, getOptionsBundle(activity, sharedElements));
    }

    /**
     * 前往 activity
     *
     * @param activity  当前 activity.
     * @param pkg       包名
     * @param cls       目标activity.class.
     * @param enterAnim 进入动画 使用r.anim.xxx 的资源
     * @param exitAnim  退出动画 使用r.anim.xxx 的资源
     */
    public static void startActivity(@NonNull final Activity activity,
                                     @NonNull final String pkg,
                                     @NonNull final String cls,
                                     @AnimRes final int enterAnim,
                                     @AnimRes final int exitAnim) {
        startActivity(activity, null, pkg, cls, getOptionsBundle(activity, enterAnim, exitAnim));
    }

    /**
     * 前往 activity
     *
     * @param extras 携带的bundle数据
     * @param pkg    包名
     * @param cls    目标activity.class.
     */
    public static void startActivity(@NonNull final Bundle extras,
                                     @NonNull final String pkg,
                                     @NonNull final String cls) {
        startActivity(getTopActivityOrApp(), extras, pkg, cls, null);
    }

    /**
     * 前往 activity
     *
     * @param extras  携带的bundle数据
     * @param pkg     包名
     * @param cls     目标activity.class.
     * @param options 跳转的动画. 使用{@link ActivityOptionsCompat#toBundle()} 生成所需要的bundle
     */
    public static void startActivity(@NonNull final Bundle extras,
                                     @NonNull final String pkg,
                                     @NonNull final String cls,
                                     @Nullable final Bundle options) {
        startActivity(getTopActivityOrApp(), extras, pkg, cls, options);
    }

    /**
     * 前往 activity
     *
     * @param extras    携带的bundle数据
     * @param pkg       包名
     * @param cls       目标activity.class.
     * @param enterAnim 进入动画 使用r.anim.xxx 的资源
     * @param exitAnim  退出动画 使用r.anim.xxx 的资源
     */
    public static void startActivity(@NonNull final Bundle extras,
                                     @NonNull final String pkg,
                                     @NonNull final String cls,
                                     @AnimRes final int enterAnim,
                                     @AnimRes final int exitAnim) {
        Context context = getTopActivityOrApp();
        startActivity(context, extras, pkg, cls, getOptionsBundle(context, enterAnim, exitAnim));
    }

    /**
     * 前往 activity
     *
     * @param activity activity.
     * @param extras   携带的bundle数据
     * @param pkg      包名
     * @param cls      目标activity.class.
     */
    public static void startActivity(@NonNull final Bundle extras,
                                     @NonNull final Activity activity,
                                     @NonNull final String pkg,
                                     @NonNull final String cls) {
        startActivity(activity, extras, pkg, cls, null);
    }

    /**
     * 前往 activity
     *
     * @param activity activity.
     * @param extras   携带的bundle数据
     * @param pkg      包名
     * @param cls      目标activity.class.
     * @param options  跳转的动画. 使用{@link ActivityOptionsCompat#toBundle()} 生成所需要的bundle
     */
    public static void startActivity(@NonNull final Bundle extras,
                                     @NonNull final Activity activity,
                                     @NonNull final String pkg,
                                     @NonNull final String cls,
                                     @Nullable final Bundle options) {
        startActivity(activity, extras, pkg, cls, options);
    }

    /**
     * 前往 activity
     *
     * @param extras         携带的bundle数据
     * @param activity       当前 activity.
     * @param pkg            包名
     * @param cls            目标activity.class.
     * @param sharedElements 和目标的view联动的动画 比如平移啥的
     */
    public static void startActivity(@NonNull final Bundle extras,
                                     @NonNull final Activity activity,
                                     @NonNull final String pkg,
                                     @NonNull final String cls,
                                     final View... sharedElements) {
        startActivity(activity, extras, pkg, cls, getOptionsBundle(activity, sharedElements));
    }

    /**
     * 前往 activity
     *
     * @param extras    携带的bundle数据
     * @param activity  当前 activity.
     * @param pkg       包名
     * @param cls       目标activity.class.
     * @param enterAnim 进入动画 使用r.anim.xxx 的资源
     * @param exitAnim  退出动画 使用r.anim.xxx 的资源
     */
    public static void startActivity(@NonNull final Bundle extras,
                                     @NonNull final Activity activity,
                                     @NonNull final String pkg,
                                     @NonNull final String cls,
                                     @AnimRes final int enterAnim,
                                     @AnimRes final int exitAnim) {
        startActivity(activity, extras, pkg, cls, getOptionsBundle(activity, enterAnim, exitAnim));
    }

    /**
     * 前往 activity
     *
     * @param intent intent
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean startActivity(@NonNull final Intent intent) {
        return startActivity(intent, getTopActivityOrApp(), null);
    }

    /**
     * 前往 activity
     *
     * @param intent  intent
     * @param options 跳转的动画. 使用{@link ActivityOptionsCompat#toBundle()} 生成所需要的bundle
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean startActivity(@NonNull final Intent intent,
                                        @Nullable final Bundle options) {
        return startActivity(intent, getTopActivityOrApp(), options);
    }

    /**
     * 前往 activity
     *
     * @param intent    intent
     * @param enterAnim 进入动画 使用r.anim.xxx 的资源
     * @param exitAnim  退出动画 使用r.anim.xxx 的资源
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean startActivity(@NonNull final Intent intent,
                                        @AnimRes final int enterAnim,
                                        @AnimRes final int exitAnim) {
        Context context = getTopActivityOrApp();
        return startActivity(intent, context, getOptionsBundle(context, enterAnim, exitAnim));
    }

    /**
     * 前往 activity
     *
     * @param activity activity.
     * @param intent   intent
     */
    public static void startActivity(@NonNull final Activity activity,
                                     @NonNull final Intent intent) {
        startActivity(intent, activity, null);
    }

    /**
     * 前往 activity
     *
     * @param activity activity.
     * @param intent   intent
     * @param options  跳转的动画. 使用{@link ActivityOptionsCompat#toBundle()} 生成所需要的bundle
     */
    public static void startActivity(@NonNull final Activity activity,
                                     @NonNull final Intent intent,
                                     @Nullable final Bundle options) {
        startActivity(intent, activity, options);
    }

    /**
     * 前往 activity
     *
     * @param activity       当前 activity.
     * @param intent         intent
     * @param sharedElements 和目标的view联动的动画 比如平移啥的
     */
    public static void startActivity(@NonNull final Activity activity,
                                     @NonNull final Intent intent,
                                     final View... sharedElements) {
        startActivity(intent, activity, getOptionsBundle(activity, sharedElements));
    }

    /**
     * 前往 activity
     *
     * @param activity  activity.
     * @param intent    intent
     * @param enterAnim 进入动画 使用r.anim.xxx 的资源
     * @param exitAnim  退出动画 使用r.anim.xxx 的资源
     */
    public static void startActivity(@NonNull final Activity activity,
                                     @NonNull final Intent intent,
                                     @AnimRes final int enterAnim,
                                     @AnimRes final int exitAnim) {
        startActivity(intent, activity, getOptionsBundle(activity, enterAnim, exitAnim));
    }

    /**
     * 前往 activity
     *
     * @param activity    activity.
     * @param clz         目标activity.class
     * @param requestCode 如果 >= 0，则活动退出时将在 onActivityResult() 中返回结果
     */
    public static void startActivityForResult(@NonNull final Activity activity,
                                              @NonNull final Class<? extends Activity> clz,
                                              final int requestCode) {
        startActivityForResult(activity, null, activity.getPackageName(), clz.getName(),
                requestCode, null);
    }

    /**
     * 前往 activity
     *
     * @param activity    activity.
     * @param clz         目标activity.class
     * @param requestCode 如果 >= 0，则活动退出时将在 onActivityResult() 中返回结果
     * @param options     跳转的动画. 使用{@link ActivityOptionsCompat#toBundle()} 生成所需要的bundle
     */
    public static void startActivityForResult(@NonNull final Activity activity,
                                              @NonNull final Class<? extends Activity> clz,
                                              final int requestCode,
                                              @Nullable final Bundle options) {
        startActivityForResult(activity, null, activity.getPackageName(), clz.getName(),
                requestCode, options);
    }

    /**
     * 前往 activity
     *
     * @param activity       当前 activity.
     * @param clz            目标activity.class
     * @param requestCode    如果 >= 0，则活动退出时将在 onActivityResult() 中返回结果
     * @param sharedElements 和目标的view联动的动画 比如平移啥的
     */
    public static void startActivityForResult(@NonNull final Activity activity,
                                              @NonNull final Class<? extends Activity> clz,
                                              final int requestCode,
                                              final View... sharedElements) {
        startActivityForResult(activity, null, activity.getPackageName(), clz.getName(),
                requestCode, getOptionsBundle(activity, sharedElements));
    }

    /**
     * 前往 activity
     *
     * @param activity    当前 activity.
     * @param clz         目标activity.class
     * @param requestCode 如果 >= 0，则活动退出时将在 onActivityResult() 中返回结果
     * @param enterAnim   进入动画 使用r.anim.xxx 的资源
     * @param exitAnim    退出动画 使用r.anim.xxx 的资源
     */
    public static void startActivityForResult(@NonNull final Activity activity,
                                              @NonNull final Class<? extends Activity> clz,
                                              final int requestCode,
                                              @AnimRes final int enterAnim,
                                              @AnimRes final int exitAnim) {
        startActivityForResult(activity, null, activity.getPackageName(), clz.getName(),
                requestCode, getOptionsBundle(activity, enterAnim, exitAnim));
    }

    /**
     * 前往 activity
     *
     * @param extras      携带的bundle数据
     * @param activity    当前 activity.
     * @param clz         目标activity.class
     * @param requestCode 如果 >= 0，则活动退出时将在 onActivityResult() 中返回结果
     */
    public static void startActivityForResult(@NonNull final Bundle extras,
                                              @NonNull final Activity activity,
                                              @NonNull final Class<? extends Activity> clz,
                                              final int requestCode) {
        startActivityForResult(activity, extras, activity.getPackageName(), clz.getName(),
                requestCode, null);
    }

    /**
     * 前往 activity
     *
     * @param extras      携带的bundle数据
     * @param activity    当前 activity.
     * @param clz         目标activity.class
     * @param requestCode 如果 >= 0，则活动退出时将在 onActivityResult() 中返回结果
     * @param options     跳转的动画. 使用{@link ActivityOptionsCompat#toBundle()} 生成所需要的bundle
     */
    public static void startActivityForResult(@NonNull final Bundle extras,
                                              @NonNull final Activity activity,
                                              @NonNull final Class<? extends Activity> clz,
                                              final int requestCode,
                                              @Nullable final Bundle options) {
        startActivityForResult(activity, extras, activity.getPackageName(), clz.getName(),
                requestCode, options);
    }

    /**
     * 前往 activity
     *
     * @param extras         携带的bundle数据
     * @param activity       当前 activity.
     * @param clz            目标activity.class
     * @param requestCode    如果 >= 0，则活动退出时将在 onActivityResult() 中返回结果
     * @param sharedElements 和目标的view联动的动画 比如平移啥的
     */
    public static void startActivityForResult(@NonNull final Bundle extras,
                                              @NonNull final Activity activity,
                                              @NonNull final Class<? extends Activity> clz,
                                              final int requestCode,
                                              final View... sharedElements) {
        startActivityForResult(activity, extras, activity.getPackageName(), clz.getName(),
                requestCode, getOptionsBundle(activity, sharedElements));
    }

    /**
     * 前往 activity
     *
     * @param extras      携带的bundle数据
     * @param activity    当前 activity.
     * @param clz         目标activity.class
     * @param requestCode 如果 >= 0，则活动退出时将在 onActivityResult() 中返回结果
     * @param enterAnim   进入动画 使用r.anim.xxx 的资源
     * @param exitAnim    退出动画 使用r.anim.xxx 的资源
     */
    public static void startActivityForResult(@NonNull final Bundle extras,
                                              @NonNull final Activity activity,
                                              @NonNull final Class<? extends Activity> clz,
                                              final int requestCode,
                                              @AnimRes final int enterAnim,
                                              @AnimRes final int exitAnim) {
        startActivityForResult(activity, extras, activity.getPackageName(), clz.getName(),
                requestCode, getOptionsBundle(activity, enterAnim, exitAnim));
    }

    /**
     * 前往活动并在活动结束时获取结果
     *
     * @param extras      携带的bundle数据
     * @param activity    当前 activity.
     * @param pkg         包名
     * @param cls         目标activity.class
     * @param requestCode 如果 >= 0，则活动退出时将在 onActivityResult() 中返回结果
     */
    public static void startActivityForResult(@NonNull final Bundle extras,
                                              @NonNull final Activity activity,
                                              @NonNull final String pkg,
                                              @NonNull final String cls,
                                              final int requestCode) {
        startActivityForResult(activity, extras, pkg, cls, requestCode, null);
    }

    /**
     * 前往活动并在活动结束时获取结果
     *
     * @param extras      携带的bundle数据
     * @param activity    当前 activity.
     * @param pkg         包名
     * @param cls         目标activity.class
     * @param requestCode 如果 >= 0，则活动退出时将在 onActivityResult() 中返回结果
     * @param options     跳转的动画. 使用{@link ActivityOptionsCompat#toBundle()} 生成所需要的bundle
     */
    public static void startActivityForResult(@NonNull final Bundle extras,
                                              @NonNull final Activity activity,
                                              @NonNull final String pkg,
                                              @NonNull final String cls,
                                              final int requestCode,
                                              @Nullable final Bundle options) {
        startActivityForResult(activity, extras, pkg, cls, requestCode, options);
    }

    /**
     * 前往活动并在活动结束时获取结果
     *
     * @param extras         携带的bundle数据
     * @param activity       当前 activity.
     * @param pkg            包名
     * @param cls            目标activity.class.
     * @param requestCode    如果 >= 0，则活动退出时将在 onActivityResult() 中返回结果
     * @param sharedElements 和目标的view联动的动画 比如平移啥的
     */
    public static void startActivityForResult(@NonNull final Bundle extras,
                                              @NonNull final Activity activity,
                                              @NonNull final String pkg,
                                              @NonNull final String cls,
                                              final int requestCode,
                                              final View... sharedElements) {
        startActivityForResult(activity, extras, pkg, cls,
                requestCode, getOptionsBundle(activity, sharedElements));
    }

    /**
     * 前往活动并在活动结束时获取结果
     *
     * @param extras      携带的bundle数据
     * @param pkg         包名
     * @param cls         目标activity.class.
     * @param requestCode 如果 >= 0，则活动退出时将在 onActivityResult() 中返回结果
     * @param enterAnim   进入动画 使用r.anim.xxx 的资源
     * @param exitAnim    退出动画 使用r.anim.xxx 的资源
     */
    public static void startActivityForResult(@NonNull final Bundle extras,
                                              @NonNull final Activity activity,
                                              @NonNull final String pkg,
                                              @NonNull final String cls,
                                              final int requestCode,
                                              @AnimRes final int enterAnim,
                                              @AnimRes final int exitAnim) {
        startActivityForResult(activity, extras, pkg, cls,
                requestCode, getOptionsBundle(activity, enterAnim, exitAnim));
    }

    /**
     * 前往活动并在活动结束时获取结果
     *
     * @param activity    The activity.
     * @param intent      intent
     * @param requestCode 如果 >= 0，则活动退出时将在 onActivityResult() 中返回结果
     */
    public static void startActivityForResult(@NonNull final Activity activity,
                                              @NonNull final Intent intent,
                                              final int requestCode) {
        startActivityForResult(intent, activity, requestCode, null);
    }

    /**
     * 前往活动并在活动结束时获取结果
     *
     * @param activity    The activity.
     * @param intent      intent
     * @param requestCode 如果 >= 0，则活动退出时将在 onActivityResult() 中返回结果
     * @param options     跳转的动画. 使用{@link ActivityOptionsCompat#toBundle()} 生成所需要的bundle
     */
    public static void startActivityForResult(@NonNull final Activity activity,
                                              @NonNull final Intent intent,
                                              final int requestCode,
                                              @Nullable final Bundle options) {
        startActivityForResult(intent, activity, requestCode, options);
    }

    /**
     * 前往活动并在活动结束时获取结果
     *
     * @param activity       当前 activity.
     * @param intent         The description of the activity to start.
     * @param requestCode    如果 >= 0，则活动退出时将在 onActivityResult() 中返回结果
     * @param sharedElements 和目标的view联动的动画 比如平移啥的
     */
    public static void startActivityForResult(@NonNull final Activity activity,
                                              @NonNull final Intent intent,
                                              final int requestCode,
                                              final View... sharedElements) {
        startActivityForResult(intent, activity,
                requestCode, getOptionsBundle(activity, sharedElements));
    }

    /**
     * 前往活动并在活动结束时获取结果
     *
     * @param activity    The activity.
     * @param intent      intent
     * @param requestCode 如果 >= 0，则活动退出时将在 onActivityResult() 中返回结果
     * @param enterAnim   进入动画 使用r.anim.xxx 的资源
     * @param exitAnim    退出动画 使用r.anim.xxx 的资源
     */
    public static void startActivityForResult(@NonNull final Activity activity,
                                              @NonNull final Intent intent,
                                              final int requestCode,
                                              @AnimRes final int enterAnim,
                                              @AnimRes final int exitAnim) {
        startActivityForResult(intent, activity,
                requestCode, getOptionsBundle(activity, enterAnim, exitAnim));
    }

    /**
     * 前往 activity
     *
     * @param fragment    fragment
     * @param clz         The activity class.
     * @param requestCode 如果 >= 0，则活动退出时将在 onActivityResult() 中返回结果
     */
    public static void startActivityForResult(@NonNull final Fragment fragment,
                                              @NonNull final Class<? extends Activity> clz,
                                              final int requestCode) {
        startActivityForResult(fragment, null, DawnBridge.getApp().getPackageName(), clz.getName(),
                requestCode, null);
    }

    /**
     * 前往 activity
     *
     * @param fragment    fragment
     * @param clz         The activity class.
     * @param requestCode 如果 >= 0，则活动退出时将在 onActivityResult() 中返回结果
     * @param options     跳转的动画. 使用{@link ActivityOptionsCompat#toBundle()} 生成所需要的bundle
     */
    public static void startActivityForResult(@NonNull final Fragment fragment,
                                              @NonNull final Class<? extends Activity> clz,
                                              final int requestCode,
                                              @Nullable final Bundle options) {
        startActivityForResult(fragment, null, DawnBridge.getApp().getPackageName(), clz.getName(),
                requestCode, options);
    }

    /**
     * 前往 activity
     *
     * @param fragment       The fragment.
     * @param clz            The activity class.
     * @param requestCode    如果 >= 0，则活动退出时将在 onActivityResult() 中返回结果
     * @param sharedElements 和目标的view联动的动画 比如平移啥的
     */
    public static void startActivityForResult(@NonNull final Fragment fragment,
                                              @NonNull final Class<? extends Activity> clz,
                                              final int requestCode,
                                              final View... sharedElements) {
        startActivityForResult(fragment, null, DawnBridge.getApp().getPackageName(), clz.getName(),
                requestCode, getOptionsBundle(fragment, sharedElements));
    }

    /**
     * 前往 activity
     *
     * @param fragment    fragment
     * @param clz         The activity class.
     * @param requestCode 如果 >= 0，则活动退出时将在 onActivityResult() 中返回结果
     * @param enterAnim   进入动画 使用r.anim.xxx 的资源
     * @param exitAnim    退出动画 使用r.anim.xxx 的资源
     */
    public static void startActivityForResult(@NonNull final Fragment fragment,
                                              @NonNull final Class<? extends Activity> clz,
                                              final int requestCode,
                                              @AnimRes final int enterAnim,
                                              @AnimRes final int exitAnim) {
        startActivityForResult(fragment, null, DawnBridge.getApp().getPackageName(), clz.getName(),
                requestCode, getOptionsBundle(fragment, enterAnim, exitAnim));
    }

    /**
     * 前往 activity
     *
     * @param extras      携带的bundle数据
     * @param fragment    fragment
     * @param clz         The activity class.
     * @param requestCode 如果 >= 0，则活动退出时将在 onActivityResult() 中返回结果
     */
    public static void startActivityForResult(@NonNull final Bundle extras,
                                              @NonNull final Fragment fragment,
                                              @NonNull final Class<? extends Activity> clz,
                                              final int requestCode) {
        startActivityForResult(fragment, extras, DawnBridge.getApp().getPackageName(), clz.getName(),
                requestCode, null);
    }

    /**
     * 前往 activity
     *
     * @param extras      携带的bundle数据
     * @param fragment    fragment
     * @param clz         The activity class.
     * @param requestCode 如果 >= 0，则活动退出时将在 onActivityResult() 中返回结果
     * @param options     跳转的动画. 使用{@link ActivityOptionsCompat#toBundle()} 生成所需要的bundle
     */
    public static void startActivityForResult(@NonNull final Bundle extras,
                                              @NonNull final Fragment fragment,
                                              @NonNull final Class<? extends Activity> clz,
                                              final int requestCode,
                                              @Nullable final Bundle options) {
        startActivityForResult(fragment, extras, DawnBridge.getApp().getPackageName(), clz.getName(),
                requestCode, options);
    }

    /**
     * 前往 activity
     *
     * @param extras         携带的bundle数据
     * @param fragment       The fragment.
     * @param clz            The activity class.
     * @param requestCode    如果 >= 0，则活动退出时将在 onActivityResult() 中返回结果
     * @param sharedElements 和目标的view联动的动画 比如平移啥的
     */
    public static void startActivityForResult(@NonNull final Bundle extras,
                                              @NonNull final Fragment fragment,
                                              @NonNull final Class<? extends Activity> clz,
                                              final int requestCode,
                                              final View... sharedElements) {
        startActivityForResult(fragment, extras, DawnBridge.getApp().getPackageName(), clz.getName(),
                requestCode, getOptionsBundle(fragment, sharedElements));
    }

    /**
     * 前往 activity
     *
     * @param extras      携带的bundle数据
     * @param fragment    fragment
     * @param clz         The activity class.
     * @param requestCode 如果 >= 0，则活动退出时将在 onActivityResult() 中返回结果
     * @param enterAnim   进入动画 使用r.anim.xxx 的资源
     * @param exitAnim    退出动画 使用r.anim.xxx 的资源
     */
    public static void startActivityForResult(@NonNull final Bundle extras,
                                              @NonNull final Fragment fragment,
                                              @NonNull final Class<? extends Activity> clz,
                                              final int requestCode,
                                              @AnimRes final int enterAnim,
                                              @AnimRes final int exitAnim) {
        startActivityForResult(fragment, extras, DawnBridge.getApp().getPackageName(), clz.getName(),
                requestCode, getOptionsBundle(fragment, enterAnim, exitAnim));
    }

    /**
     * 前往活动并在活动结束时获取结果
     *
     * @param fragment    fragment
     * @param extras      携带的bundle数据
     * @param pkg         包名
     * @param cls         目标activity.class.
     * @param requestCode 如果 >= 0，则活动退出时将在 onActivityResult() 中返回结果
     */
    public static void startActivityForResult(@NonNull final Bundle extras,
                                              @NonNull final Fragment fragment,
                                              @NonNull final String pkg,
                                              @NonNull final String cls,
                                              final int requestCode) {
        startActivityForResult(fragment, extras, pkg, cls, requestCode, null);
    }

    /**
     * 前往活动并在活动结束时获取结果
     *
     * @param extras      携带的bundle数据
     * @param fragment    fragment
     * @param pkg         包名
     * @param cls         目标activity.class.
     * @param requestCode 如果 >= 0，则活动退出时将在 onActivityResult() 中返回结果
     * @param options     跳转的动画. 使用{@link ActivityOptionsCompat#toBundle()} 生成所需要的bundle
     */
    public static void startActivityForResult(@NonNull final Bundle extras,
                                              @NonNull final Fragment fragment,
                                              @NonNull final String pkg,
                                              @NonNull final String cls,
                                              final int requestCode,
                                              @Nullable final Bundle options) {
        startActivityForResult(fragment, extras, pkg, cls, requestCode, options);
    }

    /**
     * 前往活动并在活动结束时获取结果
     *
     * @param extras         携带的bundle数据
     * @param fragment       The fragment.
     * @param pkg            包名
     * @param cls            目标activity.class.
     * @param requestCode    如果 >= 0，则活动退出时将在 onActivityResult() 中返回结果
     * @param sharedElements 和目标的view联动的动画 比如平移啥的
     */
    public static void startActivityForResult(@NonNull final Bundle extras,
                                              @NonNull final Fragment fragment,
                                              @NonNull final String pkg,
                                              @NonNull final String cls,
                                              final int requestCode,
                                              final View... sharedElements) {
        startActivityForResult(fragment, extras, pkg, cls,
                requestCode, getOptionsBundle(fragment, sharedElements));
    }

    /**
     * 前往活动并在活动结束时获取结果
     *
     * @param extras      携带的bundle数据
     * @param pkg         包名
     * @param cls         目标activity.class.
     * @param requestCode 如果 >= 0，则活动退出时将在 onActivityResult() 中返回结果
     * @param enterAnim   进入动画 使用r.anim.xxx 的资源
     * @param exitAnim    退出动画 使用r.anim.xxx 的资源
     */
    public static void startActivityForResult(@NonNull final Bundle extras,
                                              @NonNull final Fragment fragment,
                                              @NonNull final String pkg,
                                              @NonNull final String cls,
                                              final int requestCode,
                                              @AnimRes final int enterAnim,
                                              @AnimRes final int exitAnim) {
        startActivityForResult(fragment, extras, pkg, cls,
                requestCode, getOptionsBundle(fragment, enterAnim, exitAnim));
    }

    /**
     * 前往活动并在活动结束时获取结果
     *
     * @param fragment    fragment
     * @param intent      intent
     * @param requestCode 如果 >= 0，则活动退出时将在 onActivityResult() 中返回结果
     */
    public static void startActivityForResult(@NonNull final Fragment fragment,
                                              @NonNull final Intent intent,
                                              final int requestCode) {
        startActivityForResult(intent, fragment, requestCode, null);
    }

    /**
     * 前往活动并在活动结束时获取结果
     *
     * @param fragment    fragment
     * @param intent      intent
     * @param requestCode 如果 >= 0，则活动退出时将在 onActivityResult() 中返回结果
     * @param options     跳转的动画. 使用{@link ActivityOptionsCompat#toBundle()} 生成所需要的bundle
     */
    public static void startActivityForResult(@NonNull final Fragment fragment,
                                              @NonNull final Intent intent,
                                              final int requestCode,
                                              @Nullable final Bundle options) {
        startActivityForResult(intent, fragment, requestCode, options);
    }

    /**
     * 前往活动并在活动结束时获取结果
     *
     * @param fragment       fragment.
     * @param intent         intent
     * @param requestCode    如果 >= 0，则活动退出时将在 onActivityResult() 中返回结果
     * @param sharedElements 和目标的view联动的动画 比如平移啥的
     */
    public static void startActivityForResult(@NonNull final Fragment fragment,
                                              @NonNull final Intent intent,
                                              final int requestCode,
                                              final View... sharedElements) {
        startActivityForResult(intent, fragment,
                requestCode, getOptionsBundle(fragment, sharedElements));
    }

    /**
     * 前往活动并在活动结束时获取结果
     *
     * @param fragment    fragment
     * @param intent      intent
     * @param requestCode 如果 >= 0，则活动退出时将在 onActivityResult() 中返回结果
     * @param enterAnim   进入动画 使用r.anim.xxx 的资源
     * @param exitAnim    退出动画 使用r.anim.xxx 的资源
     */
    public static void startActivityForResult(@NonNull final Fragment fragment,
                                              @NonNull final Intent intent,
                                              final int requestCode,
                                              @AnimRes final int enterAnim,
                                              @AnimRes final int exitAnim) {
        startActivityForResult(intent, fragment,
                requestCode, getOptionsBundle(fragment, enterAnim, exitAnim));
    }

    /**
     * Start activities.
     *
     * @param intents The descriptions of the activities to start.
     */
    public static void startActivities(@NonNull final Intent[] intents) {
        startActivities(intents, getTopActivityOrApp(), null);
    }

    /**
     * Start activities.
     *
     * @param intents The descriptions of the activities to start.
     * @param options Additional options for how the Activity should be started.
     */
    public static void startActivities(@NonNull final Intent[] intents,
                                       @Nullable final Bundle options) {
        startActivities(intents, getTopActivityOrApp(), options);
    }

    /**
     * 前往activity
     *
     * @param intents   前往activity的intents
     * @param enterAnim 进入动画 使用r.anim.xxx 的资源
     * @param exitAnim  退出动画 使用r.anim.xxx 的资源
     */
    public static void startActivities(@NonNull final Intent[] intents,
                                       @AnimRes final int enterAnim,
                                       @AnimRes final int exitAnim) {
        Context context = getTopActivityOrApp();
        startActivities(intents, context, getOptionsBundle(context, enterAnim, exitAnim));
    }

    /**
     * Start activities.
     *
     * @param activity activity.
     * @param intents  The descriptions of the activities to start.
     */
    public static void startActivities(@NonNull final Activity activity,
                                       @NonNull final Intent[] intents) {
        startActivities(intents, activity, null);
    }

    /**
     * Start activities.
     *
     * @param activity The activity.
     * @param intents  The descriptions of the activities to start.
     * @param options  Additional options for how the Activity should be started.
     */
    public static void startActivities(@NonNull final Activity activity,
                                       @NonNull final Intent[] intents,
                                       @Nullable final Bundle options) {
        startActivities(intents, activity, options);
    }

    /**
     * Start activities.
     *
     * @param activity  The activity.
     * @param intents   The descriptions of the activities to start.
     * @param enterAnim 进入动画 使用r.anim.xxx 的资源
     * @param exitAnim  退出动画 使用r.anim.xxx 的资源
     */
    public static void startActivities(@NonNull final Activity activity,
                                       @NonNull final Intent[] intents,
                                       @AnimRes final int enterAnim,
                                       @AnimRes final int exitAnim) {
        startActivities(intents, activity, getOptionsBundle(activity, enterAnim, exitAnim));
    }

    /**
     * 前往桌面（相当于按了返回桌面按钮）
     */
    public static void startHomeActivity() {
        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
        homeIntent.addCategory(Intent.CATEGORY_HOME);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(homeIntent);
    }

    /**
     * 前往main页面 就是在清单列表设置main的activity
     */
    public static void startLauncherActivity() {
        startLauncherActivity(DawnBridge.getApp().getPackageName());
    }

    /**
     * 前往main页面 就是在清单列表设置main的activity
     *
     * @param pkg 包名
     */
    public static void startLauncherActivity(@NonNull final String pkg) {
        String launcherActivity = getLauncherActivity(pkg);
        if (TextUtils.isEmpty(launcherActivity)) {
            return;
        }
        startActivity(pkg, launcherActivity);
    }

    /**
     * Return  activity 列表
     *
     * @return the list of activity
     */
    public static List<Activity> getActivityList() {
        return DawnBridge.getActivityList();
    }

    /**
     * Return launcher activity的名字
     *
     * @return the name of launcher activity
     */
    public static String getLauncherActivity() {
        return getLauncherActivity(DawnBridge.getApp().getPackageName());
    }

    /**
     * Return launcher activity的名字
     *
     * @param pkg The name of the package.
     * @return the name of launcher activity
     */
    public static String getLauncherActivity(@NonNull final String pkg) {
        if (DawnBridge.isSpace(pkg)) {
            return "";
        }
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setPackage(pkg);
        PackageManager pm = DawnBridge.getApp().getPackageManager();
        List<ResolveInfo> info = pm.queryIntentActivities(intent, 0);
        if (info == null || info.size() == 0) {
            return "";
        }
        return info.get(0).activityInfo.name;
    }

    /**
     * Return the list of main activities.
     *
     * @return the list of main activities
     */
    public static List<String> getMainActivities() {
        return getMainActivities(DawnBridge.getApp().getPackageName());
    }

    /**
     * Return the list of main activities.
     *
     * @param pkg The name of the package.
     * @return the list of main activities
     */
    public static List<String> getMainActivities(@NonNull final String pkg) {
        List<String> ret = new ArrayList<>();
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.setPackage(pkg);
        PackageManager pm = DawnBridge.getApp().getPackageManager();
        List<ResolveInfo> info = pm.queryIntentActivities(intent, 0);
        int size = info.size();
        if (size == 0) {
            return ret;
        }
        for (int i = 0; i < size; i++) {
            ResolveInfo ri = info.get(i);
            if (ri.activityInfo.processName.equals(pkg)) {
                ret.add(ri.activityInfo.name);
            }
        }
        return ret;
    }

    /**
     * Return  栈定的activity
     *
     * @return the top activity in activity's stack
     */
    public static Activity getTopActivity() {
        return DawnBridge.getTopActivity();
    }

    /**
     * 指定activity是否在活动状态
     *
     * @param context The context.
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isActivityAlive(final Context context) {
        return isActivityAlive(getActivityByContext(context));
    }

    /**
     * 指定activity是否在活动状态
     *
     * @param activity The activity.
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isActivityAlive(final Activity activity) {
        return activity != null && !activity.isFinishing() && !activity.isDestroyed();
    }

    /**
     * 返回activity是否存在于活动的堆栈中。
     *
     * @param activity The activity.
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isActivityExistsInStack(@NonNull final Activity activity) {
        List<Activity> activities = DawnBridge.getActivityList();
        for (Activity aActivity : activities) {
            if (aActivity.equals(activity)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 返回activity是否存在于活动的堆栈中。
     *
     * @param clz The activity class.
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isActivityExistsInStack(@NonNull final Class<? extends Activity> clz) {
        List<Activity> activities = DawnBridge.getActivityList();
        for (Activity aActivity : activities) {
            if (aActivity.getClass().equals(clz)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 结束activity
     *
     * @param activity The activity.
     */
    public static void finishActivity(@NonNull final Activity activity) {
        finishActivity(activity, false);
    }

    /**
     * 结束activity
     *
     * @param activity   The activity.
     * @param isLoadAnim True 为传出活动使用动画，否则为 false。
     */
    public static void finishActivity(@NonNull final Activity activity, final boolean isLoadAnim) {
        activity.finish();
        if (!isLoadAnim) {
            activity.overridePendingTransition(0, 0);
        }
    }

    /**
     * 结束activity
     *
     * @param activity  The activity.
     * @param enterAnim 进入动画 使用r.anim.xxx 的资源
     * @param exitAnim  退出动画 使用r.anim.xxx 的资源
     */
    public static void finishActivity(@NonNull final Activity activity,
                                      @AnimRes final int enterAnim,
                                      @AnimRes final int exitAnim) {
        activity.finish();
        activity.overridePendingTransition(enterAnim, exitAnim);
    }

    /**
     * 结束activity
     *
     * @param clz The activity class.
     */
    public static void finishActivity(@NonNull final Class<? extends Activity> clz) {
        finishActivity(clz, false);
    }

    /**
     * 结束activity
     *
     * @param clz        The activity class.
     * @param isLoadAnim True to use animation for the outgoing activity, false otherwise.
     */
    public static void finishActivity(@NonNull final Class<? extends Activity> clz,
                                      final boolean isLoadAnim) {
        List<Activity> activities = DawnBridge.getActivityList();
        for (Activity activity : activities) {
            if (activity.getClass().equals(clz)) {
                activity.finish();
                if (!isLoadAnim) {
                    activity.overridePendingTransition(0, 0);
                }
            }
        }
    }

    /**
     * 结束activity
     *
     * @param clz       The activity class.
     * @param enterAnim 进入动画 使用r.anim.xxx 的资源
     * @param exitAnim  退出动画 使用r.anim.xxx 的资源
     */
    public static void finishActivity(@NonNull final Class<? extends Activity> clz,
                                      @AnimRes final int enterAnim,
                                      @AnimRes final int exitAnim) {
        List<Activity> activities = DawnBridge.getActivityList();
        for (Activity activity : activities) {
            if (activity.getClass().equals(clz)) {
                activity.finish();
                activity.overridePendingTransition(enterAnim, exitAnim);
            }
        }
    }

    /**
     * 结束activity
     *
     * @param activity      The activity.
     * @param isIncludeSelf 如果包含活动，则为 true，否则为 false。
     */
    public static boolean finishToActivity(@NonNull final Activity activity,
                                           final boolean isIncludeSelf) {
        return finishToActivity(activity, isIncludeSelf, false);
    }

    /**
     * 结束activity
     *
     * @param activity      The activity.
     * @param isIncludeSelf 如果包含活动，则为 true，否则为 false。
     * @param isLoadAnim    动画
     */
    public static boolean finishToActivity(@NonNull final Activity activity,
                                           final boolean isIncludeSelf,
                                           final boolean isLoadAnim) {
        List<Activity> activities = DawnBridge.getActivityList();
        for (Activity act : activities) {
            if (act.equals(activity)) {
                if (isIncludeSelf) {
                    finishActivity(act, isLoadAnim);
                }
                return true;
            }
            finishActivity(act, isLoadAnim);
        }
        return false;
    }

    /**
     * 结束activity
     *
     * @param activity      The activity.
     * @param isIncludeSelf 如果包含活动，则为 true，否则为 false。
     * @param enterAnim     进入动画 使用r.anim.xxx 的资源
     * @param exitAnim      退出动画 使用r.anim.xxx 的资源
     */
    public static boolean finishToActivity(@NonNull final Activity activity,
                                           final boolean isIncludeSelf,
                                           @AnimRes final int enterAnim,
                                           @AnimRes final int exitAnim) {
        List<Activity> activities = DawnBridge.getActivityList();
        for (Activity act : activities) {
            if (act.equals(activity)) {
                if (isIncludeSelf) {
                    finishActivity(act, enterAnim, exitAnim);
                }
                return true;
            }
            finishActivity(act, enterAnim, exitAnim);
        }
        return false;
    }

    /**
     * 结束activity
     *
     * @param clz           The activity class.
     * @param isIncludeSelf 如果包含活动，则为 true，否则为 false。
     */
    public static boolean finishToActivity(@NonNull final Class<? extends Activity> clz,
                                           final boolean isIncludeSelf) {
        return finishToActivity(clz, isIncludeSelf, false);
    }

    /**
     * 结束activity
     *
     * @param clz           The activity class.
     * @param isIncludeSelf 如果包含活动，则为 true，否则为 false。
     * @param isLoadAnim    动画
     */
    public static boolean finishToActivity(@NonNull final Class<? extends Activity> clz,
                                           final boolean isIncludeSelf,
                                           final boolean isLoadAnim) {
        List<Activity> activities = DawnBridge.getActivityList();
        for (Activity act : activities) {
            if (act.getClass().equals(clz)) {
                if (isIncludeSelf) {
                    finishActivity(act, isLoadAnim);
                }
                return true;
            }
            finishActivity(act, isLoadAnim);
        }
        return false;
    }

    /**
     * 结束activity
     *
     * @param clz           The activity class.
     * @param isIncludeSelf 如果包含活动，则为 true，否则为 false。
     * @param enterAnim     进入动画 使用r.anim.xxx 的资源
     * @param exitAnim      退出动画 使用r.anim.xxx 的资源
     */
    public static boolean finishToActivity(@NonNull final Class<? extends Activity> clz,
                                           final boolean isIncludeSelf,
                                           @AnimRes final int enterAnim,
                                           @AnimRes final int exitAnim) {
        List<Activity> activities = DawnBridge.getActivityList();
        for (Activity act : activities) {
            if (act.getClass().equals(clz)) {
                if (isIncludeSelf) {
                    finishActivity(act, enterAnim, exitAnim);
                }
                return true;
            }
            finishActivity(act, enterAnim, exitAnim);
        }
        return false;
    }

    /**
     * Finish 类型不等于activity类别的activity
     *
     * @param clz The activity class.
     */
    public static void finishOtherActivities(@NonNull final Class<? extends Activity> clz) {
        finishOtherActivities(clz, false);
    }


    /**
     * Finish 类型不等于activity类别的activity
     *
     * @param clz        The activity class.
     * @param isLoadAnim 动画
     */
    public static void finishOtherActivities(@NonNull final Class<? extends Activity> clz,
                                             final boolean isLoadAnim) {
        List<Activity> activities = DawnBridge.getActivityList();
        for (Activity act : activities) {
            if (!act.getClass().equals(clz)) {
                finishActivity(act, isLoadAnim);
            }
        }
    }

    /**
     * Finish 类型不等于activity类别的activity
     *
     * @param clz       The activity class.
     * @param enterAnim 进入动画 使用r.anim.xxx 的资源
     * @param exitAnim  退出动画 使用r.anim.xxx 的资源
     */
    public static void finishOtherActivities(@NonNull final Class<? extends Activity> clz,
                                             @AnimRes final int enterAnim,
                                             @AnimRes final int exitAnim) {
        List<Activity> activities = DawnBridge.getActivityList();
        for (Activity act : activities) {
            if (!act.getClass().equals(clz)) {
                finishActivity(act, enterAnim, exitAnim);
            }
        }
    }

    /**
     * Finish 所有 activities.
     */
    public static void finishAllActivities() {
        finishAllActivities(false);
    }

    /**
     * Finish 所有 activities.
     *
     * @param isLoadAnim 动画
     */
    public static void finishAllActivities(final boolean isLoadAnim) {
        List<Activity> activityList = DawnBridge.getActivityList();
        for (Activity act : activityList) {
            // sActivityList remove the index activity at onActivityDestroyed
            act.finish();
            if (!isLoadAnim) {
                act.overridePendingTransition(0, 0);
            }
        }
    }

    /**
     * Finish 所有 activities.
     *
     * @param enterAnim 进入动画 使用r.anim.xxx 的资源
     * @param exitAnim  退出动画 使用r.anim.xxx 的资源
     */
    public static void finishAllActivities(@AnimRes final int enterAnim,
                                           @AnimRes final int exitAnim) {
        List<Activity> activityList = DawnBridge.getActivityList();
        for (Activity act : activityList) {
            // sActivityList remove the index activity at onActivityDestroyed
            act.finish();
            act.overridePendingTransition(enterAnim, exitAnim);
        }
    }

    /**
     * finish除栈顶activity外的所有activity
     */
    public static void finishAllActivitiesExceptNewest() {
        finishAllActivitiesExceptNewest(false);
    }

    /**
     * finish除栈顶activity外的所有activity
     *
     * @param isLoadAnim 动画
     */
    public static void finishAllActivitiesExceptNewest(final boolean isLoadAnim) {
        List<Activity> activities = DawnBridge.getActivityList();
        for (int i = 1; i < activities.size(); i++) {
            finishActivity(activities.get(i), isLoadAnim);
        }
    }

    /**
     * finish除栈顶activity外的所有activity
     *
     * @param enterAnim 进入动画 使用r.anim.xxx 的资源
     * @param exitAnim  退出动画 使用r.anim.xxx 的资源
     */
    public static void finishAllActivitiesExceptNewest(@AnimRes final int enterAnim,
                                                       @AnimRes final int exitAnim) {
        List<Activity> activities = DawnBridge.getActivityList();
        for (int i = 1; i < activities.size(); i++) {
            finishActivity(activities.get(i), enterAnim, exitAnim);
        }
    }

    /**
     * Return activity 的icon.
     *
     * @param activity The activity.
     * @return activity 的icon
     */
    @Nullable
    public static Drawable getActivityIcon(@NonNull final Activity activity) {
        return getActivityIcon(activity.getComponentName());
    }

    /**
     * Return activity 的icon.
     *
     * @param clz The activity class.
     * @return activity 的icon
     */
    @Nullable
    public static Drawable getActivityIcon(@NonNull final Class<? extends Activity> clz) {
        return getActivityIcon(new ComponentName(DawnBridge.getApp(), clz));
    }

    /**
     * Return activity 的icon.
     *
     * @param activityName The name of activity.
     * @return activity 的icon
     */
    @Nullable
    public static Drawable getActivityIcon(@NonNull final ComponentName activityName) {
        PackageManager pm = DawnBridge.getApp().getPackageManager();
        try {
            return pm.getActivityIcon(activityName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Return activity 的logo.
     *
     * @param activity activity.
     * @return activity 的logo
     */
    @Nullable
    public static Drawable getActivityLogo(@NonNull final Activity activity) {
        return getActivityLogo(activity.getComponentName());
    }

    /**
     * 返回 activity 的logo.
     *
     * @param clz activity class.
     * @return activity 的logo
     */
    @Nullable
    public static Drawable getActivityLogo(@NonNull final Class<? extends Activity> clz) {
        return getActivityLogo(new ComponentName(DawnBridge.getApp(), clz));
    }

    /**
     * 返回 activity 的logo.
     *
     * @param activityName activity 名称.
     * @return activity 的logo
     */
    @Nullable
    public static Drawable getActivityLogo(@NonNull final ComponentName activityName) {
        PackageManager pm = DawnBridge.getApp().getPackageManager();
        try {
            return pm.getActivityLogo(activityName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 按上下文返回活动。
     *
     * @param context context
     * @return 此上下文的activity
     */
    @Nullable
    public static Activity getActivityByContext(@Nullable Context context) {
        if (context == null) {
            return null;
        }
        Activity activity = getActivityByContextInner(context);
        if (!isActivityAlive(activity)) {
            return null;
        }
        return activity;
    }

    @Nullable
    private static Activity getActivityByContextInner(@Nullable Context context) {
        if (context == null) {
            return null;
        }
        List<Context> list = new ArrayList<>();
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (Activity) context;
            }
            Activity activity = getActivityFromDecorContext(context);
            if (activity != null) {
                return activity;
            }
            list.add(context);
            context = ((ContextWrapper) context).getBaseContext();
            if (context == null) {
                return null;
            }
            if (list.contains(context)) {
                // loop context
                return null;
            }
        }
        return null;
    }


    @Nullable
    private static Activity getActivityFromDecorContext(@Nullable Context context) {
        if (context == null) {
            return null;
        }
        if ("com.android.internal.policy.DecorContext".equals(context.getClass().getName())) {
            try {
                Field mActivityContextField = context.getClass().getDeclaredField("mActivityContext");
                mActivityContextField.setAccessible(true);
                //noinspection ConstantConditions,unchecked
                return ((WeakReference<Activity>) mActivityContextField.get(context)).get();
            } catch (Exception ignore) {
            }
        }
        return null;
    }

    private static void startActivity(final Context context,
                                      final Bundle extras,
                                      final String pkg,
                                      final String cls,
                                      @Nullable final Bundle options) {
        Intent intent = new Intent();
        if (extras != null) {
            intent.putExtras(extras);
        }
        intent.setComponent(new ComponentName(pkg, cls));
        startActivity(intent, context, options);
    }

    private static boolean startActivity(final Intent intent,
                                         final Context context,
                                         final Bundle options) {
        if (!isIntentAvailable(intent)) {
            Log.e("ActivityUtils", "intent is unavailable");
            return false;
        }
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        if (options != null) {
            context.startActivity(intent, options);
        } else {
            context.startActivity(intent);
        }
        return true;
    }

    private static boolean isIntentAvailable(final Intent intent) {
        return DawnBridge.getApp()
                .getPackageManager()
                .queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
                .size() > 0;
    }

    private static boolean startActivityForResult(final Activity activity,
                                                  final Bundle extras,
                                                  final String pkg,
                                                  final String cls,
                                                  final int requestCode,
                                                  @Nullable final Bundle options) {
        Intent intent = new Intent();
        if (extras != null) {
            intent.putExtras(extras);
        }
        intent.setComponent(new ComponentName(pkg, cls));
        return startActivityForResult(intent, activity, requestCode, options);
    }

    private static boolean startActivityForResult(final Intent intent,
                                                  final Activity activity,
                                                  final int requestCode,
                                                  @Nullable final Bundle options) {
        if (!isIntentAvailable(intent)) {
            Log.e("ActivityUtils", "intent is unavailable");
            return false;
        }
        if (options != null) {
            activity.startActivityForResult(intent, requestCode, options);
        } else {
            activity.startActivityForResult(intent, requestCode);
        }
        return true;
    }

    private static void startActivities(final Intent[] intents,
                                        final Context context,
                                        @Nullable final Bundle options) {
        if (!(context instanceof Activity)) {
            for (Intent intent : intents) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
        }
        if (options != null) {
            context.startActivities(intents, options);
        } else {
            context.startActivities(intents);
        }
    }

    private static boolean startActivityForResult(final Fragment fragment,
                                                  final Bundle extras,
                                                  final String pkg,
                                                  final String cls,
                                                  final int requestCode,
                                                  @Nullable final Bundle options) {
        Intent intent = new Intent();
        if (extras != null) {
            intent.putExtras(extras);
        }
        intent.setComponent(new ComponentName(pkg, cls));
        return startActivityForResult(intent, fragment, requestCode, options);
    }

    private static boolean startActivityForResult(final Intent intent,
                                                  final Fragment fragment,
                                                  final int requestCode,
                                                  @Nullable final Bundle options) {
        if (!isIntentAvailable(intent)) {
            Log.e("ActivityUtils", "intent is unavailable");
            return false;
        }
        if (fragment.getActivity() == null) {
            Log.e("ActivityUtils", "Fragment " + fragment + " not attached to Activity");
            return false;
        }
        if (options != null) {
            fragment.startActivityForResult(intent, requestCode, options);
        } else {
            fragment.startActivityForResult(intent, requestCode);
        }
        return true;
    }

    private static Bundle getOptionsBundle(final Fragment fragment,
                                           final int enterAnim,
                                           final int exitAnim) {
        Activity activity = fragment.getActivity();
        if (activity == null) {
            return null;
        }
        return ActivityOptionsCompat.makeCustomAnimation(activity, enterAnim, exitAnim).toBundle();
    }

    private static Bundle getOptionsBundle(final Context context,
                                           final int enterAnim,
                                           final int exitAnim) {
        return ActivityOptionsCompat.makeCustomAnimation(context, enterAnim, exitAnim).toBundle();
    }

    private static Bundle getOptionsBundle(final Fragment fragment,
                                           final View[] sharedElements) {
        Activity activity = fragment.getActivity();
        if (activity == null) {
            return null;
        }
        return getOptionsBundle(activity, sharedElements);
    }

    private static Bundle getOptionsBundle(final Activity activity,
                                           final View[] sharedElements) {
        if (sharedElements == null) {
            return null;
        }
        int len = sharedElements.length;
        if (len <= 0) {
            return null;
        }
        @SuppressWarnings("unchecked")
        Pair<View, String>[] pairs = new Pair[len];
        for (int i = 0; i < len; i++) {
            pairs[i] = Pair.create(sharedElements[i], sharedElements[i].getTransitionName());
        }
        return ActivityOptionsCompat.makeSceneTransitionAnimation(activity, pairs).toBundle();
    }

    private static Context getTopActivityOrApp() {
        if (DawnBridge.isAppForeground()) {
            Activity topActivity = getTopActivity();
            return topActivity == null ? DawnBridge.getApp() : topActivity;
        } else {
            return DawnBridge.getApp();
        }
    }
}
