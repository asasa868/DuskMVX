package com.lzq.dawn.util.app

import android.graphics.drawable.Drawable

/**
 * @Name :AppInfo
 * @Time :2022/7/12 14:09
 * @Author :  Lzq
 * @Desc : app 信息
 */
class AppInfo(
    packageName: String?,
    name: String?,
    icon: Drawable?,
    packagePath: String?,
    versionName: String?,
    versionCode: Int,
    isSystem: Boolean
) {
    var packageName: String? = null
    var name: String? = null
    var icon: Drawable? = null
    var packagePath: String? = null
    var versionName: String? = null
    var versionCode = 0
    var isSystem = false

    init {
        this.name = name
        this.icon = icon
        this.packageName = packageName
        this.packagePath = packagePath
        this.versionName = versionName
        this.versionCode = versionCode
        this.isSystem = isSystem
    }

    override fun toString(): String {
        return """{
    pkg name: ${packageName}
    app icon: ${icon}
    app name: ${name}
    app path: ${packagePath}
    app version name: ${versionName}
    app version code: ${versionCode}
    is system: ${isSystem}
}"""
    }
}