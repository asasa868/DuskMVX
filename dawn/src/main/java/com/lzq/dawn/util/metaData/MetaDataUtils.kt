package com.lzq.dawn.util.metaData;

import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;

import androidx.annotation.NonNull;

import com.lzq.dawn.DawnBridge;

/**
 * @Name :MetaDataUtils
 * @Time :2022/8/15 16:34
 * @Author :  Lzq
 * @Desc : 关于清单文件中 MetaData
 */
public final class MetaDataUtils {
    private MetaDataUtils() {
    }

    /**
     * 返回应用程序中 meta-data 的值。
     *
     * @param key meta-data 的 key（name）
     * @return 清单文件中 meta-data 的value
     */
    public static String getMetaDataInApp(@NonNull final String key) {
        String value = "";
        PackageManager pm = DawnBridge.getApp().getPackageManager();
        String packageName = DawnBridge.getApp().getPackageName();
        try {
            ApplicationInfo ai = pm.getApplicationInfo(packageName, PackageManager.GET_META_DATA);
            value = String.valueOf(ai.metaData.get(key));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return value;
    }


    /**
     *  返回activity中 meta-data 的值。
     *
     * @param activity  activity.
     * @param key      meta-data 的 key（name）
     * @return  清单文件中activity的 meta-data 的value
     */
    public static String getMetaDataInActivity(@NonNull final Activity activity,
                                               @NonNull final String key) {
        return getMetaDataInActivity(activity.getClass(), key);
    }

    /**
     * 返回activity中 meta-data 的值。
     *
     * @param clz  activity class.
     * @param key  meta-data 的 key（name）
     * @return 清单文件中activity的 meta-data 的value
     */
    public static String getMetaDataInActivity(@NonNull final Class<? extends Activity> clz,
                                               @NonNull final String key) {
        String value = "";
        PackageManager pm = DawnBridge.getApp().getPackageManager();
        ComponentName componentName = new ComponentName(DawnBridge.getApp(), clz);
        try {
            ActivityInfo ai = pm.getActivityInfo(componentName, PackageManager.GET_META_DATA);
            value = String.valueOf(ai.metaData.get(key));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return value;
    }

    /**
     *  返回service中 meta-data 的值。
     *
     * @param service  service.
     * @param key      meta-data 的 key（name）
     * @return  清单文件中service的 meta-data 的value
     */
    public static String getMetaDataInService(@NonNull final Service service,
                                              @NonNull final String key) {
        return getMetaDataInService(service.getClass(), key);
    }

    /**
     *  返回service中 meta-data 的值。
     *
     * @param clz  service class.
     * @param key      meta-data 的 key（name）
     * @return  清单文件中service的 meta-data 的value
     */
    public static String getMetaDataInService(@NonNull final Class<? extends Service> clz,
                                              @NonNull final String key) {
        String value = "";
        PackageManager pm = DawnBridge.getApp().getPackageManager();
        ComponentName componentName = new ComponentName(DawnBridge.getApp(), clz);
        try {
            ServiceInfo info = pm.getServiceInfo(componentName, PackageManager.GET_META_DATA);
            value = String.valueOf(info.metaData.get(key));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return value;
    }

    /**
     *  返回receiver中 meta-data 的值。
     *
     * @param receiver  receiver.
     * @param key      meta-data 的 key（name）
     * @return  清单文件中receiver的 meta-data 的value
     */
    public static String getMetaDataInReceiver(@NonNull final BroadcastReceiver receiver,
                                               @NonNull final String key) {
        return getMetaDataInReceiver(receiver.getClass(), key);
    }

    /**
     *  返回receiver中 meta-data 的值。
     *
     * @param clz  receiver class.
     * @param key      meta-data 的 key（name）
     * @return  清单文件中receiver的 meta-data 的value
     */
    public static String getMetaDataInReceiver(@NonNull final Class<? extends BroadcastReceiver> clz,
                                               @NonNull final String key) {
        String value = "";
        PackageManager pm = DawnBridge.getApp().getPackageManager();
        ComponentName componentName = new ComponentName(DawnBridge.getApp(), clz);
        try {
            ActivityInfo info = pm.getReceiverInfo(componentName, PackageManager.GET_META_DATA);
            value = String.valueOf(info.metaData.get(key));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return value;
    }
}
