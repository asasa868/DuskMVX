package com.lzq.dawn.util.init;

import android.app.Application;

import androidx.core.content.FileProvider;

import com.lzq.dawn.DawnBridge;

/**
 * className :DawnFileProvider
 * createTime :2022/7/8 15:17
 *
 * @Author :  Lzq
 */
public class DawnFileProvider extends FileProvider {


    @Override
    public boolean onCreate() {
        DawnBridge.init((Application) getContext().getApplicationContext());
        return true;
    }
}
