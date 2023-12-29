package com.lzq.dawn.util.service;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;

import androidx.annotation.NonNull;

import com.lzq.dawn.DawnBridge;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @Name :ServiceUtils
 * @Time :2022/8/29 15:55
 * @Author :  Lzq
 * @Desc : Service 服务
 */
public class ServiceUtils {

    private ServiceUtils() {
    }


    /**
     * 返回所有在运行的服务。
     *
     * @return 所有在运行的服务。
     */
    public static Set<String> getAllRunningServices() {
        ActivityManager am = (ActivityManager) DawnBridge.getApp().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> info = am.getRunningServices(0x7FFFFFFF);
        Set<String> names = new HashSet<>();
        if (info == null || info.size() == 0) {
            return null;
        }
        for (ActivityManager.RunningServiceInfo aInfo : info) {
            names.add(aInfo.service.getClassName());
        }
        return names;
    }

    /**
     * 启动服务
     *
     * @param className 类的名称。
     */
    public static void startService(@NonNull final String className) {
        try {
            startService(Class.forName(className));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 启动服务
     *
     * @param cls 类
     */
    public static void startService(@NonNull final Class<?> cls) {
        startService(new Intent(DawnBridge.getApp(), cls));
    }

    /**
     * 启动服务
     *
     * @param intent intent.
     */
    public static void startService(Intent intent) {
        try {
            intent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                DawnBridge.getApp().startForegroundService(intent);
            } else {
                DawnBridge.getApp().startService(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 停止服务
     *
     * @param className 类的名称
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean stopService(@NonNull final String className) {
        try {
            return stopService(Class.forName(className));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 停止服务
     *
     * @param cls 类的名称
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean stopService(@NonNull final Class<?> cls) {
        return stopService(new Intent(DawnBridge.getApp(), cls));
    }

    /**
     * 停止服务
     *
     * @param intent intent.
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean stopService(@NonNull Intent intent) {
        try {
            return DawnBridge.getApp().stopService(intent);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 绑定服务
     *
     * @param className 类的名称。
     * @param conn      服务连接对象。
     * @param flags     绑定的操作选项。
     *                  <ul>
     *                  <li>0</li>
     *                  <li>{@link Context#BIND_AUTO_CREATE}</li>
     *                  <li>{@link Context#BIND_DEBUG_UNBIND}</li>
     *                  <li>{@link Context#BIND_NOT_FOREGROUND}</li>
     *                  <li>{@link Context#BIND_ABOVE_CLIENT}</li>
     *                  <li>{@link Context#BIND_ALLOW_OOM_MANAGEMENT}</li>
     *                  <li>{@link Context#BIND_WAIVE_PRIORITY}</li>
     *                  </ul>
     */
    public static void bindService(@NonNull final String className,
                                   @NonNull final ServiceConnection conn,
                                   final int flags) {
        try {
            bindService(Class.forName(className), conn, flags);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 绑定服务
     *
     * @param cls   服务类
     * @param conn  服务连接对象。
     * @param flags 绑定的操作选项。
     *              <ul>
     *              <li>0</li>
     *              <li>{@link Context#BIND_AUTO_CREATE}</li>
     *              <li>{@link Context#BIND_DEBUG_UNBIND}</li>
     *              <li>{@link Context#BIND_NOT_FOREGROUND}</li>
     *              <li>{@link Context#BIND_ABOVE_CLIENT}</li>
     *              <li>{@link Context#BIND_ALLOW_OOM_MANAGEMENT}</li>
     *              <li>{@link Context#BIND_WAIVE_PRIORITY}</li>
     *              </ul>
     */
    public static void bindService(@NonNull final Class<?> cls,
                                   @NonNull final ServiceConnection conn,
                                   final int flags) {
        bindService(new Intent(DawnBridge.getApp(), cls), conn, flags);
    }

    /**
     * 绑定服务
     *
     * @param intent intent.
     * @param conn   服务连接对象。
     * @param flags  绑定的操作选项。
     *               <ul>
     *               <li>0</li>
     *               <li>{@link Context#BIND_AUTO_CREATE}</li>
     *               <li>{@link Context#BIND_DEBUG_UNBIND}</li>
     *               <li>{@link Context#BIND_NOT_FOREGROUND}</li>
     *               <li>{@link Context#BIND_ABOVE_CLIENT}</li>
     *               <li>{@link Context#BIND_ALLOW_OOM_MANAGEMENT}</li>
     *               <li>{@link Context#BIND_WAIVE_PRIORITY}</li>
     *               </ul>
     */
    public static void bindService(@NonNull final Intent intent,
                                   @NonNull final ServiceConnection conn,
                                   final int flags) {
        try {
            DawnBridge.getApp().bindService(intent, conn, flags);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 解除绑定服务。
     *
     * @param conn 服务连接对象。
     */
    public static void unbindService(@NonNull final ServiceConnection conn) {
        DawnBridge.getApp().unbindService(conn);
    }

    /**
     * 返回服务是否正在运行。
     *
     * @param cls 服务类
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isServiceRunning(@NonNull final Class<?> cls) {
        return isServiceRunning(cls.getName());
    }

    /**
     * 返回服务是否正在运行。
     *
     * @param className 类的名称。
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isServiceRunning(@NonNull final String className) {
        try {
            ActivityManager am = (ActivityManager) DawnBridge.getApp().getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningServiceInfo> info = am.getRunningServices(0x7FFFFFFF);
            if (info == null || info.size() == 0) {
                return false;
            }
            for (ActivityManager.RunningServiceInfo aInfo : info) {
                if (className.equals(aInfo.service.getClassName())) {
                    return true;
                }
            }
            return false;
        } catch (Exception ignore) {
            return false;
        }
    }

}
