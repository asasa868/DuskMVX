package com.lzq.dusk.mainMvi

import com.lzq.dawn.mvi.view.m.BaseMviRepository
import com.lzq.dawn.network.bean.DawnHttpResult
import com.lzq.dusk.network.MainService
import com.lzq.dusk.network.bean.BannerBean

/**
 * @projectName com.lzq.dusk.mainMvi
 * @author Lzq
 * @date : Created by Lzq on 2024/1/8 14:17
 * @version
 * @description:
 */
class MainMviRepository : BaseMviRepository() {
    suspend fun getBanner(): DawnHttpResult<List<BannerBean>> {
        return MainService.getApiService().getBanner()
    }
}
