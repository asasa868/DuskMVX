package com.lzq.dawn.util.sdcard;

import android.content.Context;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;

import com.lzq.dawn.DawnBridge;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @Name :SDCardUtils
 * @Time :2022/8/29 15:49
 * @Author :  Lzq
 * @Desc : SD卡
 */
public final class SDCardUtils {
    private SDCardUtils() {
    }

    /**
     * 返回SD卡是否启用。
     *
     * @return {@code true}: enabled<br>{@code false}: disabled
     */
    public static boolean isSDCardEnableByEnvironment() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    /**
     * 返回SD卡的路径。
     *
     * @return SD卡的路径。
     */
    public static String getSDCardPathByEnvironment() {
        if (isSDCardEnableByEnvironment()) {
            return Environment.getExternalStorageDirectory().getAbsolutePath();
        }
        return "";
    }

    /**
     * 返回SD卡的信息。
     *
     * @return SD卡的信息。
     */
    public static List<SDCardInfo> getSDCardInfo() {
        List<SDCardInfo> paths = new ArrayList<>();
        StorageManager sm = (StorageManager) DawnBridge.getApp().getSystemService(Context.STORAGE_SERVICE);
        if (sm == null) {
            return paths;
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            List<StorageVolume> storageVolumes = sm.getStorageVolumes();
            try {
                //noinspection JavaReflectionMemberAccess
                Method getPathMethod = StorageVolume.class.getMethod("getPath");
                for (StorageVolume storageVolume : storageVolumes) {
                    boolean isRemovable = storageVolume.isRemovable();
                    String state = storageVolume.getState();
                    String path = (String) getPathMethod.invoke(storageVolume);
                    paths.add(new SDCardInfo(path, state, isRemovable));
                }
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        } else {
            try {
                Class<?> storageVolumeClazz = Class.forName("android.os.storage.StorageVolume");
                //noinspection JavaReflectionMemberAccess
                Method getPathMethod = storageVolumeClazz.getMethod("getPath");
                Method isRemovableMethod = storageVolumeClazz.getMethod("isRemovable");
                //noinspection JavaReflectionMemberAccess
                Method getVolumeStateMethod = StorageManager.class.getMethod("getVolumeState", String.class);
                //noinspection JavaReflectionMemberAccess
                Method getVolumeListMethod = StorageManager.class.getMethod("getVolumeList");
                Object result = getVolumeListMethod.invoke(sm);
                final int length = Array.getLength(result);
                for (int i = 0; i < length; i++) {
                    Object storageVolumeElement = Array.get(result, i);
                    String path = (String) getPathMethod.invoke(storageVolumeElement);
                    boolean isRemovable = (Boolean) isRemovableMethod.invoke(storageVolumeElement);
                    String state = (String) getVolumeStateMethod.invoke(sm, path);
                    paths.add(new SDCardInfo(path, state, isRemovable));
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return paths;
    }

    /**
     * 返回已安装SD卡的path。
     *
     * @return 已安装SD卡的path。
     */
    public static List<String> getMountedSDCardPath() {
        List<String> path = new ArrayList<>();
        List<SDCardInfo> sdCardInfo = getSDCardInfo();
        if (sdCardInfo == null || sdCardInfo.isEmpty()) {
            return path;
        }
        for (SDCardInfo cardInfo : sdCardInfo) {
            String state = cardInfo.getState();
            if (state == null) {
                continue;
            }
            if ("mounted".equals(state.toLowerCase())) {
                path.add(cardInfo.getPath());
            }
        }
        return path;
    }


    /**
     * 返回外部存储的总大小
     *
     * @return 外部存储的总大小
     */
    public static long getExternalTotalSize() {
        return DawnBridge.getFsTotalSize(getSDCardPathByEnvironment());
    }

    /**
     * Return the available size of external storage.
     *
     * @return the available size of external storage
     */
    public static long getExternalAvailableSize() {
        return DawnBridge.getFsAvailableSize(getSDCardPathByEnvironment());
    }

    /**
     * Return the total size of internal storage
     *
     * @return the total size of internal storage
     */
    public static long getInternalTotalSize() {
        return DawnBridge.getFsTotalSize(Environment.getDataDirectory().getAbsolutePath());
    }

    /**
     * Return the available size of internal storage.
     *
     * @return the available size of internal storage
     */
    public static long getInternalAvailableSize() {
        return DawnBridge.getFsAvailableSize(Environment.getDataDirectory().getAbsolutePath());
    }


}
