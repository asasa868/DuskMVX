package com.lzq.dusk.network

import com.lzq.dawn.network.bean.DawnHttpResult
import com.lzq.dusk.network.bean.BannerBean
import retrofit2.http.GET

/**
 * @projectName com.lzq.dusk.network
 * @author Lzq
 * @date : Created by Lzq on 2024/1/29 11:11
 * @version
 * @description:
 */
interface MainApi {

    @GET("banner/json")
    suspend fun getBanner(): DawnHttpResult<List<BannerBean>>
}