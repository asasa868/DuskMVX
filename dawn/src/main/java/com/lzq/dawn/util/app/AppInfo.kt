package com.lzq.dawn.util.app;

import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;

/**
 * @Name :AppInfo
 * @Time :2022/7/12 14:09
 * @Author :  Lzq
 * @Desc : app 信息
 */
public class AppInfo {

    private String packageName;
    private String name;
    private Drawable icon;
    private String packagePath;
    private String versionName;
    private int versionCode;
    private boolean isSystem;

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(final Drawable icon) {
        this.icon = icon;
    }

    public boolean isSystem() {
        return isSystem;
    }

    public void setSystem(final boolean isSystem) {
        this.isSystem = isSystem;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(final String packageName) {
        this.packageName = packageName;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getPackagePath() {
        return packagePath;
    }

    public void setPackagePath(final String packagePath) {
        this.packagePath = packagePath;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(final int versionCode) {
        this.versionCode = versionCode;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(final String versionName) {
        this.versionName = versionName;
    }

    public AppInfo(String packageName, String name, Drawable icon, String packagePath,
                   String versionName, int versionCode, boolean isSystem) {
        this.setName(name);
        this.setIcon(icon);
        this.setPackageName(packageName);
        this.setPackagePath(packagePath);
        this.setVersionName(versionName);
        this.setVersionCode(versionCode);
        this.setSystem(isSystem);
    }

    @Override
    @NonNull
    public String toString() {
        return "{" +
                "\n    pkg name: " + getPackageName() +
                "\n    app icon: " + getIcon() +
                "\n    app name: " + getName() +
                "\n    app path: " + getPackagePath() +
                "\n    app version name: " + getVersionName() +
                "\n    app version code: " + getVersionCode() +
                "\n    is system: " + isSystem() +
                "\n}";
    }
}