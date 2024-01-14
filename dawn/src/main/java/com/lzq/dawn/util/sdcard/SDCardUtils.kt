package com.lzq.dawn.util.sdcard

import android.content.Context
import android.os.Build
import android.os.Environment
import android.os.storage.StorageManager
import android.os.storage.StorageVolume
import com.lzq.dawn.DawnBridge
import java.lang.reflect.Array
import java.lang.reflect.InvocationTargetException
import java.util.Locale

/**
 * @Name :SDCardUtils
 * @Time :2022/8/29 15:49
 * @Author :  Lzq
 * @Desc : SD卡
 */
object SDCardUtils {
    @JvmStatic
    val isSDCardEnableByEnvironment: Boolean
        /**
         * 返回SD卡是否启用。
         *
         * @return `true`: enabled<br></br>`false`: disabled
         */
        get() = Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()
    @JvmStatic
    val sDCardPathByEnvironment: String
        /**
         * 返回SD卡的路径。
         *
         * @return SD卡的路径。
         */
        get() = if (isSDCardEnableByEnvironment) {
            Environment.getExternalStorageDirectory().absolutePath
        } else ""

    @JvmStatic
    val sDCardInfo: List<SDCardInfo>
        /**
         * 返回SD卡的信息。
         *
         * @return SD卡的信息。
         */
        get() {
            val paths: MutableList<SDCardInfo> = ArrayList()
            val sm = DawnBridge.getApp().getSystemService(Context.STORAGE_SERVICE) as StorageManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                val storageVolumes = sm.storageVolumes
                try {
                    val getPathMethod = StorageVolume::class.java.getMethod("getPath")
                    for (storageVolume in storageVolumes) {
                        val isRemovable = storageVolume.isRemovable
                        val state = storageVolume.state
                        val path = getPathMethod.invoke(storageVolume) as String
                        paths.add(SDCardInfo(path, state, isRemovable))
                    }
                } catch (e: NoSuchMethodException) {
                    e.printStackTrace()
                } catch (e: IllegalAccessException) {
                    e.printStackTrace()
                } catch (e: InvocationTargetException) {
                    e.printStackTrace()
                }
            } else {
                try {
                    val storageVolumeClazz = Class.forName("android.os.storage.StorageVolume")
                    val getPathMethod = storageVolumeClazz.getMethod("getPath")
                    val isRemovableMethod = storageVolumeClazz.getMethod("isRemovable")
                    val getVolumeStateMethod = StorageManager::class.java.getMethod("getVolumeState", String::class.java)
                    val getVolumeListMethod = StorageManager::class.java.getMethod("getVolumeList")
                    val result = getVolumeListMethod.invoke(sm)
                    val length = Array.getLength(result)
                    for (i in 0 until length) {
                        val storageVolumeElement = Array.get(result, i)
                        val path = getPathMethod.invoke(storageVolumeElement) as String
                        val isRemovable = isRemovableMethod.invoke(storageVolumeElement) as Boolean
                        val state = getVolumeStateMethod.invoke(sm, path) as String
                        paths.add(SDCardInfo(path, state, isRemovable))
                    }
                } catch (e: ClassNotFoundException) {
                    e.printStackTrace()
                } catch (e: InvocationTargetException) {
                    e.printStackTrace()
                } catch (e: NoSuchMethodException) {
                    e.printStackTrace()
                } catch (e: IllegalAccessException) {
                    e.printStackTrace()
                }
            }
            return paths
        }
    @JvmStatic
    val mountedSDCardPath: List<String>
        /**
         * 返回已安装SD卡的path。
         *
         * @return 已安装SD卡的path。
         */
        get() {
            val path: MutableList<String> = ArrayList()
            val sdCardInfo = sDCardInfo
            if (sdCardInfo.isEmpty()) {
                return path
            }
            for (cardInfo in sdCardInfo) {
                val state = cardInfo.state ?: continue
                if ("mounted" == state.lowercase(Locale.getDefault())) {
                    path.add(cardInfo.path)
                }
            }
            return path
        }

    @JvmStatic
    val externalTotalSize: Long
        /**
         * 返回外部存储的总大小
         *
         * @return 外部存储的总大小
         */
        get() = DawnBridge.getFsTotalSize(sDCardPathByEnvironment)

    @JvmStatic
    val externalAvailableSize: Long
        /**
         * Return the available size of external storage.
         *
         * @return the available size of external storage
         */
        get() = DawnBridge.getFsAvailableSize(sDCardPathByEnvironment)

    @JvmStatic
    val internalTotalSize: Long
        /**
         * Return the total size of internal storage
         *
         * @return the total size of internal storage
         */
        get() = DawnBridge.getFsTotalSize(Environment.getDataDirectory().absolutePath)

    @JvmStatic
    val internalAvailableSize: Long
        /**
         * Return the available size of internal storage.
         *
         * @return the available size of internal storage
         */
        get() = DawnBridge.getFsAvailableSize(Environment.getDataDirectory().absolutePath)
}