package com.lzq.dawn.mvvm.v

import android.os.Bundle
import androidx.viewbinding.ViewBinding
import com.hjq.toast.Toaster
import com.lzq.dawn.base.state.doOnError
import com.lzq.dawn.base.state.doOnLoading
import com.lzq.dawn.base.state.doOnSuccess
import com.lzq.dawn.base.view.BaseRootActivity
import com.lzq.dawn.mvvm.vm.IBaseMvvmViewModel

/**
 * @projectName com.lzq.dawn.base.mvvm
 * @author Lzq
 * @date : Created by Lzq on 2023/12/26 10:56
 * @version 0.0.1
 * @description: MVVM架构模式的Activity基类
 */
abstract class BaseMvvmActivity<out VB : ViewBinding, out VM : IBaseMvvmViewModel> : BaseRootActivity(),
    BaseMvvmView<VB, VM> {

    private val _viewBinding: VB by lazy { createBinding() }

    private val _viewModel: VM by lazy { createViewModel() }

    protected val mViewBinding get() = _viewBinding

    protected val mViewModel get() = _viewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(_viewBinding.root)
        observerViewState()
    }

    private fun observerViewState(){
        mViewModel.getViewState().observe(this) { viewState ->
            viewState.doOnLoading {
                showLoading()
            }.doOnSuccess {
                hideLoading()
            }.doOnError { _, msg ->
                hideLoading()
                msg ?: return@doOnError
                Toaster.show(msg)
            }
        }
    }
}