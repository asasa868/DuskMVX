package com.lzq.dusk.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update

/**
 * @projectName com.lzq.dusk.room
 * @author Lzq
 * @date : Created by Lzq on 2024/6/25 17:36
 * @version
 * @description: room数据库Dao
 */


@Dao
interface BannerDao {

    @Insert
    suspend fun insert(vararg bean: BannerBean)

    @Insert
    @Transaction
    suspend fun insertAll(bean: List<BannerBean>)

    @Delete
    suspend fun delete(vararg bean: BannerBean)

    @Update(entity = BannerBean::class)
    suspend fun update(vararg bean: BannerBean)

    @Query("SELECT * FROM BANNERLIST")
    suspend fun getAll(): List<BannerBean>

    @Query("SELECT * FROM BANNERLIST WHERE id =:id")
    suspend fun getBannerById(id: Int): BannerBean?

    @Query("DELETE FROM BANNERLIST WHERE insertTime < :expiredTime ")
    fun deleteExpiredData(expiredTime: Long)

}



@Dao
interface HarmonyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg bean: HarmonyBean)

    @Delete
    suspend fun delete(vararg bean: HarmonyBean)

    @Update(entity = HarmonyBean::class)
    suspend fun update(vararg bean: HarmonyBean)

    @Query("SELECT * FROM HARMONY")
    suspend fun getAll(): List<HarmonyBean>

    @Query("DELETE FROM HARMONY WHERE insertTime < :expiredTime ")
    fun deleteExpiredData(expiredTime: Long)
}

@Dao
interface CategoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg bean: Category)

    @Delete
    suspend fun delete(vararg bean: Category)


    @Update(entity = Category::class)
    suspend fun update(vararg bean: Category)

    @Query("SELECT * FROM CATEGORY")
    suspend fun getAll(): List<Category>
}

@Dao
interface ArticleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg bean: Article)

    @Delete
    suspend fun delete(vararg bean: Article)


    @Update(entity = Article::class)
    suspend fun update(vararg bean: Article)

    @Query("SELECT * FROM ARTICLES")
    suspend fun getAll(): List<Article>
}

