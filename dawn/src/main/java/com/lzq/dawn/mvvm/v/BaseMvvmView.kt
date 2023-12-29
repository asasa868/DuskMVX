package com.lzq.dawn.mvvm.v

import androidx.viewbinding.ViewBinding
import com.lzq.dawn.base.controller.IBaseRootViewModel
import com.lzq.dawn.base.view.IBaseRootView

/**
 * @projectName com.lzq.dawn.mvvm.v
 * @author Lzq
 * @date : Created by Lzq on 2023/12/26 11:40
 * @version 0.0.1
 * @description: MVVM架构模式的V层接口
 */
interface BaseMvvmView<VB : ViewBinding, VM : IBaseRootViewModel> : IBaseRootView {

    /**
     * NVVM采用ViewBinding 来获取布局，所以用不到次方法，返回0不执行
     */
    override fun getLayoutId(): Int {
        return 0
    }

    /**
     * 创建ViewBinding
     */
    fun createBinding(): VB


    /**
     * 创建ViewModel
     */
    fun createViewModel(): VM

}