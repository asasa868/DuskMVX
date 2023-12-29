package com.lzq.dawn.util.clipboard;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;

import com.lzq.dawn.DawnBridge;

/**
 * @Name :ClipboardUtils
 * @Time :2022/7/19 16:20
 * @Author :  Lzq
 * @Desc : 粘贴板
 */
public final  class ClipboardUtils {
    private ClipboardUtils() {
    }


    /**
     * 将文本复制到剪贴板
     * <p>label 等于 包名.</p>
     *
     * @param text text.
     */
    public static void copyText(final CharSequence text) {
        copyText(DawnBridge.getApp().getPackageName(), text);
    }

    /**
     * 将文本复制到剪贴板
     *
     * @param label label.
     * @param text  text.
     */
    public static void copyText(final CharSequence label, final CharSequence text) {
        ClipboardManager cm = (ClipboardManager) DawnBridge.getApp().getSystemService(Context.CLIPBOARD_SERVICE);
        //noinspection ConstantConditions
        cm.setPrimaryClip(ClipData.newPlainText(label, text));
    }

    /**
     * 清除剪贴板
     */
    public static void clear() {
        copyText(null, "");
    }

    /**
     * 返回剪贴板的标签。
     *
     * @return 返回剪贴板的标签。
     */
    public static CharSequence getLabel() {
        ClipboardManager cm = (ClipboardManager) DawnBridge.getApp().getSystemService(Context.CLIPBOARD_SERVICE);
        //noinspection ConstantConditions
        ClipDescription des = cm.getPrimaryClipDescription();
        if (des == null) {
            return "";
        }
        CharSequence label = des.getLabel();
        if (label == null) {
            return "";
        }
        return label;
    }

    /**
     * 返回剪贴板的文本。
     *
     * @return 返回剪贴板的文本。
     */
    public static CharSequence getText() {
        ClipboardManager cm = (ClipboardManager) DawnBridge.getApp().getSystemService(Context.CLIPBOARD_SERVICE);
        //noinspection ConstantConditions
        ClipData clip = cm.getPrimaryClip();
        if (clip != null && clip.getItemCount() > 0) {
            CharSequence text = clip.getItemAt(0).coerceToText(DawnBridge.getApp());
            if (text != null) {
                return text;
            }
        }
        return "";
    }

    /**
     * 添加剪贴板更改的listener.
     */
    public static void addChangedListener(final ClipboardManager.OnPrimaryClipChangedListener listener) {
        ClipboardManager cm = (ClipboardManager) DawnBridge.getApp().getSystemService(Context.CLIPBOARD_SERVICE);
        //noinspection ConstantConditions
        cm.addPrimaryClipChangedListener(listener);
    }

    /**
     * 删除剪贴板更改的listener.
     */
    public static void removeChangedListener(final ClipboardManager.OnPrimaryClipChangedListener listener) {
        ClipboardManager cm = (ClipboardManager) DawnBridge.getApp().getSystemService(Context.CLIPBOARD_SERVICE);
        //noinspection ConstantConditions
        cm.removePrimaryClipChangedListener(listener);
    }
}
