package com.lzq.dawn.util.resource;

import android.graphics.drawable.Drawable;

import androidx.annotation.DrawableRes;
import androidx.annotation.RawRes;
import androidx.core.content.ContextCompat;

import com.lzq.dawn.DawnBridge;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.List;

/**
 * @Name :ResourceUtils
 * @Time :2022/8/29 14:59
 * @Author :  Lzq
 * @Desc : 资源
 */
public final class ResourceUtils {

    private static final int BUFFER_SIZE = 8192;

    private ResourceUtils() {
    }


    /**
     * 按标识符返回可绘制对象。
     *
     * @param id 标识符
     * @return drawable
     */
    public static Drawable getDrawable(@DrawableRes int id) {
        return ContextCompat.getDrawable(DawnBridge.getApp(), id);
    }

    /**
     * 按名称的 id 标识符
     *
     * @param name id.
     * @return 按名称的 id 标识符
     */
    public static int getIdByName(String name) {
        return DawnBridge.getApp().getResources().getIdentifier(name, "id", DawnBridge.getApp().getPackageName());
    }

    /**
     * 按名称返回字符串标识符。
     *
     * @param name 字符串的名称。
     * @return 按名称的字符串标识符
     */
    public static int getStringIdByName(String name) {
        return DawnBridge.getApp().getResources().getIdentifier(name, "string", DawnBridge.getApp().getPackageName());
    }

    /**
     * 按名称返回颜色标识符。
     *
     * @param name 颜色的名称。
     * @return 名称的颜色标识符
     */
    public static int getColorIdByName(String name) {
        return DawnBridge.getApp().getResources().getIdentifier(name, "color", DawnBridge.getApp().getPackageName());
    }

    /**
     * 按名称返回dimen标识符。
     *
     * @param name dimen的名字
     * @return 名称的dimen标识符
     */
    public static int getDimenIdByName(String name) {
        return DawnBridge.getApp().getResources().getIdentifier(name, "dimen", DawnBridge.getApp().getPackageName());
    }

    /**
     * 按名称返回Drawable标识符。
     *
     * @param name Drawable的名称。
     * @return 按名称绘制的Drawable标识符
     */
    public static int getDrawableIdByName(String name) {
        return DawnBridge.getApp().getResources().getIdentifier(name, "drawable", DawnBridge.getApp().getPackageName());
    }

    /**
     * 按名称返回 mipmap 标识符。
     *
     * @param name mipmap 的名称。
     * @return 名称的 mipmap 标识符
     */
    public static int getMipmapIdByName(String name) {
        return DawnBridge.getApp().getResources().getIdentifier(name, "mipmap", DawnBridge.getApp().getPackageName());
    }

    /**
     * 按名称返回布局标识符。
     *
     * @param name 布局的名称
     * @return 按名称排列的布局标识符
     */
    public static int getLayoutIdByName(String name) {
        return DawnBridge.getApp().getResources().getIdentifier(name, "layout", DawnBridge.getApp().getPackageName());
    }

    /**
     * 按名称返回样式标识符。
     *
     * @param name 样式的名称。
     * @return 按名称的样式标识符
     */
    public static int getStyleIdByName(String name) {
        return DawnBridge.getApp().getResources().getIdentifier(name, "style", DawnBridge.getApp().getPackageName());
    }

    /**
     * 按名称返回动画标识符。
     *
     * @param name 动画的名称。
     * @return 动画标识符的名称
     */
    public static int getAnimIdByName(String name) {
        return DawnBridge.getApp().getResources().getIdentifier(name, "anim", DawnBridge.getApp().getPackageName());
    }

    /**
     * 按名称返回菜单标识符。
     *
     * @param name 菜单的名称
     * @return 按名称的菜单标识符
     */
    public static int getMenuIdByName(String name) {
        return DawnBridge.getApp().getResources().getIdentifier(name, "menu", DawnBridge.getApp().getPackageName());
    }

