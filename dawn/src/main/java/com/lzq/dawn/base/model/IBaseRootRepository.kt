package com.lzq.dawn.base.model

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector

/**
 * @projectName com.lzq.dawn.base.model
 * @author Lzq
 * @date : Created by Lzq on 2023/12/25 17:45
 * @version 0.0.1
 * @description: 最初始Repository
 */
interface IBaseRootRepository {

    /**
     * 使用Flow进行请求
     */
    fun <T> requestToFlow(requestBlock: suspend FlowCollector<T>.() -> Unit): Flow<T>


    /**
     * 使用Rxjava进行请求
     */
    fun <T : Any> requestToRxjava(
        requestBlock: () -> T,
        onSuccess: (T) -> Unit,
        onError: (Throwable) -> Unit = {},
        onComplete: () -> Unit = {}
    )
}