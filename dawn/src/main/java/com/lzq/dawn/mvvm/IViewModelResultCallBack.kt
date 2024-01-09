package com.lzq.dawn.mvvm

/**
 * @projectName com.lzq.dawn.mvvm
 * @author Lzq
 * @date : Created by Lzq on 2024/1/3 15:56
 * @version 0.0.1
 * @description: MVVM ViewModel的结果回调
 */
sealed interface IViewModelResultCallBack {
    /**
     * 成功
     */
    fun interface OnSuccess<T>{
        fun onSuccess(value:T)
    }

    /**
     * 失败
     */
    fun interface OnFailure<T>{
        fun onFailure(value:T)
    }

}