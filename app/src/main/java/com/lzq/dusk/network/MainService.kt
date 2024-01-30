package com.lzq.dusk.network

import com.lzq.dawn.network.DawnNetWork
import com.lzq.dawn.network.core.IResponseCode
import okhttp3.Request

/**
 * @projectName com.lzq.dusk.network
 * @author Lzq
 * @date : Created by Lzq on 2024/1/29 10:33
 * @version
 * @description:
 */
object MainService : DawnNetWork<MainApi>(MainApi::class.java) {

    override fun isDebug(): Boolean {
        return true
    }

    override fun getBaseUrl(): String {
        return "https://www.wanandroid.com/"
    }

    override fun createRequestHeader(builder: Request.Builder): Request.Builder? {
        return null
    }

    override fun getHttpResponseCode(): IResponseCode {
        return object : IResponseCode {
            override fun getCodeName(): String {
                return "errorCode"
            }

            override fun getMessageName(): String {
                return "errorMsg"
            }

            override fun getDataName(): String {
                return "data"
            }

            override fun isSuccessCode(): Int {
                return 0

            }
        }
    }

}