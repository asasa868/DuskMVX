package com.lzq.dawn.mvi.i

/**
 * @projectName com.lzq.dawn.mvi.i
 * @author Lzq
 * @date : Created by Lzq on 2024/1/8 11:00
 * @version 0.0.1
 * @description: MVI的flow结果接口
 */
fun interface IMviViewStateFlowResult<T> {
    suspend fun onResult(value: T)
}


fun interface IMviFlowResult<I : BaseMviIntent, T> {

    suspend fun onResult(value: T): I
}