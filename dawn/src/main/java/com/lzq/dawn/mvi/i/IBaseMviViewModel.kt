package com.lzq.dawn.mvi.i

import com.lzq.dawn.base.controller.IBaseRootViewModel


/**
 * @projectName com.lzq.dawn.mvi.i
 * @author Lzq
 * @date : Created by Lzq on 2024/1/4 17:48
 * @version 0.0.1
 * @description: MVI架构模式ViewModel的接口
 */
interface IBaseMviViewModel<I> : IBaseRootViewModel {


    /**
     * view层发送intent
     */
    fun inputViewState(intent: I)



    /**
     * 观察intent的改变并回调
     */
    suspend fun outputViewState(result: IMviViewStateFlowResult<I>)

}