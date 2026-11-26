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
 *
 * 将视图绑定与状态观察限定在当前视图生命周期内的页面基类。
 */
abstract class BaseMvvmFragment<out VB : ViewBinding, out VM : IBaseMvvmViewModel> : BaseRootFragment(),
    BaseMvvmView<VB, VM> {

    private var _viewBinding: VB? = null

    private val _viewModel: VM by lazy { createViewModel() }

    /** 仅在当前视图创建后到销毁前访问绑定。 */
    protected val mViewBinding: VB
        get() = checkNotNull(_viewBinding) { "当前视图尚未创建或已经销毁" }

    protected val mViewModel get() = _viewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return createBinding().also { _viewBinding = it }.root
    }

    override fun onViewLifecycleCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewLifecycleCreated(view, savedInstanceState)
        observerViewState()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _viewBinding = null
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
