package com.lzq.dawn.mvvm.vm


import com.lzq.dawn.base.controller.BaseRootViewModel
import com.lzq.dawn.base.model.IBaseRootRepository

/**
 * @projectName com.lzq.dawn.mvvm.vm
 * @author Lzq
 * @date : Created by Lzq on 2023/12/26 14:29
 * @version 0.0.1
 * @description: MVVM架构模式的ViewModel基类
 */
abstract class BaseMvvmViewModel<M : IBaseRootRepository> : BaseRootViewModel() {

    protected val mModel by lazy { createModel() }


}