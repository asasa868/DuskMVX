package com.lzq.dusk.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

/**
 * @projectName com.lzq.dusk.room
 * @author Lzq
 * @date : Created by Lzq on 2024/6/25 17:34
 * @version
 * @description: room数据库实体
 */


@Entity(tableName = "bannerList")
data class BannerBean(
    @SerializedName("desc") val desc: String,
    @SerializedName("id") @PrimaryKey(autoGenerate = false) val id: Int,
    @SerializedName("imagePath") val imagePath: String,
    @SerializedName("isVisible") val isVisible: Int,
    @SerializedName("order") val order: Int,
    @SerializedName("title") val title: String,
    @SerializedName("type") val type: Int,
    @SerializedName("url") val url: String,
    var insertTime: Long = System.currentTimeMillis()
)


@Entity(tableName = "harmony")
data class HarmonyBean(
    @PrimaryKey(autoGenerate = true) val mainId: Int,
    @SerializedName("links") val links: Category,
    @SerializedName("open_sources") val open_sources: Category,
    @SerializedName("tools") val tools: Category,
    var insertTime: Long = System.currentTimeMillis()
)

@Entity(tableName = "category")
data class Category(
    @PrimaryKey val id: Int,
    val articleList: List<Article>,
    val author: String,
    val children: List<String>,
    val courseId: Int,
    val cover: String,
    val desc: String,
    val lisense: String,
    val lisenseLink: String,
    val name: String,
    val order: Int,
    val parentChapterId: Int,
    val type: Int,
    val userControlSetTop: Boolean,
    val visible: Int,
    var category: String
)


@Entity(tableName = "articles")
data class Article(
    @PrimaryKey val id: Int,
    @SerializedName("adminAdd") val adminAdd: Boolean,
    val apkLink: String,
    val audit: Int,
    val author: String,
    val canEdit: Boolean,
    val chapterId: Int,
    val chapterName: String,
    val collect: Boolean,
    val courseId: Int,
    val desc: String,
    val descMd: String,
    val envelopePic: String,
    val fresh: Boolean,
    val host: String,
    @SerializedName("isAdminAdd") val adminAdds: Boolean,
    val link: String,
    val niceDate: String,
    val niceShareDate: String,
    val origin: String,
    val prefix: String,
    val projectLink: String,
    val publishTime: Long,
    val realSuperChapterId: Int,
    val selfVisible: Int,
    val shareDate: Long,
    val shareUser: String,
    val superChapterId: Int,
    val superChapterName: String,
    val tags: List<String>,
    val title: String,
    val type: Int,
    val userId: Int,
    val visible: Int,
    val zan: Int,
    var category: String
)
