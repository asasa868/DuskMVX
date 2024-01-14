package com.lzq.dawn.util.activity

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.annotation.AnimRes
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.fragment.app.Fragment
import com.lzq.dawn.DawnBridge
import java.lang.ref.WeakReference

/**
 * className :ActivityUtils
 * createTime :2022/7/8 15:51
 *
 * @Author :  Lzq
 */
object ActivityUtils {
    /**
     * Add callbacks of activity lifecycle.
     *
     * @param callbacks The callbacks.
     */
    fun addActivityLifecycleCallbacks(callbacks: ActivityLifecycleCallbacks?) {
        DawnBridge.addActivityLifecycleCallbacks(callbacks)
    }

    /**
     * Add callbacks of activity lifecycle.
     *
     * @param activity  The activity.
     * @param callbacks The callbacks.
     */
    fun addActivityLifecycleCallbacks(
        activity: Activity?, callbacks: ActivityLifecycleCallbacks?
    ) {
        DawnBridge.addActivityLifecycleCallbacks(activity, callbacks)
    }

    /**
     * Remove callbacks of activity lifecycle.
     *
     * @param callbacks The callbacks.
     */
    fun removeActivityLifecycleCallbacks(callbacks: ActivityLifecycleCallbacks?) {
        DawnBridge.removeActivityLifecycleCallbacks(callbacks)
    }

    /**
     * Remove callbacks of activity lifecycle.
     *
     * @param activity The activity.
     */
    fun removeActivityLifecycleCallbacks(activity: Activity?) {
        DawnBridge.removeActivityLifecycleCallbacks(activity)
    }

    /**
     * Remove callbacks of activity lifecycle.
     *
     * @param activity  The activity.
     * @param callbacks The callbacks.
     */
    fun removeActivityLifecycleCallbacks(
        activity: Activity?, callbacks: ActivityLifecycleCallbacks?
    ) {
        DawnBridge.removeActivityLifecycleCallbacks(activity, callbacks)
    }

    /**
     * 返回活动是否存在。
     *
     * @param pkg 包名
     * @param cls activity类名.
     * @return `true`: 是<br></br>`false`: 否
     */
    fun isActivityExists(
        pkg: String, cls: String
    ): Boolean {
        val intent = Intent()
        intent.setClassName(pkg, cls)
        val pm = DawnBridge.getApp().packageManager
        return !(pm.resolveActivity(
            intent, 0
        ) == null || intent.resolveActivity(pm) == null || pm.queryIntentActivities(intent, 0).size == 0)
    }

    /**
     * 前往 activity
     *
     * @param clz 目标activity .
     */
    fun startActivity(clz: Class<out Activity?>) {
        val context = topActivityOrApp
        startActivity(context, null, context.packageName, clz.name, null)
    }

    /**
     * 前往 activity
     *
     * @param clz     目标activity .
     * @param options 跳转的动画. 使用[ActivityOptionsCompat].toBundle 生成所需要的bundle
     */
    fun startActivity(
        clz: Class<out Activity?>, options: Bundle?
    ) {
        val context = topActivityOrApp
        startActivity(context, null, context.packageName, clz.name, options)
    }

    /**
     * 前往 activity
     *
     * @param clz       activity class.
     * @param enterAnim 进入动画 使用r.anim.xxx 的资源
     * @param exitAnim  退出动画 使用r.anim.xxx 的资源
     */
    fun startActivity(
        clz: Class<out Activity?>, @AnimRes enterAnim: Int, @AnimRes exitAnim: Int
    ) {
        val context = topActivityOrApp
        startActivity(
            context, null, context.packageName, clz.name, getOptionsBundle(context, enterAnim, exitAnim)
        )
    }

    /**
     * 前往 activity
     *
     * @param activity activity.
     * @param clz      目标activity.class.
     */
    fun startActivity(
        activity: Activity, clz: Class<out Activity?>
    ) {
        startActivity(activity, null, activity.packageName, clz.name, null)
    }

    /**
     * 前往 activity
     *
     * @param activity activity.
     * @param clz      目标activity.class.
     * @param options  跳转的动画. 使用[ActivityOptionsCompat].toBundle 生成所需要的bundle
     */
    fun startActivity(
        activity: Activity, clz: Class<out Activity?>, options: Bundle?
    ) {
        startActivity(activity, null, activity.packageName, clz.name, options)
    }

    /**
     * 前往 activity
     *
     * @param activity       activity.
     * @param clz            目标activity.class.
     * @param sharedElements 和目标的view联动的动画 比如平移啥的
     */
    fun startActivity(
        activity: Activity, clz: Class<out Activity?>, vararg sharedElements: View
    ) {
        startActivity(
            activity,
            null,
            activity.packageName,
            clz.name,
            getOptionsBundle(activity, sharedElements.toMutableList().toTypedArray())
        )
    }

    /**
     * 前往 activity
     *
     * @param activity  activity.
     * @param clz       目标activity class.
     * @param enterAnim 进入动画 使用r.anim.xxx 的资源
     * @param exitAnim  退出动画 使用r.anim.xxx 的资源
     */
    fun startActivity(
        activity: Activity, clz: Class<out Activity?>, @AnimRes enterAnim: Int, @AnimRes exitAnim: Int
    ) {
        startActivity(
            activity, null, activity.packageName, clz.name, getOptionsBundle(activity, enterAnim, exitAnim)
        )
    }

    /**
     * 前往 activity
     *
     * @param extras 携带跳转的bundle数据.
     * @param clz    目标activity class.
     */
    fun startActivity(
        extras: Bundle, clz: Class<out Activity?>
    ) {
        val context = topActivityOrApp
        startActivity(context, extras, context.packageName, clz.name, null)
    }

    /**
     * 前往 activity
     *
     * @param extras  携带的bundle数据
     * @param clz     目标activity class.
     * @param options 跳转的动画. 使用[ActivityOptionsCompat].toBundle 生成所需要的bundle
     */
    fun startActivity(
        extras: Bundle, clz: Class<out Activity?>, options: Bundle?
    ) {
        val context = topActivityOrApp
        startActivity(context, extras, context.packageName, clz.name, options)
    }

    /**
     * 前往 activity
     *
     * @param extras    携带的bundle数据
     * @param clz       目标activity class.
     * @param enterAnim 进入动画 使用r.anim.xxx 的资源
     * @param exitAnim  退出动画 使用r.anim.xxx 的资源
     */
    fun startActivity(
        extras: Bundle, clz: Class<out Activity?>, @AnimRes enterAnim: Int, @AnimRes exitAnim: Int
    ) {
        val context = topActivityOrApp
        startActivity(
            context, extras, context.packageName, clz.name, getOptionsBundle(context, enterAnim, exitAnim)
        )
    }

