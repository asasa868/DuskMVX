package com.lzq.dawn.mvvm.v

import android.os.Bundle
import androidx.viewbinding.ViewBinding
import com.lzq.dawn.base.controller.IBaseRootViewModel
import com.lzq.dawn.base.view.BaseRootActivity

/**
 * @projectName com.lzq.dawn.base.mvvm
 * @author Lzq
 * @date : Created by Lzq on 2023/12/26 10:56
 * @version 0.0.1
 * @description: MVVM架构模式的Activity基类
 */
abstract class BaseMvvmActivity<VB : ViewBinding, VM : IBaseRootViewModel> : BaseRootActivity(),
    BaseMvvmView<VB, VM> {

    private val _viewBinding: VB by lazy { createBinding() }

    private val _viewModel: VM by lazy { createViewModel() }

    protected val mViewBinding get() = _viewBinding

    protected val mViewModel get() = _viewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(_viewBinding.root)
    }

}