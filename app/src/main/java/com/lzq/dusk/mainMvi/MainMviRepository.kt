package com.lzq.dusk.mainMvi

import com.lzq.dawn.mvi.view.m.BaseMviRepository
import com.lzq.dawn.network.bean.DawnHttpResult
import com.lzq.dawn.util.log.LogUtils
import com.lzq.dawn.util.time.TimeConstants
import com.lzq.dusk.network.MainService
import com.lzq.dusk.room.BannerBean
import com.lzq.dusk.room.DatabaseManager
import com.lzq.dusk.room.HarmonyBean

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

/**
 * @projectName com.lzq.dusk.mainMvi
 * @author Lzq
 * @date : Created by Lzq on 2024/1/8 14:17
 * @version
 * @description:
 */
class MainMviRepository : BaseMviRepository() {
    fun getBanner(): Flow<DawnHttpResult<List<BannerBean>>> {
        return flow {
            try {
                val currentTime = System.currentTimeMillis()
                DatabaseManager.getInstance().bannerDao().deleteExpiredData(currentTime - TimeConstants.HOUR)
                val result = DatabaseManager.getInstance().bannerDao().getAll()
                if (DatabaseManager.getInstance().bannerDao().getAll().isNotEmpty()) {
                    emit(DawnHttpResult.Success(result))
                    return@flow
                }
            } catch (e: Exception) {
                LogUtils.d(e.message)
            }
            try {
                val result = MainService.getApiService().getBanner()
                if (result is DawnHttpResult.Success) {
                    emit(result)

                    val currentTime = System.currentTimeMillis()
                    for (item in result.data) {
                        item.insertTime = currentTime
                    }
                    DatabaseManager.getInstance().bannerDao().insertAll(result.data)
                }
            } catch (e: Exception) {
                LogUtils.d(e.message)
            }
        }.flowOn(Dispatchers.IO)
    }


    fun getHarmony(): Flow<DawnHttpResult<HarmonyBean>> {
        return flow {
            try {
                val currentTime = System.currentTimeMillis()
                val expirationTime = 60 * 1000
                DatabaseManager.getInstance().harmonyDao().deleteExpiredData(currentTime - expirationTime)
                val result = DatabaseManager.getInstance().harmonyDao().getAll()
                if (result.isNotEmpty()) {
                    emit(DawnHttpResult.Success(result[0]))
                    return@flow
                }
            } catch (e: Exception) {
                LogUtils.d(e.message)
            }
            try {
                val result = MainService.getApiService().getHarmony()
                if (result is DawnHttpResult.Success) {
                    try {
                        val currentTime = System.currentTimeMillis()
                        result.data.insertTime = currentTime
                        DatabaseManager.getInstance().harmonyDao().insert(result.data)
                    } catch (e: Exception) {
                        LogUtils.d(e.message)
                    }
                }
                emit(result)
            } catch (e: Exception) {
                LogUtils.d(e.message)
            }
        }.flowOn(Dispatchers.IO)
    }
}
