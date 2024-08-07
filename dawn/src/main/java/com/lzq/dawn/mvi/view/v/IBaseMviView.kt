package com.lzq.dawn.mvi.view.v

import androidx.lifecycle.Lifecycle
import androidx.viewbinding.ViewBinding
import com.lzq.dawn.base.model.IBaseRootRepository
import com.lzq.dawn.base.view.IBaseRootView
import com.lzq.dawn.mvi.view.i.BaseMviIntent

/**
 * @projectName com.lzq.dawn.mvi.v
 * @author Lzq
 * @date : Created by Lzq on 2024/1/5 10:17
 * @version 0.0.1
 * @description: MVI架构模式VIew的接口
 */
interface IBaseMviView<M : IBaseRootRepository, out VB : ViewBinding, out VM : IBaseMviViewModel<I, M>, I : BaseMviIntent> :
    IBaseRootView {
    /**
     * MVI采用ViewBinding 来获取布局，所以用不到次方法，返回0不执行
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

    /**
     * view层监听intent状态变化
     */
    fun onOutput(state: Lifecycle.State = Lifecycle.State.CREATED)
}
