package com.lzq.dawn.util.activity

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.lifecycle.Lifecycle
import com.lzq.dawn.DawnBridge
import java.util.LinkedList
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

/**
 * className :ActivityLifecycleImpl
 * createTime :2022/7/8 15:53
 *
 * @Author :  Lzq
 */
class ActivityLifecycleImpl : Application.ActivityLifecycleCallbacks {
    /**
     * activity 的对象
     */
    private val mActivityList = LinkedList<Activity>()

    /**
     * 监听事件 的对象
     */
    private val mStatusListeners: MutableList<OnAppStatusChangedListener> = CopyOnWriteArrayList()

    /**
     * activity 和监听对象的map
     */
    private val mActivityLifecycleCallbacksMap: MutableMap<Activity, MutableList<ActivityLifecycleCallbacks>> =
        ConcurrentHashMap()

    /**
     * 前台计数
     */
    private var mForegroundCount = 0

    /**
     * 后台计数
     */
    private var mConfigCount = 0

    /**
     * 是否在后台
     */
    private var mIsBackground = false

    /**
     * 初始化
     *
     * @param app Application
     */
    fun init(app: Application) {
        app.registerActivityLifecycleCallbacks(this)
    }

    fun unInit(app: Application) {
        mActivityList.clear()
        app.unregisterActivityLifecycleCallbacks(this)
    }

    val topActivity: Activity?
        get() {
            val activityList = activityList
            for (activity in activityList) {
                if (!DawnBridge.isActivityAlive(activity)) {
                    continue
                }
                return activity
            }
            return null
        }
    val activityList: List<Activity>
        get() {
            if (!mActivityList.isEmpty()) {
                return LinkedList(mActivityList)
            }
            val reflectActivities = activitiesByReflect
            mActivityList.addAll(reflectActivities)
            return LinkedList(mActivityList)
        }

    fun addOnAppStatusChangedListener(listener: OnAppStatusChangedListener) {
        mStatusListeners.add(listener)
    }

    fun removeOnAppStatusChangedListener(listener: OnAppStatusChangedListener) {
        mStatusListeners.remove(listener)
    }

    fun addActivityLifecycleCallbacks(listener: ActivityLifecycleCallbacks?) {
        addActivityLifecycleCallbacks(STUB, listener)
    }

    fun addActivityLifecycleCallbacks(
        activity: Activity?, listener: ActivityLifecycleCallbacks?
    ) {
        if (activity == null || listener == null) {
            return
        }
        DawnBridge.runOnUiThread { addActivityLifecycleCallbacksInner(activity, listener) }
    }

    val isAppForeground: Boolean
        get() = !mIsBackground

    private fun addActivityLifecycleCallbacksInner(
        activity: Activity, callbacks: ActivityLifecycleCallbacks
    ) {
        var callbacksList = mActivityLifecycleCallbacksMap[activity]
        if (callbacksList == null) {
            callbacksList = CopyOnWriteArrayList()
            mActivityLifecycleCallbacksMap[activity] = callbacksList
        } else {
            if (callbacksList.contains(callbacks)) {
                return
            }
        }
        callbacksList.add(callbacks)
    }

    fun removeActivityLifecycleCallbacks(callbacks: ActivityLifecycleCallbacks?) {
        removeActivityLifecycleCallbacks(STUB, callbacks)
    }

    fun removeActivityLifecycleCallbacks(activity: Activity?) {
        if (activity == null) {
            return
        }
        DawnBridge.runOnUiThread { mActivityLifecycleCallbacksMap.remove(activity) }
    }

    fun removeActivityLifecycleCallbacks(
        activity: Activity?, callbacks: ActivityLifecycleCallbacks?
    ) {
        if (activity == null || callbacks == null) {
            return
        }
        DawnBridge.runOnUiThread { removeActivityLifecycleCallbacksInner(activity, callbacks) }
    }

    private fun removeActivityLifecycleCallbacksInner(
        activity: Activity, callbacks: ActivityLifecycleCallbacks
    ) {
        val callbacksList = mActivityLifecycleCallbacksMap[activity]
        if (callbacksList != null && !callbacksList.isEmpty()) {
            callbacksList.remove(callbacks)
        }
    }

    private fun consumeActivityLifecycleCallbacks(activity: Activity, event: Lifecycle.Event) {
        consumeLifecycle(activity, event, mActivityLifecycleCallbacksMap[activity])
        consumeLifecycle(activity, event, mActivityLifecycleCallbacksMap[STUB])
    }

