package com.lzq.dawn.base.controller

import com.lzq.dawn.base.model.IBaseRootRepository

/**
 * @projectName com.lzq.dawn.base.controller
 * @author Lzq
 * @date : Created by Lzq on 2023/12/28 17:48
 * @version 0.0.1
 * @description: 最初始的ViewModel接口
 */
interface IBaseRootViewModel<M : IBaseRootRepository> {
    /**
     * 创建model对象
     */
    fun createModel(): M
}