    /**
     * 前往 activity
     *
     * @param extras   携带的bundle数据
     * @param activity 当前activity.
     * @param clz      目标activity class.
     */
    fun startActivity(
        extras: Bundle, activity: Activity, clz: Class<out Activity?>
    ) {
        startActivity(activity, extras, activity.packageName, clz.name, null)
    }

    /**
     * 前往 activity
     *
     * @param extras   携带的bundle数据
     * @param activity 当前activity.
     * @param clz      目标activity class.
     * @param options  跳转的动画. 使用[ActivityOptionsCompat].toBundle 生成所需要的bundle
     */
    fun startActivity(
        extras: Bundle, activity: Activity, clz: Class<out Activity?>, options: Bundle?
    ) {
        startActivity(activity, extras, activity.packageName, clz.name, options)
    }

    /**
     * 前往 activity
     *
     * @param activity       activity.
     * @param clz            目标activity.class.
     * @param sharedElements 和目标的view联动的动画 比如平移啥的
     * @param extras         携带的bundle数据
     */
    fun startActivity(
        extras: Bundle, activity: Activity, clz: Class<out Activity?>, vararg sharedElements: View
    ) {
        startActivity(
            activity,
            extras,
            activity.packageName,
            clz.name,
            getOptionsBundle(activity, sharedElements.toMutableList().toTypedArray())
        )
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
    fun startActivity(
        extras: Bundle,
        activity: Activity,
        clz: Class<out Activity?>,
        @AnimRes enterAnim: Int,
        @AnimRes exitAnim: Int
    ) {
        startActivity(
            activity, extras, activity.packageName, clz.name, getOptionsBundle(activity, enterAnim, exitAnim)
        )
    }

    /**
     * 前往 activity
     *
     * @param pkg 包名
     * @param cls 目标activity.class.
     */
    fun startActivity(
        pkg: String, cls: String
    ) {
        startActivity(topActivityOrApp, null, pkg, cls, null)
    }

    /**
     * 前往 activity
     *
     * @param pkg     包名
     * @param cls     目标activity.class.
     * @param options 跳转的动画. 使用[ActivityOptionsCompat].toBundle 生成所需要的bundle
     */
    fun startActivity(
        pkg: String, cls: String, options: Bundle?
    ) {
        startActivity(topActivityOrApp, null, pkg, cls, options)
    }

    /**
     * 前往 activity
     *
     * @param pkg       包名
     * @param cls       目标activity.class.
     * @param enterAnim 进入动画 使用r.anim.xxx 的资源
     * @param exitAnim  退出动画 使用r.anim.xxx 的资源
     */
    fun startActivity(
        pkg: String, cls: String, @AnimRes enterAnim: Int, @AnimRes exitAnim: Int
    ) {
        val context = topActivityOrApp
        startActivity(context, null, pkg, cls, getOptionsBundle(context, enterAnim, exitAnim))
    }

    /**
     * 前往 activity
     *
     * @param pkg      包名
     * @param cls      目标activity.class.
     * @param activity 当前 activity.
     */
    fun startActivity(
        activity: Activity, pkg: String, cls: String
    ) {
        startActivity(activity, null, pkg, cls, null)
    }

    /**
     * 前往 activity
     *
     * @param pkg      包名
     * @param cls      目标activity.class.
     * @param activity 当前 activity.
     * @param options  跳转的动画. 使用[ActivityOptionsCompat.toBundle] 生成所需要的bundle
     */
    fun startActivity(
        activity: Activity, pkg: String, cls: String, options: Bundle?
    ) {
        startActivity(activity, null, pkg, cls, options)
    }

    /**
     * 前往 activity
     *
     * @param activity       当前 activity.
     * @param pkg            包名
     * @param cls            目标activity.class.
     * @param sharedElements 和目标的view联动的动画 比如平移啥的
     */
    fun startActivity(
        activity: Activity, pkg: String, cls: String, vararg sharedElements: View
    ) {
        startActivity(
            activity,
            null,
            pkg,
            cls,
            getOptionsBundle(activity, sharedElements.toMutableList().toTypedArray())
        )
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
    fun startActivity(
        activity: Activity, pkg: String, cls: String, @AnimRes enterAnim: Int, @AnimRes exitAnim: Int
    ) {
        startActivity(activity, null, pkg, cls, getOptionsBundle(activity, enterAnim, exitAnim))
    }

    /**
     * 前往 activity
     *
     * @param extras 携带的bundle数据
     * @param pkg    包名
     * @param cls    目标activity.class.
     */
    fun startActivity(
        extras: Bundle, pkg: String, cls: String
    ) {
        startActivity(topActivityOrApp, extras, pkg, cls, null)
    }

    /**
     * 前往 activity
     *
     * @param extras  携带的bundle数据
     * @param pkg     包名
     * @param cls     目标activity.class.
     * @param options 跳转的动画. 使用[ActivityOptionsCompat.toBundle] 生成所需要的bundle
     */
    fun startActivity(
        extras: Bundle, pkg: String, cls: String, options: Bundle?
    ) {
        startActivity(topActivityOrApp, extras, pkg, cls, options)
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
    fun startActivity(
        extras: Bundle, pkg: String, cls: String, @AnimRes enterAnim: Int, @AnimRes exitAnim: Int
    ) {
        val context = topActivityOrApp
        startActivity(context, extras, pkg, cls, getOptionsBundle(context, enterAnim, exitAnim))
    }

    /**
     * 前往 activity
     *
     * @param activity activity.
     * @param extras   携带的bundle数据
     * @param pkg      包名
     * @param cls      目标activity.class.
     */
    fun startActivity(
        extras: Bundle, activity: Activity, pkg: String, cls: String
    ) {
        startActivity(activity, extras, pkg, cls, null)
    }

    /**
     * 前往 activity
     *
     * @param activity activity.
     * @param extras   携带的bundle数据
     * @param pkg      包名
     * @param cls      目标activity.class.
     * @param options  跳转的动画. 使用[ActivityOptionsCompat.toBundle] 生成所需要的bundle
     */
    fun startActivity(
        extras: Bundle, activity: Activity, pkg: String, cls: String, options: Bundle?
    ) {
        startActivity(activity, extras, pkg, cls, options)
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
    fun startActivity(
        extras: Bundle, activity: Activity, pkg: String, cls: String, vararg sharedElements: View
    ) {
        startActivity(
            activity,
            extras,
            pkg,
            cls,
            getOptionsBundle(activity, sharedElements.toMutableList().toTypedArray())
        )
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
    fun startActivity(
        extras: Bundle,
        activity: Activity,
        pkg: String,
        cls: String,
        @AnimRes enterAnim: Int,
        @AnimRes exitAnim: Int
    ) {
        startActivity(activity, extras, pkg, cls, getOptionsBundle(activity, enterAnim, exitAnim))
    }

    /**
     * 前往 activity
     *
     * @param intent intent
     * @return `true`: success<br></br>`false`: fail
     */
    fun startActivity(intent: Intent): Boolean {
        return startActivity(intent, topActivityOrApp, null)
    }

    /**
     * 前往 activity
     *
     * @param intent  intent
     * @param options 跳转的动画. 使用[ActivityOptionsCompat.toBundle] 生成所需要的bundle
     * @return `true`: success<br></br>`false`: fail
     */
    fun startActivity(
        intent: Intent, options: Bundle?
    ): Boolean {
        return startActivity(intent, topActivityOrApp, options)
    }

    /**
     * 前往 activity
     *
     * @param intent    intent
     * @param enterAnim 进入动画 使用r.anim.xxx 的资源
     * @param exitAnim  退出动画 使用r.anim.xxx 的资源
     * @return `true`: success<br></br>`false`: fail
     */
    fun startActivity(
        intent: Intent, @AnimRes enterAnim: Int, @AnimRes exitAnim: Int
    ): Boolean {
        val context = topActivityOrApp
        return startActivity(intent, context, getOptionsBundle(context, enterAnim, exitAnim))
    }

    /**
     * 前往 activity
     *
     * @param activity activity.
     * @param intent   intent
     */
    fun startActivity(
        activity: Activity, intent: Intent
    ) {
        startActivity(intent, activity, null)
    }

    /**
     * 前往 activity
     *
     * @param activity activity.
     * @param intent   intent
     * @param options  跳转的动画. 使用[ActivityOptionsCompat.toBundle] 生成所需要的bundle
     */
    fun startActivity(
        activity: Activity, intent: Intent, options: Bundle?
    ) {
        startActivity(intent, activity, options)
    }

    /**
     * 前往 activity
     *
     * @param activity       当前 activity.
     * @param intent         intent
     * @param sharedElements 和目标的view联动的动画 比如平移啥的
     */
    fun startActivity(
        activity: Activity, intent: Intent, vararg sharedElements: View
    ) {
        startActivity(
            intent,
            activity,
            getOptionsBundle(activity, sharedElements.toMutableList().toTypedArray())
        )
    }

    /**
     * 前往 activity
     *
     * @param activity  activity.
     * @param intent    intent
     * @param enterAnim 进入动画 使用r.anim.xxx 的资源
     * @param exitAnim  退出动画 使用r.anim.xxx 的资源
     */
    fun startActivity(
        activity: Activity, intent: Intent, @AnimRes enterAnim: Int, @AnimRes exitAnim: Int
    ) {
        startActivity(intent, activity, getOptionsBundle(activity, enterAnim, exitAnim))
    }

    /**
     * 前往 activity
     *
     * @param activity    activity.
     * @param clz         目标activity.class
     * @param requestCode 如果 >= 0，则活动退出时将在 onActivityResult() 中返回结果
     */
    fun startActivityForResult(
        activity: Activity, clz: Class<out Activity?>, requestCode: Int
    ) {
        startActivityForResult(
            activity, null, activity.packageName, clz.name, requestCode, null
        )
    }

    /**
     * 前往 activity
     *
     * @param activity    activity.
     * @param clz         目标activity.class
     * @param requestCode 如果 >= 0，则活动退出时将在 onActivityResult() 中返回结果
     * @param options     跳转的动画. 使用[ActivityOptionsCompat.toBundle] 生成所需要的bundle
     */
    fun startActivityForResult(
        activity: Activity, clz: Class<out Activity?>, requestCode: Int, options: Bundle?
    ) {
        startActivityForResult(
            activity, null, activity.packageName, clz.name, requestCode, options
        )
    }

    /**
     * 前往 activity
     *
     * @param activity       当前 activity.
     * @param clz            目标activity.class
     * @param requestCode    如果 >= 0，则活动退出时将在 onActivityResult() 中返回结果
     * @param sharedElements 和目标的view联动的动画 比如平移啥的
     */
    fun startActivityForResult(
        activity: Activity, clz: Class<out Activity?>, requestCode: Int, vararg sharedElements: View
    ) {
        startActivityForResult(
            activity,
            null,
            activity.packageName,
            clz.name,
            requestCode,
            getOptionsBundle(activity, sharedElements.toMutableList().toTypedArray())
        )
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
    fun startActivityForResult(
        activity: Activity,
        clz: Class<out Activity?>,
        requestCode: Int,
        @AnimRes enterAnim: Int,
        @AnimRes exitAnim: Int
    ) {
        startActivityForResult(
            activity,
            null,
            activity.packageName,
            clz.name,
            requestCode,
            getOptionsBundle(activity, enterAnim, exitAnim)
        )
    }

    /**
     * 前往 activity
     *
     * @param extras      携带的bundle数据
     * @param activity    当前 activity.
     * @param clz         目标activity.class
     * @param requestCode 如果 >= 0，则活动退出时将在 onActivityResult() 中返回结果
     */
    fun startActivityForResult(
        extras: Bundle, activity: Activity, clz: Class<out Activity?>, requestCode: Int
    ) {
        startActivityForResult(
            activity, extras, activity.packageName, clz.name, requestCode, null
        )
    }

    /**
     * 前往 activity
     *
     * @param extras      携带的bundle数据
     * @param activity    当前 activity.
     * @param clz         目标activity.class
     * @param requestCode 如果 >= 0，则活动退出时将在 onActivityResult() 中返回结果
     * @param options     跳转的动画. 使用[ActivityOptionsCompat.toBundle] 生成所需要的bundle
     */
    fun startActivityForResult(
        extras: Bundle, activity: Activity, clz: Class<out Activity?>, requestCode: Int, options: Bundle?
    ) {
        startActivityForResult(
            activity, extras, activity.packageName, clz.name, requestCode, options
        )
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
    fun startActivityForResult(
        extras: Bundle,
        activity: Activity,
        clz: Class<out Activity?>,
        requestCode: Int,
        vararg sharedElements: View
    ) {
        startActivityForResult(
            activity,
            extras,
            activity.packageName,
            clz.name,
            requestCode,
            getOptionsBundle(activity, sharedElements.toMutableList().toTypedArray())
        )
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
    fun startActivityForResult(
        extras: Bundle,
        activity: Activity,
        clz: Class<out Activity?>,
        requestCode: Int,
        @AnimRes enterAnim: Int,
        @AnimRes exitAnim: Int
    ) {
        startActivityForResult(
            activity,
            extras,
            activity.packageName,
            clz.name,
            requestCode,
            getOptionsBundle(activity, enterAnim, exitAnim)
        )
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
    fun startActivityForResult(
        extras: Bundle, activity: Activity, pkg: String, cls: String, requestCode: Int
    ) {
        startActivityForResult(activity, extras, pkg, cls, requestCode, null)
    }

    /**
     * 前往活动并在活动结束时获取结果
     *
     * @param extras      携带的bundle数据
     * @param activity    当前 activity.
     * @param pkg         包名
     * @param cls         目标activity.class
     * @param requestCode 如果 >= 0，则活动退出时将在 onActivityResult() 中返回结果
     * @param options     跳转的动画. 使用[ActivityOptionsCompat.toBundle] 生成所需要的bundle
     */
    fun startActivityForResult(
        extras: Bundle, activity: Activity, pkg: String, cls: String, requestCode: Int, options: Bundle?
    ) {
        startActivityForResult(activity, extras, pkg, cls, requestCode, options)
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
    fun startActivityForResult(
        extras: Bundle,
        activity: Activity,
        pkg: String,
        cls: String,
        requestCode: Int,
        vararg sharedElements: View
    ) {
        startActivityForResult(
            activity,
            extras,
            pkg,
            cls,
            requestCode,
            getOptionsBundle(activity, sharedElements.toMutableList().toTypedArray())
        )
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
    fun startActivityForResult(
        extras: Bundle,
        activity: Activity,
        pkg: String,
        cls: String,
        requestCode: Int,
        @AnimRes enterAnim: Int,
        @AnimRes exitAnim: Int
    ) {
        startActivityForResult(
            activity, extras, pkg, cls, requestCode, getOptionsBundle(activity, enterAnim, exitAnim)
        )
    }

    /**
     * 前往活动并在活动结束时获取结果
     *
     * @param activity    The activity.
     * @param intent      intent
     * @param requestCode 如果 >= 0，则活动退出时将在 onActivityResult() 中返回结果
     */
    fun startActivityForResult(
        activity: Activity, intent: Intent, requestCode: Int
    ) {
        startActivityForResult(intent, activity, requestCode, null)
    }

    /**
     * 前往活动并在活动结束时获取结果
     *
     * @param activity    The activity.
     * @param intent      intent
     * @param requestCode 如果 >= 0，则活动退出时将在 onActivityResult() 中返回结果
     * @param options     跳转的动画. 使用[ActivityOptionsCompat.toBundle] 生成所需要的bundle
     */
    fun startActivityForResult(
        activity: Activity, intent: Intent, requestCode: Int, options: Bundle?
    ) {
        startActivityForResult(intent, activity, requestCode, options)
    }

    /**
     * 前往活动并在活动结束时获取结果
     *
     * @param activity       当前 activity.
     * @param intent         The description of the activity to start.
     * @param requestCode    如果 >= 0，则活动退出时将在 onActivityResult() 中返回结果
     * @param sharedElements 和目标的view联动的动画 比如平移啥的
     */
    fun startActivityForResult(
        activity: Activity, intent: Intent, requestCode: Int, vararg sharedElements: View
    ) {
        startActivityForResult(
            intent,
            activity,
            requestCode,
            getOptionsBundle(activity, sharedElements.toMutableList().toTypedArray())
        )
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
    fun startActivityForResult(
        activity: Activity, intent: Intent, requestCode: Int, @AnimRes enterAnim: Int, @AnimRes exitAnim: Int
    ) {
        startActivityForResult(
            intent, activity, requestCode, getOptionsBundle(activity, enterAnim, exitAnim)
        )
    }

    /**
     * 前往 activity
     *
     * @param fragment    fragment
     * @param clz         The activity class.
     * @param requestCode 如果 >= 0，则活动退出时将在 onActivityResult() 中返回结果
     */
    fun startActivityForResult(
        fragment: Fragment, clz: Class<out Activity?>, requestCode: Int
    ) {
        startActivityForResult(
            fragment, null, DawnBridge.getApp().packageName, clz.name, requestCode, null
        )
    }

    /**
     * 前往 activity
     *
     * @param fragment    fragment
     * @param clz         The activity class.
     * @param requestCode 如果 >= 0，则活动退出时将在 onActivityResult() 中返回结果
     * @param options     跳转的动画. 使用[ActivityOptionsCompat.toBundle] 生成所需要的bundle
     */
    fun startActivityForResult(
        fragment: Fragment, clz: Class<out Activity?>, requestCode: Int, options: Bundle?
    ) {
        startActivityForResult(
            fragment, null, DawnBridge.getApp().packageName, clz.name, requestCode, options
        )
    }

    /**
     * 前往 activity
     *
     * @param fragment       The fragment.
     * @param clz            The activity class.
     * @param requestCode    如果 >= 0，则活动退出时将在 onActivityResult() 中返回结果
     * @param sharedElements 和目标的view联动的动画 比如平移啥的
     */
    fun startActivityForResult(
        fragment: Fragment, clz: Class<out Activity?>, requestCode: Int, vararg sharedElements: View
    ) {
        startActivityForResult(
            fragment,
            null,
            DawnBridge.getApp().packageName,
            clz.name,
            requestCode,
            getOptionsBundle(fragment, sharedElements.toMutableList().toTypedArray())
        )
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
    fun startActivityForResult(
        fragment: Fragment,
        clz: Class<out Activity?>,
        requestCode: Int,
        @AnimRes enterAnim: Int,
        @AnimRes exitAnim: Int
    ) {
        startActivityForResult(
            fragment,
            null,
            DawnBridge.getApp().packageName,
            clz.name,
            requestCode,
            getOptionsBundle(fragment, enterAnim, exitAnim)
        )
    }

    /**
     * 前往 activity
     *
     * @param extras      携带的bundle数据
     * @param fragment    fragment
     * @param clz         The activity class.
     * @param requestCode 如果 >= 0，则活动退出时将在 onActivityResult() 中返回结果
     */
    fun startActivityForResult(
        extras: Bundle, fragment: Fragment, clz: Class<out Activity?>, requestCode: Int
    ) {
        startActivityForResult(
            fragment, extras, DawnBridge.getApp().packageName, clz.name, requestCode, null
        )
    }

    /**
     * 前往 activity
     *
     * @param extras      携带的bundle数据
     * @param fragment    fragment
     * @param clz         The activity class.
     * @param requestCode 如果 >= 0，则活动退出时将在 onActivityResult() 中返回结果
     * @param options     跳转的动画. 使用[ActivityOptionsCompat.toBundle] 生成所需要的bundle
     */
    fun startActivityForResult(
        extras: Bundle, fragment: Fragment, clz: Class<out Activity?>, requestCode: Int, options: Bundle?
    ) {
        startActivityForResult(
            fragment, extras, DawnBridge.getApp().packageName, clz.name, requestCode, options
        )
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
    fun startActivityForResult(
        extras: Bundle,
        fragment: Fragment,
        clz: Class<out Activity?>,
        requestCode: Int,
        vararg sharedElements: View
    ) {
        startActivityForResult(
            fragment,
            extras,
            DawnBridge.getApp().packageName,
            clz.name,
            requestCode,
            getOptionsBundle(fragment, sharedElements.toMutableList().toTypedArray())
        )
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
    fun startActivityForResult(
        extras: Bundle,
        fragment: Fragment,
        clz: Class<out Activity?>,
        requestCode: Int,
        @AnimRes enterAnim: Int,
        @AnimRes exitAnim: Int
    ) {
        startActivityForResult(
            fragment,
            extras,
            DawnBridge.getApp().packageName,
            clz.name,
            requestCode,
            getOptionsBundle(fragment, enterAnim, exitAnim)
        )
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
    fun startActivityForResult(
        extras: Bundle, fragment: Fragment, pkg: String, cls: String, requestCode: Int
    ) {
        startActivityForResult(fragment, extras, pkg, cls, requestCode, null)
    }

    /**
     * 前往活动并在活动结束时获取结果
     *
     * @param extras      携带的bundle数据
     * @param fragment    fragment
     * @param pkg         包名
     * @param cls         目标activity.class.
     * @param requestCode 如果 >= 0，则活动退出时将在 onActivityResult() 中返回结果
     * @param options     跳转的动画. 使用[ActivityOptionsCompat.toBundle] 生成所需要的bundle
     */
    fun startActivityForResult(
        extras: Bundle, fragment: Fragment, pkg: String, cls: String, requestCode: Int, options: Bundle?
    ) {
        startActivityForResult(fragment, extras, pkg, cls, requestCode, options)
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
    fun startActivityForResult(
        extras: Bundle,
        fragment: Fragment,
        pkg: String,
        cls: String,
        requestCode: Int,
        vararg sharedElements: View
    ) {
        startActivityForResult(
            fragment,
            extras,
            pkg,
            cls,
            requestCode,
            getOptionsBundle(fragment, sharedElements.toMutableList().toTypedArray())
        )
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
    fun startActivityForResult(
        extras: Bundle,
        fragment: Fragment,
        pkg: String,
        cls: String,
        requestCode: Int,
        @AnimRes enterAnim: Int,
        @AnimRes exitAnim: Int
    ) {
        startActivityForResult(
            fragment, extras, pkg, cls, requestCode, getOptionsBundle(fragment, enterAnim, exitAnim)
        )
    }

    /**
     * 前往活动并在活动结束时获取结果
     *
     * @param fragment    fragment
     * @param intent      intent
     * @param requestCode 如果 >= 0，则活动退出时将在 onActivityResult() 中返回结果
     */
    fun startActivityForResult(
        fragment: Fragment, intent: Intent, requestCode: Int
    ) {
        startActivityForResult(intent, fragment, requestCode, null)
    }

    /**
     * 前往活动并在活动结束时获取结果
     *
     * @param fragment    fragment
     * @param intent      intent
     * @param requestCode 如果 >= 0，则活动退出时将在 onActivityResult() 中返回结果
     * @param options     跳转的动画. 使用[ActivityOptionsCompat.toBundle] 生成所需要的bundle
     */
    fun startActivityForResult(
        fragment: Fragment, intent: Intent, requestCode: Int, options: Bundle?
    ) {
        startActivityForResult(intent, fragment, requestCode, options)
    }

    /**
     * 前往活动并在活动结束时获取结果
     *
     * @param fragment       fragment.
     * @param intent         intent
     * @param requestCode    如果 >= 0，则活动退出时将在 onActivityResult() 中返回结果
     * @param sharedElements 和目标的view联动的动画 比如平移啥的
     */
    fun startActivityForResult(
        fragment: Fragment, intent: Intent, requestCode: Int, vararg sharedElements: View
    ) {


        startActivityForResult(
            intent, fragment, requestCode, getOptionsBundle(
                fragment, sharedElements.toMutableList().toTypedArray()
            )
        )
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
    fun startActivityForResult(
        fragment: Fragment, intent: Intent, requestCode: Int, @AnimRes enterAnim: Int, @AnimRes exitAnim: Int
    ) {
        startActivityForResult(
            intent, fragment, requestCode, getOptionsBundle(fragment, enterAnim, exitAnim)
        )
    }

    /**
     * Start activities.
     *
     * @param intents The descriptions of the activities to start.
     */
    fun startActivities(intents: Array<Intent>) {
        startActivities(intents, topActivityOrApp, null)
    }

    /**
     * Start activities.
     *
     * @param intents The descriptions of the activities to start.
     * @param options Additional options for how the Activity should be started.
     */
    fun startActivities(
        intents: Array<Intent>, options: Bundle?
    ) {
        startActivities(intents, topActivityOrApp, options)
    }

    /**
     * 前往activity
     *
     * @param intents   前往activity的intents
     * @param enterAnim 进入动画 使用r.anim.xxx 的资源
     * @param exitAnim  退出动画 使用r.anim.xxx 的资源
     */
    fun startActivities(
        intents: Array<Intent>, @AnimRes enterAnim: Int, @AnimRes exitAnim: Int
    ) {
        val context = topActivityOrApp
        startActivities(intents, context, getOptionsBundle(context, enterAnim, exitAnim))
    }

    /**
     * Start activities.
     *
     * @param activity activity.
     * @param intents  The descriptions of the activities to start.
     */
    fun startActivities(
        activity: Activity, intents: Array<Intent>
    ) {
        startActivities(intents, activity, null)
    }

    /**
     * Start activities.
     *
     * @param activity The activity.
     * @param intents  The descriptions of the activities to start.
     * @param options  Additional options for how the Activity should be started.
     */
    fun startActivities(
        activity: Activity, intents: Array<Intent>, options: Bundle?
    ) {
        startActivities(intents, activity, options)
    }

    /**
     * Start activities.
     *
     * @param activity  The activity.
     * @param intents   The descriptions of the activities to start.
     * @param enterAnim 进入动画 使用r.anim.xxx 的资源
     * @param exitAnim  退出动画 使用r.anim.xxx 的资源
     */
    fun startActivities(
        activity: Activity, intents: Array<Intent>, @AnimRes enterAnim: Int, @AnimRes exitAnim: Int
    ) {
        startActivities(intents, activity, getOptionsBundle(activity, enterAnim, exitAnim))
    }

    /**
     * 前往桌面（相当于按了返回桌面按钮）
     */
    @JvmStatic
    fun startHomeActivity() {
        val homeIntent = Intent(Intent.ACTION_MAIN)
        homeIntent.addCategory(Intent.CATEGORY_HOME)
        homeIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(homeIntent)
    }
    /**
     * 前往main页面 就是在清单列表设置main的activity
     *
     * @param pkg 包名
     */
    /**
     * 前往main页面 就是在清单列表设置main的activity
     */
    @JvmOverloads
    fun startLauncherActivity(pkg: String = DawnBridge.getApp().packageName) {
        val launcherActivity = getLauncherActivity(pkg)
        if (TextUtils.isEmpty(launcherActivity)) {
            return
        }
        startActivity(pkg, launcherActivity)
    }

    val activityList: List<Activity>
        /**
         * Return  activity 列表
         *
         * @return the list of activity
         */
        get() = DawnBridge.getActivityList()
    val launcherActivity: String
        /**
         * Return launcher activity的名字
         *
         * @return the name of launcher activity
         */
        get() = getLauncherActivity(DawnBridge.getApp().packageName)

    /**
     * Return launcher activity的名字
     *
     * @param pkg The name of the package.
     * @return the name of launcher activity
     */
    @JvmStatic
    fun getLauncherActivity(pkg: String): String {
        if (DawnBridge.isSpace(pkg)) {
            return ""
        }
        val intent = Intent(Intent.ACTION_MAIN, null)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        intent.setPackage(pkg)
        val pm = DawnBridge.getApp().packageManager
        val info = pm.queryIntentActivities(intent, 0)
        return if (info == null || info.size == 0) {
            ""
        } else info[0].activityInfo.name
    }

    val mainActivities: List<String>
        /**
         * Return the list of main activities.
         *
         * @return the list of main activities
         */
        get() = getMainActivities(DawnBridge.getApp().packageName)

    /**
     * Return the list of main activities.
     *
     * @param pkg The name of the package.
     * @return the list of main activities
     */
    fun getMainActivities(pkg: String): List<String> {
        val ret: MutableList<String> = ArrayList()
        val intent = Intent(Intent.ACTION_MAIN, null)
        intent.setPackage(pkg)
        val pm = DawnBridge.getApp().packageManager
        val info = pm.queryIntentActivities(intent, 0)
        val size = info.size
        if (size == 0) {
            return ret
        }
        for (i in 0 until size) {
            val ri = info[i]
            if (ri.activityInfo.processName == pkg) {
                ret.add(ri.activityInfo.name)
            }
        }
        return ret
    }

    val topActivity: Activity
        /**
         * Return  栈定的activity
         *
         * @return the top activity in activity's stack
         */
        get() = DawnBridge.getTopActivity()

    /**
     * 指定activity是否在活动状态
     *
     * @param context The context.
     * @return `true`: yes<br></br>`false`: no
     */
    fun isActivityAlive(context: Context?): Boolean {
        return isActivityAlive(getActivityByContext(context))
    }

    /**
     * 指定activity是否在活动状态
     *
     * @param activity The activity.
     * @return `true`: yes<br></br>`false`: no
     */
    @JvmStatic
    fun isActivityAlive(activity: Activity?): Boolean {
        return activity != null && !activity.isFinishing && !activity.isDestroyed
    }

    /**
     * 返回activity是否存在于活动的堆栈中。
     *
     * @param activity The activity.
     * @return `true`: yes<br></br>`false`: no
     */
    fun isActivityExistsInStack(activity: Activity): Boolean {
        val activities = DawnBridge.getActivityList()
        for (aActivity in activities) {
            if (aActivity == activity) {
                return true
            }
        }
        return false
    }

    /**
     * 返回activity是否存在于活动的堆栈中。
     *
     * @param clz The activity class.
     * @return `true`: yes<br></br>`false`: no
     */
    fun isActivityExistsInStack(clz: Class<out Activity?>): Boolean {
        val activities = DawnBridge.getActivityList()
        for (aActivity in activities) {
            if (aActivity.javaClass == clz) {
                return true
            }
        }
        return false
    }
    /**
     * 结束activity
     *
     * @param activity   The activity.
     * @param isLoadAnim True 为传出活动使用动画，否则为 false。
     */
    /**
     * 结束activity
     *
     * @param activity The activity.
     */
    @JvmOverloads
    fun finishActivity(activity: Activity, isLoadAnim: Boolean = false) {
        activity.finish()
        if (!isLoadAnim) {
            activity.overridePendingTransition(0, 0)
        }
    }

    /**
     * 结束activity
     *
     * @param activity  The activity.
     * @param enterAnim 进入动画 使用r.anim.xxx 的资源
     * @param exitAnim  退出动画 使用r.anim.xxx 的资源
     */
    fun finishActivity(
        activity: Activity, @AnimRes enterAnim: Int, @AnimRes exitAnim: Int
    ) {
        activity.finish()
        activity.overridePendingTransition(enterAnim, exitAnim)
    }
    /**
     * 结束activity
     *
     * @param clz        The activity class.
     * @param isLoadAnim True to use animation for the outgoing activity, false otherwise.
     */
    /**
     * 结束activity
     *
     * @param clz The activity class.
     */
    @JvmOverloads
    fun finishActivity(
        clz: Class<out Activity?>, isLoadAnim: Boolean = false
    ) {
        val activities = DawnBridge.getActivityList()
        for (activity in activities) {
            if (activity.javaClass == clz) {
                activity.finish()
                if (!isLoadAnim) {
                    activity.overridePendingTransition(0, 0)
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
    fun finishActivity(
        clz: Class<out Activity?>, @AnimRes enterAnim: Int, @AnimRes exitAnim: Int
    ) {
        val activities = DawnBridge.getActivityList()
        for (activity in activities) {
            if (activity.javaClass == clz) {
                activity.finish()
                activity.overridePendingTransition(enterAnim, exitAnim)
            }
        }
    }
    /**
     * 结束activity
     *
     * @param activity      The activity.
     * @param isIncludeSelf 如果包含活动，则为 true，否则为 false。
     * @param isLoadAnim    动画
     */
    /**
     * 结束activity
     *
     * @param activity      The activity.
     * @param isIncludeSelf 如果包含活动，则为 true，否则为 false。
     */
    @JvmOverloads
    fun finishToActivity(
        activity: Activity, isIncludeSelf: Boolean, isLoadAnim: Boolean = false
    ): Boolean {
        val activities = DawnBridge.getActivityList()
        for (act in activities) {
            if (act == activity) {
                if (isIncludeSelf) {
                    finishActivity(act, isLoadAnim)
                }
                return true
            }
            finishActivity(act, isLoadAnim)
        }
        return false
    }

    /**
     * 结束activity
     *
     * @param activity      The activity.
     * @param isIncludeSelf 如果包含活动，则为 true，否则为 false。
     * @param enterAnim     进入动画 使用r.anim.xxx 的资源
     * @param exitAnim      退出动画 使用r.anim.xxx 的资源
     */
    fun finishToActivity(
        activity: Activity, isIncludeSelf: Boolean, @AnimRes enterAnim: Int, @AnimRes exitAnim: Int
    ): Boolean {
        val activities = DawnBridge.getActivityList()
        for (act in activities) {
            if (act == activity) {
                if (isIncludeSelf) {
                    finishActivity(act, enterAnim, exitAnim)
                }
                return true
            }
            finishActivity(act, enterAnim, exitAnim)
        }
        return false
    }
    /**
     * 结束activity
     *
     * @param clz           The activity class.
     * @param isIncludeSelf 如果包含活动，则为 true，否则为 false。
     * @param isLoadAnim    动画
     */
    /**
     * 结束activity
     *
     * @param clz           The activity class.
     * @param isIncludeSelf 如果包含活动，则为 true，否则为 false。
     */
    @JvmOverloads
    fun finishToActivity(
        clz: Class<out Activity?>, isIncludeSelf: Boolean, isLoadAnim: Boolean = false
    ): Boolean {
        val activities = DawnBridge.getActivityList()
        for (act in activities) {
            if (act.javaClass == clz) {
                if (isIncludeSelf) {
                    finishActivity(act, isLoadAnim)
                }
                return true
            }
            finishActivity(act, isLoadAnim)
        }
        return false
    }

    /**
     * 结束activity
     *
     * @param clz           The activity class.
     * @param isIncludeSelf 如果包含活动，则为 true，否则为 false。
     * @param enterAnim     进入动画 使用r.anim.xxx 的资源
     * @param exitAnim      退出动画 使用r.anim.xxx 的资源
     */
    fun finishToActivity(
        clz: Class<out Activity?>, isIncludeSelf: Boolean, @AnimRes enterAnim: Int, @AnimRes exitAnim: Int
    ): Boolean {
        val activities = DawnBridge.getActivityList()
        for (act in activities) {
            if (act.javaClass == clz) {
                if (isIncludeSelf) {
                    finishActivity(act, enterAnim, exitAnim)
                }
                return true
            }
            finishActivity(act, enterAnim, exitAnim)
        }
        return false
    }
    /**
     * Finish 类型不等于activity类别的activity
     *
     * @param clz        The activity class.
     * @param isLoadAnim 动画
     */
    /**
     * Finish 类型不等于activity类别的activity
     *
     * @param clz The activity class.
     */
    @JvmOverloads
    fun finishOtherActivities(
        clz: Class<out Activity?>, isLoadAnim: Boolean = false
    ) {
        val activities = DawnBridge.getActivityList()
        for (act in activities) {
            if (act.javaClass != clz) {
                finishActivity(act, isLoadAnim)
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
    fun finishOtherActivities(
        clz: Class<out Activity?>, @AnimRes enterAnim: Int, @AnimRes exitAnim: Int
    ) {
        val activities = DawnBridge.getActivityList()
        for (act in activities) {
            if (act.javaClass != clz) {
                finishActivity(act, enterAnim, exitAnim)
            }
        }
    }
    /**
     * Finish 所有 activities.
     *
     * @param isLoadAnim 动画
     */
    /**
     * Finish 所有 activities.
     */
    @JvmOverloads
    @JvmStatic
    fun finishAllActivities(isLoadAnim: Boolean = false) {
        val activityList = DawnBridge.getActivityList()
        for (act in activityList) {
            // sActivityList remove the index activity at onActivityDestroyed
            act.finish()
            if (!isLoadAnim) {
                act.overridePendingTransition(0, 0)
            }
        }
    }

    /**
     * Finish 所有 activities.
     *
     * @param enterAnim 进入动画 使用r.anim.xxx 的资源
     * @param exitAnim  退出动画 使用r.anim.xxx 的资源
     */
    fun finishAllActivities(
        @AnimRes enterAnim: Int, @AnimRes exitAnim: Int
    ) {
        val activityList = DawnBridge.getActivityList()
        for (act in activityList) {
            // sActivityList remove the index activity at onActivityDestroyed
            act.finish()
            act.overridePendingTransition(enterAnim, exitAnim)
        }
    }
    /**
     * finish除栈顶activity外的所有activity
     *
     * @param isLoadAnim 动画
     */
    /**
     * finish除栈顶activity外的所有activity
     */
    @JvmOverloads
    fun finishAllActivitiesExceptNewest(isLoadAnim: Boolean = false) {
        val activities = DawnBridge.getActivityList()
        for (i in 1 until activities.size) {
            finishActivity(activities[i], isLoadAnim)
        }
    }

    /**
     * finish除栈顶activity外的所有activity
     *
     * @param enterAnim 进入动画 使用r.anim.xxx 的资源
     * @param exitAnim  退出动画 使用r.anim.xxx 的资源
     */
    fun finishAllActivitiesExceptNewest(
        @AnimRes enterAnim: Int, @AnimRes exitAnim: Int
    ) {
        val activities = DawnBridge.getActivityList()
        for (i in 1 until activities.size) {
            finishActivity(activities[i], enterAnim, exitAnim)
        }
    }

    /**
     * Return activity 的icon.
     *
     * @param activity The activity.
     * @return activity 的icon
     */
    fun getActivityIcon(activity: Activity): Drawable? {
        return getActivityIcon(activity.componentName)
    }

    /**
     * Return activity 的icon.
     *
     * @param clz The activity class.
     * @return activity 的icon
     */
    fun getActivityIcon(clz: Class<out Activity?>): Drawable? {
        return getActivityIcon(ComponentName(DawnBridge.getApp(), clz))
    }

    /**
     * Return activity 的icon.
     *
     * @param activityName The name of activity.
     * @return activity 的icon
     */
    fun getActivityIcon(activityName: ComponentName): Drawable? {
        val pm = DawnBridge.getApp().packageManager
        return try {
            pm.getActivityIcon(activityName)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Return activity 的logo.
     *
     * @param activity activity.
     * @return activity 的logo
     */
    fun getActivityLogo(activity: Activity): Drawable? {
        return getActivityLogo(activity.componentName)
    }

    /**
     * 返回 activity 的logo.
     *
     * @param clz activity class.
     * @return activity 的logo
     */
    fun getActivityLogo(clz: Class<out Activity?>): Drawable? {
        return getActivityLogo(ComponentName(DawnBridge.getApp(), clz))
    }

    /**
     * 返回 activity 的logo.
     *
     * @param activityName activity 名称.
     * @return activity 的logo
     */
    fun getActivityLogo(activityName: ComponentName): Drawable? {
        val pm = DawnBridge.getApp().packageManager
        return try {
            pm.getActivityLogo(activityName)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            null
        }
    }

    /**
     * 按上下文返回活动。
     *
     * @param context context
     * @return 此上下文的activity
     */
    @JvmStatic
    fun getActivityByContext(context: Context?): Activity? {
        if (context == null) {
            return null
        }
        val activity = getActivityByContextInner(context)
        return if (!isActivityAlive(activity)) {
            null
        } else activity
    }

    private fun getActivityByContextInner(context: Context?): Activity? {
        var context: Context? = context ?: return null
        val list: MutableList<Context> = ArrayList()
        while (context is ContextWrapper) {
            if (context is Activity) {
                return context
            }
            val activity = getActivityFromDecorContext(context)
            if (activity != null) {
                return activity
            }
            list.add(context)
            context = context.baseContext
            if (context == null) {
                return null
            }
            if (list.contains(context)) {
                // loop context
                return null
            }
        }
        return null
    }

    private fun getActivityFromDecorContext(context: Context?): Activity? {
        if (context == null) {
            return null
        }
        if ("com.android.internal.policy.DecorContext" == context.javaClass.name) {
            try {
                val mActivityContextField = context.javaClass.getDeclaredField("mActivityContext")
                mActivityContextField.isAccessible = true
                return (mActivityContextField[context] as WeakReference<Activity?>).get()
            } catch (ignore: Exception) {
            }
        }
        return null
    }

    private fun startActivity(
        context: Context, extras: Bundle?, pkg: String, cls: String, options: Bundle?
    ) {
        val intent = Intent()
        if (extras != null) {
            intent.putExtras(extras)
        }
        intent.component = ComponentName(pkg, cls)
        startActivity(intent, context, options)
    }

    private fun startActivity(
        intent: Intent, context: Context, options: Bundle?
    ): Boolean {
        if (!isIntentAvailable(intent)) {
            Log.e("ActivityUtils", "intent is unavailable")
            return false
        }
        if (context !is Activity) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        if (options != null) {
            context.startActivity(intent, options)
        } else {
            context.startActivity(intent)
        }
        return true
    }

    private fun isIntentAvailable(intent: Intent): Boolean {
        return DawnBridge.getApp().packageManager.queryIntentActivities(
            intent, PackageManager.MATCH_DEFAULT_ONLY
        ).size > 0
    }

    private fun startActivityForResult(
        activity: Activity, extras: Bundle?, pkg: String, cls: String, requestCode: Int, options: Bundle?
    ): Boolean {
        val intent = Intent()
        if (extras != null) {
            intent.putExtras(extras)
        }
        intent.component = ComponentName(pkg, cls)
        return startActivityForResult(intent, activity, requestCode, options)
    }

    private fun startActivityForResult(
        intent: Intent, activity: Activity, requestCode: Int, options: Bundle?
    ): Boolean {
        if (!isIntentAvailable(intent)) {
            Log.e("ActivityUtils", "intent is unavailable")
            return false
        }
        if (options != null) {
            activity.startActivityForResult(intent, requestCode, options)
        } else {
            activity.startActivityForResult(intent, requestCode)
        }
        return true
    }

    private fun startActivities(
        intents: Array<Intent>, context: Context, options: Bundle?
    ) {
        if (context !is Activity) {
            for (intent in intents) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        }
        if (options != null) {
            context.startActivities(intents, options)
        } else {
            context.startActivities(intents)
        }
    }

    private fun startActivityForResult(
        fragment: Fragment, extras: Bundle?, pkg: String, cls: String, requestCode: Int, options: Bundle?
    ): Boolean {
        val intent = Intent()
        if (extras != null) {
            intent.putExtras(extras)
        }
        intent.component = ComponentName(pkg, cls)
        return startActivityForResult(intent, fragment, requestCode, options)
    }

    private fun startActivityForResult(
        intent: Intent, fragment: Fragment, requestCode: Int, options: Bundle?
    ): Boolean {
        if (!isIntentAvailable(intent)) {
            Log.e("ActivityUtils", "intent is unavailable")
            return false
        }
        if (fragment.activity == null) {
            Log.e("ActivityUtils", "Fragment $fragment not attached to Activity")
            return false
        }
        if (options != null) {
            fragment.startActivityForResult(intent, requestCode, options)
        } else {
            fragment.startActivityForResult(intent, requestCode)
        }
        return true
    }

    private fun getOptionsBundle(
        fragment: Fragment, enterAnim: Int, exitAnim: Int
    ): Bundle? {
        val activity = fragment.activity ?: return null
        return ActivityOptionsCompat.makeCustomAnimation(activity, enterAnim, exitAnim).toBundle()
    }

    private fun getOptionsBundle(
        context: Context, enterAnim: Int, exitAnim: Int
    ): Bundle? {
        return ActivityOptionsCompat.makeCustomAnimation(context, enterAnim, exitAnim).toBundle()
    }

    private fun getOptionsBundle(
        fragment: Fragment, sharedElements: Array<View>
    ): Bundle? {
        val activity = fragment.activity ?: return null
        return getOptionsBundle(activity, sharedElements)
    }

    private fun getOptionsBundle(
        activity: Activity, sharedElements: Array<View>
    ): Bundle? {
        if (sharedElements == null) {
            return null
        }
        val len = sharedElements.size
        if (len <= 0) {
            return null
        }
        val pairs: Array<Pair<View, String>> = mutableListOf<Pair<View, String>>().toTypedArray()
        for (i in 0 until len) {
            pairs[i] = Pair.create(sharedElements[i], sharedElements[i].transitionName)
        }
        return ActivityOptionsCompat.makeSceneTransitionAnimation(activity, *pairs).toBundle()
    }

    private val topActivityOrApp: Context
        get() = if (DawnBridge.isAppForeground()) {
            val topActivity = topActivity
            topActivity
        } else {
            DawnBridge.getApp()
        }
}