    private fun consumeLifecycle(
        activity: Activity,
        event: Lifecycle.Event,
        listeners: List<ActivityLifecycleCallbacks>?
    ) {
        if (listeners == null) {
            return
        }
        for (listener in listeners) {
            listener.onLifecycleChanged(activity, event)
            if (event == Lifecycle.Event.ON_CREATE) {
                listener.onActivityCreated(activity)
            } else if (event == Lifecycle.Event.ON_START) {
                listener.onActivityStarted(activity)
            } else if (event == Lifecycle.Event.ON_RESUME) {
                listener.onActivityResumed(activity)
            } else if (event == Lifecycle.Event.ON_PAUSE) {
                listener.onActivityPaused(activity)
            } else if (event == Lifecycle.Event.ON_STOP) {
                listener.onActivityStopped(activity)
            } else if (event == Lifecycle.Event.ON_DESTROY) {
                listener.onActivityDestroyed(activity)
            }
        }
        if (event == Lifecycle.Event.ON_DESTROY) {
            mActivityLifecycleCallbacksMap.remove(activity)
        }
    }

    val applicationByReflect: Application?
        get() {
            try {
                val activityThreadClass = Class.forName("android.app.ActivityThread")
                val thread = activityThread ?: return null
                val app = activityThreadClass.getMethod("getApplication").invoke(thread) ?: return null
                return app as Application
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return null
        }

    override fun onActivityPreCreated(activity: Activity, savedInstanceState: Bundle?) {}
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        if (mActivityList.size == 0) {
            postStatus(activity, true)
        }
        setAnimatorsEnabled()
        setTopActivity(activity)
        consumeActivityLifecycleCallbacks(activity, Lifecycle.Event.ON_CREATE)
    }

    override fun onActivityPostCreated(activity: Activity, savedInstanceState: Bundle?) { /**/
    }

    override fun onActivityPreStarted(activity: Activity) { /**/
    }

    override fun onActivityStarted(activity: Activity) {
        if (!mIsBackground) {
            setTopActivity(activity)
        }
        if (mConfigCount < 0) {
            ++mConfigCount
        } else {
            ++mForegroundCount
        }
        consumeActivityLifecycleCallbacks(activity, Lifecycle.Event.ON_START)
    }

    override fun onActivityPostStarted(activity: Activity) { /**/
    }

    override fun onActivityPreResumed(activity: Activity) { /**/
    }

    override fun onActivityResumed(activity: Activity) {
        setTopActivity(activity)
        if (mIsBackground) {
            mIsBackground = false
            postStatus(activity, true)
        }
        processHideSoftInputOnActivityDestroy(activity, false)
        consumeActivityLifecycleCallbacks(activity, Lifecycle.Event.ON_RESUME)
    }

    override fun onActivityPostResumed(activity: Activity) { /**/
    }

    override fun onActivityPrePaused(activity: Activity) { /**/
    }

    override fun onActivityPaused(activity: Activity) {
        consumeActivityLifecycleCallbacks(activity, Lifecycle.Event.ON_PAUSE)
    }

    override fun onActivityPostPaused(activity: Activity) { /**/
    }

    override fun onActivityPreStopped(activity: Activity) { /**/
    }

    override fun onActivityStopped(activity: Activity) {
        if (activity.isChangingConfigurations) {
            --mConfigCount
        } else {
            --mForegroundCount
            if (mForegroundCount <= 0) {
                mIsBackground = true
                postStatus(activity, false)
            }
        }
        processHideSoftInputOnActivityDestroy(activity, true)
        consumeActivityLifecycleCallbacks(activity, Lifecycle.Event.ON_STOP)
    }

    override fun onActivityPostStopped(activity: Activity) { /**/
    }

