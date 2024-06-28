package com.lzq.dusk.network

import com.lzq.dawn.network.bean.DawnHttpResult
import com.lzq.dusk.room.BannerBean
import com.lzq.dusk.room.HarmonyBean
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

    @GET("harmony/index/json")
    suspend fun getHarmony(): DawnHttpResult<HarmonyBean>
}