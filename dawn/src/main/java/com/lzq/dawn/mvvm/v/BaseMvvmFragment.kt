package com.lzq.dawn.mvvm.v

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.lzq.dawn.base.controller.IBaseRootViewModel
import com.lzq.dawn.base.view.BaseRootFragment

/**
 * @projectName com.lzq.dawn.mvvm.v
 * @author Lzq
 * @date : Created by Lzq on 2023/12/26 11:55
 * @version
 * @description: MVVM架构模式的Fragment基类
 */
abstract class BaseMvvmFragment<VB : ViewBinding, VM : IBaseRootViewModel> : BaseRootFragment(),
    BaseMvvmView<VB, VM> {

    private val _viewBinding: VB by lazy { createBinding() }

    private val _viewModel: VM by lazy { createViewModel() }

    protected val mViewBinding get() = _viewBinding

    protected val viewModel get() = _viewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return _viewBinding.root
    }





}