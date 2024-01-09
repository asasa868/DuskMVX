package com.lzq.dawn.mvvm.v

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.hjq.toast.Toaster
import com.lzq.dawn.base.state.doOnError
import com.lzq.dawn.base.state.doOnLoading
import com.lzq.dawn.base.state.doOnSuccess
import com.lzq.dawn.base.view.BaseRootFragment
import com.lzq.dawn.mvvm.vm.IBaseMvvmViewModel

/**
 * @projectName com.lzq.dawn.mvvm.v
 * @author Lzq
 * @date : Created by Lzq on 2023/12/26 11:55
 * @version 0.0.1
 * @description: MVVM架构模式的Fragment基类
 */
abstract class BaseMvvmFragment<out VB : ViewBinding, out VM : IBaseMvvmViewModel> : BaseRootFragment(),
    BaseMvvmView<VB, VM> {

    private val _viewBinding: VB by lazy { createBinding() }

    private val _viewModel: VM by lazy { createViewModel() }

    protected val mViewBinding get() = _viewBinding

    protected val mViewModel get() = _viewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return _viewBinding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        observerViewState()
    }

    private fun observerViewState() {
        mViewModel.getViewState().observe(viewLifecycleOwner) { viewState ->
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