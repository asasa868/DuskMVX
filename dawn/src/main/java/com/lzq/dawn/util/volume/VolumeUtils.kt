package com.lzq.dawn.util.volume

import android.content.Context
import android.media.AudioManager
import android.os.Build
import com.lzq.dawn.DawnBridge

/**
 * @Name : VolumeUtils
 * @Time : 2022/12/21  16:28
 * @Author :  Lzq
 * @Desc : 关于音量
 */
object VolumeUtils {
    /**
     * 返回音量
     *
     * @param streamType 音量类型
     *
     *  * [AudioManager.STREAM_VOICE_CALL]
     *  * [AudioManager.STREAM_SYSTEM]
     *  * [AudioManager.STREAM_RING]
     *  * [AudioManager.STREAM_MUSIC]
     *  * [AudioManager.STREAM_ALARM]
     *  * [AudioManager.STREAM_NOTIFICATION]
     *  * [AudioManager.STREAM_DTMF]
     *  * [AudioManager.STREAM_ACCESSIBILITY]
     *
     * @return 音量
     */
    fun getVolume(streamType: Int): Int {
        val am = DawnBridge.app.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        return am.getStreamVolume(streamType)
    }

    /**
     * 设置媒体音量
     * 当将参数“volume”的值设置为大于媒体音量的最大值时，不会导致错误或抛出异常，而是最大化媒体音量。<br></br> 将音量值设置为小于 0 将最小化媒体音量。
     *
     * @param streamType 音量类型
     *
     *  * [AudioManager.STREAM_VOICE_CALL]
     *  * [AudioManager.STREAM_SYSTEM]
     *  * [AudioManager.STREAM_RING]
     *  * [AudioManager.STREAM_MUSIC]
     *  * [AudioManager.STREAM_ALARM]
     *  * [AudioManager.STREAM_NOTIFICATION]
     *  * [AudioManager.STREAM_DTMF]
     *  * [AudioManager.STREAM_ACCESSIBILITY]
     *
     * @param volume     音量
     * @param flags       flags.
     *
     *  * [AudioManager.FLAG_SHOW_UI]
     *  * [AudioManager.FLAG_ALLOW_RINGER_MODES]
     *  * [AudioManager.FLAG_PLAY_SOUND]
     *  * [AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE]
     *  * [AudioManager.FLAG_VIBRATE]
     *
     */
    fun setVolume(
        streamType: Int,
        volume: Int,
        flags: Int,
    ) {
        val am = DawnBridge.app.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        try {
            am.setStreamVolume(streamType, volume, flags)
        } catch (ignore: SecurityException) {
        }
    }

    /**
     * 返回最大音量。
     *
     * @param streamType 音量类型
     *
     *  * [AudioManager.STREAM_VOICE_CALL]
     *  * [AudioManager.STREAM_SYSTEM]
     *  * [AudioManager.STREAM_RING]
     *  * [AudioManager.STREAM_MUSIC]
     *  * [AudioManager.STREAM_ALARM]
     *  * [AudioManager.STREAM_NOTIFICATION]
     *  * [AudioManager.STREAM_DTMF]
     *  * [AudioManager.STREAM_ACCESSIBILITY]
     *
     * @return 最大音量
     */
    fun getMaxVolume(streamType: Int): Int {
        val am = DawnBridge.app.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        return am.getStreamMaxVolume(streamType)
    }

    /**
     * 返回最小音量。
     *
     * @param streamType 音量类型
     *
     *  * [AudioManager.STREAM_VOICE_CALL]
     *  * [AudioManager.STREAM_SYSTEM]
     *  * [AudioManager.STREAM_RING]
     *  * [AudioManager.STREAM_MUSIC]
     *  * [AudioManager.STREAM_ALARM]
     *  * [AudioManager.STREAM_NOTIFICATION]
     *  * [AudioManager.STREAM_DTMF]
     *  * [AudioManager.STREAM_ACCESSIBILITY]
     *
     * @return 最小音量。
     */
    fun getMinVolume(streamType: Int): Int {
        val am = DawnBridge.app.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            am.getStreamMinVolume(streamType)
        } else {
            0
        }
    }
}
