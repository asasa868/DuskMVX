package com.lzq.dawn.mvi.v

import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.viewbinding.ViewBinding
import com.lzq.dawn.base.view.BaseRootActivity
import com.lzq.dawn.mvi.i.BaseMviIntent
import com.lzq.dawn.mvi.i.IBaseMviViewModel
import com.lzq.dawn.mvi.i.IMviViewStateFlowResult
import com.lzq.dawn.tools.extensions.repeatOnLifecycle

/**
 * @projectName com.lzq.dawn.mvi.v
 * @author Lzq
 * @date : Created by Lzq on 2024/1/5 11:19
 * @version 0.0.1
 * @description: MVI架构模式的Activity基类
 */
abstract class BaseMviActivity<out VM : IBaseMviViewModel<I>, out VB : ViewBinding, I : BaseMviIntent> :
    BaseRootActivity(), IBaseMviView<VB, VM, I> {

    private val _viewBinding: VB by lazy { createBinding() }

    private val _viewModel: VM by lazy { createViewModel() }

    protected val mViewBinding get() = _viewBinding

    protected val mViewModel get() = _viewModel


    override fun onOutput(
        state: Lifecycle.State, flowResult: IMviViewStateFlowResult<I>
    ) {
        repeatOnLifecycle(state) { _viewModel.outputViewState { intent -> flowResult.onResult(intent) } }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(_viewBinding.root)
    }

}