    override fun onActivityPreSaveInstanceState(activity: Activity, outState: Bundle) { /**/
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
    override fun onActivityPostSaveInstanceState(activity: Activity, outState: Bundle) { /**/
    }

    override fun onActivityPreDestroyed(activity: Activity) { /**/
    }

    override fun onActivityDestroyed(activity: Activity) {
        mActivityList.remove(activity)
        consumeActivityLifecycleCallbacks(activity, Lifecycle.Event.ON_DESTROY)
    }

    override fun onActivityPostDestroyed(activity: Activity) { /**/
    }

    /**
     * 解决关闭键盘时的活动破坏。preActivity set windowSoftInputMode 将阻止键盘在 curActivity onDestroy 时关闭。
     */
    private fun processHideSoftInputOnActivityDestroy(activity: Activity, isSave: Boolean) {
        try {
            if (isSave) {
                val window = activity.window
                val attrs = window.attributes
                val softInputMode = attrs.softInputMode
                window.decorView.setTag(-123, softInputMode)
                window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
            } else {
                val tag = activity.window.decorView.getTag(-123) as? Int ?: return
                DawnBridge.runOnUiThreadDelayed({
                    try {
                        val window = activity.window
                        window?.setSoftInputMode(tag)
                    } catch (ignore: Exception) {
                    }
                }, 100)
            }
        } catch (ignore: Exception) {
        }
    }

    private fun postStatus(activity: Activity, isForeground: Boolean) {
        if (mStatusListeners.isEmpty()) {
            return
        }
        for (statusListener in mStatusListeners) {
            if (isForeground) {
                statusListener.onForeground(activity)
            } else {
                statusListener.onBackground(activity)
            }
        }
    }

    private fun setTopActivity(activity: Activity) {
        if (mActivityList.contains(activity)) {
            if (mActivityList.first != activity) {
                mActivityList.remove(activity)
                mActivityList.addFirst(activity)
            }
        } else {
            mActivityList.addFirst(activity)
        }
    }

    private val activitiesByReflect: List<Activity>
        /**
         * @return 排名靠前的活动
         */
        private get() {
            val list = LinkedList<Activity>()
            var topActivity: Activity? = null
            try {
                val activityThread = activityThread ?: return list
                val mActivitiesField = activityThread.javaClass.getDeclaredField("mActivities")
                mActivitiesField.isAccessible = true
                val mActivities = mActivitiesField[activityThread] as? Map<*, *> ?: return list
                val binder_activityClientRecord_map = mActivities as Map<Any, Any>
                for (activityRecord in binder_activityClientRecord_map.values) {
                    val activityClientRecordClass: Class<out Any> = activityRecord.javaClass
                    val activityField = activityClientRecordClass.getDeclaredField("activity")
                    activityField.isAccessible = true
                    val activity = activityField[activityRecord] as Activity
                    if (topActivity == null) {
                        val pausedField = activityClientRecordClass.getDeclaredField("paused")
                        pausedField.isAccessible = true
                        if (!pausedField.getBoolean(activityRecord)) {
                            topActivity = activity
                        } else {
                            list.addFirst(activity)
                        }
                    } else {
                        list.addFirst(activity)
                    }
                }
            } catch (e: Exception) {
                Log.e("UtilsActivityLifecycle", "getActivitiesByReflect: " + e.message)
            }
            if (topActivity != null) {
                list.addFirst(topActivity)
            }
            return list
        }
    private val activityThread: Any?
        private get() {
            val activityThread = activityThreadInActivityThreadStaticField
            return activityThread ?: activityThreadInActivityThreadStaticMethod
        }

    @get:SuppressLint("PrivateApi", "DiscouragedPrivateApi")
    private val activityThreadInActivityThreadStaticField: Any?
        private get() = try {
            val activityThreadClass = Class.forName("android.app.ActivityThread")
            val sCurrentActivityThreadField = activityThreadClass.getDeclaredField("sCurrentActivityThread")
            sCurrentActivityThreadField.isAccessible = true
            sCurrentActivityThreadField[null]
        } catch (e: Exception) {
            Log.e("UtilsActivityLifecycle", "getActivityThreadInActivityThreadStaticField: " + e.message)
            null
        }

    @get:SuppressLint("PrivateApi", "DiscouragedPrivateApi")
    private val activityThreadInActivityThreadStaticMethod: Any?
        private get() = try {
            val activityThreadClass = Class.forName("android.app.ActivityThread")
            activityThreadClass.getMethod("currentActivityThread").invoke(null)
        } catch (e: Exception) {
            Log.e("UtilsActivityLifecycle", "getActivityThreadInActivityThreadStaticMethod: " + e.message)
            null
        }

    companion object {
        /**
         * 单例
         */
        @JvmField
        val INSTANCE = ActivityLifecycleImpl()
        private val STUB = Activity()

        /**
         * 设置启用动画器。
         */
        @SuppressLint("SoonBlockedPrivateApi")
        private fun setAnimatorsEnabled() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && ValueAnimator.areAnimatorsEnabled()) {
                return
            }
            try {
                val sDurationScaleField = ValueAnimator::class.java.getDeclaredField("sDurationScale")
                sDurationScaleField.isAccessible = true
                val sDurationScale = sDurationScaleField[null] as Float
                if (sDurationScale == 0f) {
                    sDurationScaleField[null] = 1f
                    Log.i("UtilsActivityLifecycle", "setAnimatorsEnabled: Animators are enabled now!")
                }
            } catch (e: NoSuchFieldException) {
                e.printStackTrace()
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            }
        }
    }
}