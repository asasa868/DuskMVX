package com.lzq.dawn.util.sdcard;

import android.text.format.Formatter;

import androidx.annotation.NonNull;

import com.lzq.dawn.DawnBridge;

/**
 * @Name :SDCardInfo
 * @Time :2022/8/29 15:51
 * @Author :  Lzq
 * @Desc :
 */
public final class SDCardInfo {

    private String path;
    private String state;
    private boolean isRemovable;
    private long totalSize;
    private long availableSize;

    SDCardInfo(String path, String state, boolean isRemovable) {
        this.path = path;
        this.state = state;
        this.isRemovable = isRemovable;
        this.totalSize = DawnBridge.getFsTotalSize(path);
        this.availableSize = DawnBridge.getFsAvailableSize(path);
    }

    public String getPath() {
        return path;
    }

    public String getState() {
        return state;
    }

    public boolean isRemovable() {
        return isRemovable;
    }

    public long getTotalSize() {
        return totalSize;
    }

    public long getAvailableSize() {
        return availableSize;
    }


    public void setPath(String path) {
        this.path = path;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setRemovable(boolean removable) {
        isRemovable = removable;
    }

    public void setTotalSize(long totalSize) {
        this.totalSize = totalSize;
    }

    public void setAvailableSize(long availableSize) {
        this.availableSize = availableSize;
    }

    @NonNull
    @Override
    public String toString() {
        return "SDCardInfo {" +
                "path = " + path +
                ", state = " + state +
                ", isRemovable = " + isRemovable +
                ", totalSize = " + Formatter.formatFileSize(DawnBridge.getApp(), totalSize) +
                ", availableSize = " + Formatter.formatFileSize(DawnBridge.getApp(), availableSize) +
                '}';
    }
}

