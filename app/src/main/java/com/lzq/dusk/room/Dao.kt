package com.lzq.dusk.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.lzq.dawn.room.BaseDao

/**
 * @projectName com.lzq.dusk.room
 * @author Lzq
 * @date : Created by Lzq on 2024/6/25 17:36
 * @version
 * @description: room数据库Dao
 */


@Dao
interface BannerDao : BaseDao<BannerBean> {

    @Query("SELECT * FROM BANNERLIST")
    suspend fun getAll(): List<BannerBean>

    @Query("SELECT * FROM BANNERLIST WHERE id =:id")
    suspend fun getBannerById(id: Int): BannerBean?

    @Query("DELETE FROM BANNERLIST WHERE insertTime < :expiredTime ")
    fun deleteExpiredData(expiredTime: Long)

}



@Dao
interface HarmonyDao : BaseDao<HarmonyBean> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    override suspend fun insert(vararg entity: HarmonyBean)

    @Delete
    override suspend fun delete(vararg entity: HarmonyBean)

    @Update(entity = HarmonyBean::class)
    override suspend fun update(vararg entity: HarmonyBean)

    @Query("SELECT * FROM HARMONY")
    suspend fun getAll(): List<HarmonyBean>

    @Query("DELETE FROM HARMONY WHERE insertTime < :expiredTime ")
    fun deleteExpiredData(expiredTime: Long)
}

@Dao
interface CategoryDao : BaseDao<Category> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    override suspend fun insert(vararg entity: Category)

    @Delete
    override suspend fun delete(vararg entity: Category)


    @Update(entity = Category::class)
    override suspend fun update(vararg entity: Category)

    @Query("SELECT * FROM CATEGORY")
    suspend fun getAll(): List<Category>
}

@Dao
interface ArticleDao : BaseDao<Article> {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    override suspend fun insert(vararg entity: Article)

    @Delete
    override suspend fun delete(vararg entity: Article)


    @Update(entity = Article::class)
    override suspend fun update(vararg entity: Article)

    @Query("SELECT * FROM ARTICLES")
    suspend fun getAll(): List<Article>
}

