package com.lzq.dusk.network.bean

import com.google.gson.annotations.SerializedName

/**
 * @projectName com.lzq.dusk.network.bean
 * @author Lzq
 * @date : Created by Lzq on 2024/1/29 11:12
 * @version
 * @description:
 */
data class BannerBean(
    @SerializedName("desc") val desc: String,
    @SerializedName("id") val id: Int,
    @SerializedName("imagePath") val imagePath: String,
    @SerializedName("isVisible") val isVisible: Int,
    @SerializedName("order") val order: Int,
    @SerializedName("title") val title: String,
    @SerializedName("type") val type: Int,
    @SerializedName("url") val url: String,
)
