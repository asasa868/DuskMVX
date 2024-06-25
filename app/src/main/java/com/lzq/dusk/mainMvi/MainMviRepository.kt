package com.lzq.dusk.mainMvi

import com.lzq.dawn.mvi.view.m.BaseMviRepository
import com.lzq.dawn.network.bean.DawnHttpResult
import com.lzq.dusk.network.MainService
import com.lzq.dusk.network.bean.BannerBean
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
            emit(MainService.getApiService().getBanner())
        }.flowOn(Dispatchers.IO)
    }
}