    /**
     * 从assets复制文件.
     *
     * @param assetsFilePath assets中文件的路径。
     * @param destFilePath   目标文件的路径。
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean copyFileFromAssets(final String assetsFilePath, final String destFilePath) {
        boolean res = true;
        try {
            String[] assets = DawnBridge.getApp().getAssets().list(assetsFilePath);
            if (assets != null && assets.length > 0) {
                for (String asset : assets) {
                    res &= copyFileFromAssets(assetsFilePath + "/" + asset, destFilePath + "/" + asset);
                }
            } else {
                res = DawnBridge.writeFileFromIS(
                        destFilePath,
                        DawnBridge.getApp().getAssets().open(assetsFilePath)
                );
            }
        } catch (IOException e) {
            e.printStackTrace();
            res = false;
        }
        return res;
    }

    /**
     * 返回assets的内容。
     *
     * @param assetsFilePath assets中文件的路径。
     * @return assets的内容。
     */
    public static String readAssets2String(final String assetsFilePath) {
        return readAssets2String(assetsFilePath, null);
    }

    /**
     * 返回assets的内容。
     *
     * @param assetsFilePath assets中文件的路径
     * @param charsetName    字符集的名称。
     * @return assets的内容。
     */
    public static String readAssets2String(final String assetsFilePath, final String charsetName) {
        try {
            InputStream is = DawnBridge.getApp().getAssets().open(assetsFilePath);
            byte[] bytes = DawnBridge.inputStream2Bytes(is);
            if (bytes == null) {
                return "";
            }
            if (DawnBridge.isSpace(charsetName)) {
                return new String(bytes);
            } else {
                try {
                    return new String(bytes, charsetName);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    return "";
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 返回assets的内容
     *
     * @param assetsPath assets中文件的路径。
     * @return assets的内容
     */
    public static List<String> readAssets2List(final String assetsPath) {
        return readAssets2List(assetsPath, "");
    }

    /**
     * 返回assets的内容
     *
     * @param assetsPath  assets中文件的路径。
     * @param charsetName 字符集的名称。
     * @return assets的内容
     */
    public static List<String> readAssets2List(final String assetsPath,
                                               final String charsetName) {
        try {
            return DawnBridge.inputStream2Lines(DawnBridge.getApp().getResources().getAssets().open(assetsPath), charsetName);
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }


    /**
     * 从raw复制文件。
     *
     * @param resId        资源id。
     * @param destFilePath 目标文件的路径。
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean copyFileFromRaw(@RawRes final int resId, final String destFilePath) {
        return DawnBridge.writeFileFromIS(
                destFilePath,
                DawnBridge.getApp().getResources().openRawResource(resId)
        );
    }

    /**
     * 返回原始资源的内容。
     *
     * @param resId 资源id。
     * @return 原始资源的内容。
     */
    public static String readRaw2String(@RawRes final int resId) {
        return readRaw2String(resId, null);
    }

    /**
     * 返回原始资源的内容
     *
     * @param resId       资源id。
     * @param charsetName 字符集的名称。
     * @return 原始资源的内容
     */
    public static String readRaw2String(@RawRes final int resId, final String charsetName) {
        InputStream is = DawnBridge.getApp().getResources().openRawResource(resId);
        byte[] bytes = DawnBridge.inputStream2Bytes(is);
        if (bytes == null) {
            return null;
        }
        if (DawnBridge.isSpace(charsetName)) {
            return new String(bytes);
        } else {
            try {
                return new String(bytes, charsetName);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                return "";
            }
        }
    }

    /**
     * 返回原始资源的内容
     *
     * @param resId 资源id
     * @return 原始资源的内容
     */
    public static List<String> readRaw2List(@RawRes final int resId) {
        return readRaw2List(resId, "");
    }

    /**
     * 返回原始资源的内容。
     *
     * @param resId       资源id
     * @param charsetName 字符集的名称。
     * @return 中文件的内容
     */
    public static List<String> readRaw2List(@RawRes final int resId,
                                            final String charsetName) {
        return DawnBridge.inputStream2Lines(DawnBridge.getApp().getResources().openRawResource(resId), charsetName);
    }
}
