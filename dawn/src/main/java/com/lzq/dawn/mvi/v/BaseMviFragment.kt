package com.lzq.dawn.mvi.v

import android.content.Context
import android.util.AttributeSet
import android.view.View
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
 * @date : Created by Lzq on 2024/1/8 16:00
 * @version 0.0.1
 * @description: MVI架构模式Fragment的基类
 */
abstract class BaseMviFragment<out VM : IBaseMviViewModel<I>, out VB : ViewBinding, I : BaseMviIntent> :
    BaseRootActivity(), IBaseMviView<VB, VM, I> {

    private val _viewBinding: VB by lazy { createBinding() }

    private val _viewModel: VM by lazy { createViewModel() }

    protected val mViewBinding get() = _viewBinding

    protected val mViewModel get() = _viewModel


    override fun onOutput(
        state: Lifecycle.State
    ) {
        repeatOnLifecycle(state) {
            _viewModel.outputViewState { intent ->
                outputViewIntent(intent)
            }
        }
    }

    /**
     *获取viewModel的intent
     */
    abstract fun outputViewIntent(intent: I)
    override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
        return _viewBinding.root
    }
}
