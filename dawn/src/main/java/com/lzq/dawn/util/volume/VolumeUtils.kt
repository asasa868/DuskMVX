package com.lzq.dawn.util.volume;

import android.content.Context;
import android.media.AudioManager;
import android.os.Build;

import com.lzq.dawn.DawnBridge;

/**
 * @Name : VolumeUtils
 * @Time : 2022/12/21  16:28
 * @Author :  Lzq
 * @Desc : 关于音量
 */
public final class VolumeUtils {

    /**
     * 返回音量
     *
     * @param streamType 音量类型
     *                   <ul>
     *                   <li>{@link AudioManager#STREAM_VOICE_CALL}</li>
     *                   <li>{@link AudioManager#STREAM_SYSTEM}</li>
     *                   <li>{@link AudioManager#STREAM_RING}</li>
     *                   <li>{@link AudioManager#STREAM_MUSIC}</li>
     *                   <li>{@link AudioManager#STREAM_ALARM}</li>
     *                   <li>{@link AudioManager#STREAM_NOTIFICATION}</li>
     *                   <li>{@link AudioManager#STREAM_DTMF}</li>
     *                   <li>{@link AudioManager#STREAM_ACCESSIBILITY}</li>
     *                   </ul>
     * @return 音量
     */
    public static int getVolume(int streamType) {
        AudioManager am = (AudioManager) DawnBridge.getApp().getSystemService(Context.AUDIO_SERVICE);
        //noinspection ConstantConditions
        return am.getStreamVolume(streamType);
    }

    /**
     * 设置媒体音量
     * 当将参数“volume”的值设置为大于媒体音量的最大值时，不会导致错误或抛出异常，而是最大化媒体音量。<br> 将音量值设置为小于 0 将最小化媒体音量。
     *
     * @param streamType 音量类型
     *                   <ul>
     *                   <li>{@link AudioManager#STREAM_VOICE_CALL}</li>
     *                   <li>{@link AudioManager#STREAM_SYSTEM}</li>
     *                   <li>{@link AudioManager#STREAM_RING}</li>
     *                   <li>{@link AudioManager#STREAM_MUSIC}</li>
     *                   <li>{@link AudioManager#STREAM_ALARM}</li>
     *                   <li>{@link AudioManager#STREAM_NOTIFICATION}</li>
     *                   <li>{@link AudioManager#STREAM_DTMF}</li>
     *                   <li>{@link AudioManager#STREAM_ACCESSIBILITY}</li>
     *                   </ul>
     * @param volume     音量
     * @param flags       flags.
     *                   <ul>
     *                   <li>{@link AudioManager#FLAG_SHOW_UI}</li>
     *                   <li>{@link AudioManager#FLAG_ALLOW_RINGER_MODES}</li>
     *                   <li>{@link AudioManager#FLAG_PLAY_SOUND}</li>
     *                   <li>{@link AudioManager#FLAG_REMOVE_SOUND_AND_VIBRATE}</li>
     *                   <li>{@link AudioManager#FLAG_VIBRATE}</li>
     *                   </ul>
     */
    public static void setVolume(int streamType, int volume, int flags) {
        AudioManager am = (AudioManager) DawnBridge.getApp().getSystemService(Context.AUDIO_SERVICE);
        try {
            //noinspection ConstantConditions
            am.setStreamVolume(streamType, volume, flags);
        } catch (SecurityException ignore) {
        }
    }

    /**
     * 返回最大音量。
     *
     * @param streamType 音量类型
     *                   <ul>
     *                   <li>{@link AudioManager#STREAM_VOICE_CALL}</li>
     *                   <li>{@link AudioManager#STREAM_SYSTEM}</li>
     *                   <li>{@link AudioManager#STREAM_RING}</li>
     *                   <li>{@link AudioManager#STREAM_MUSIC}</li>
     *                   <li>{@link AudioManager#STREAM_ALARM}</li>
     *                   <li>{@link AudioManager#STREAM_NOTIFICATION}</li>
     *                   <li>{@link AudioManager#STREAM_DTMF}</li>
     *                   <li>{@link AudioManager#STREAM_ACCESSIBILITY}</li>
     *                   </ul>
     * @return 最大音量
     */
    public static int getMaxVolume(int streamType) {
        AudioManager am = (AudioManager) DawnBridge.getApp().getSystemService(Context.AUDIO_SERVICE);
        //noinspection ConstantConditions
        return am.getStreamMaxVolume(streamType);
    }

    /**
     * 返回最小音量。
     *
     * @param streamType 音量类型
     *                   <ul>
     *                   <li>{@link AudioManager#STREAM_VOICE_CALL}</li>
     *                   <li>{@link AudioManager#STREAM_SYSTEM}</li>
     *                   <li>{@link AudioManager#STREAM_RING}</li>
     *                   <li>{@link AudioManager#STREAM_MUSIC}</li>
     *                   <li>{@link AudioManager#STREAM_ALARM}</li>
     *                   <li>{@link AudioManager#STREAM_NOTIFICATION}</li>
     *                   <li>{@link AudioManager#STREAM_DTMF}</li>
     *                   <li>{@link AudioManager#STREAM_ACCESSIBILITY}</li>
     *                   </ul>
     * @return 最小音量。
     */
    public static int getMinVolume(int streamType) {
        AudioManager am = (AudioManager) DawnBridge.getApp().getSystemService(Context.AUDIO_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            //noinspection ConstantConditions
            return am.getStreamMinVolume(streamType);
        }
        return 0;
    }
}
