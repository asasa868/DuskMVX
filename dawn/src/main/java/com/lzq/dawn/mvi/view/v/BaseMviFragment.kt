package com.lzq.dawn.mvi.view.v

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.viewbinding.ViewBinding
import com.hjq.toast.Toaster
import com.lzq.dawn.base.model.IBaseRootRepository
import com.lzq.dawn.base.state.doOnError
import com.lzq.dawn.base.state.doOnLoading
import com.lzq.dawn.base.state.doOnSuccess
import com.lzq.dawn.base.view.BaseRootActivity
import com.lzq.dawn.mvi.view.i.BaseMviIntent
import com.lzq.dawn.tools.extensions.repeatOnLifecycle

/**
 * @projectName com.lzq.dawn.mvi.v
 * @author Lzq
 * @date : Created by Lzq on 2024/1/8 16:00
 * @version 0.0.1
 * @description: MVI架构模式Fragment的基类
 */
abstract class BaseMviFragment<M : IBaseRootRepository, out VM : IBaseMviViewModel<I, M>, out VB : ViewBinding, I : BaseMviIntent> :
    BaseRootActivity(), IBaseMviView<M, VB, VM, I> {
    private val _viewBinding: VB by lazy { createBinding() }

    private val _viewModel: VM by lazy { createViewModel() }

    protected val mViewBinding get() = _viewBinding

    protected val mViewModel get() = _viewModel

    override fun onOutput(state: Lifecycle.State) {
        repeatOnLifecycle(state) {
            _viewModel.outputViewState { intent ->
                outputViewIntent(intent)
                observerViewState(intent)
            }
        }
    }

    override fun onCreateView(
        name: String,
        context: Context,
        attrs: AttributeSet,
    ): View? {
        onOutput()
        return _viewBinding.root
    }

    private fun observerViewState(intent: BaseMviIntent) {
        intent.mViewState.doOnLoading {
            showLoading()
        }.doOnSuccess {
            hideLoading()
        }.doOnError { _, msg ->
            hideLoading()
            msg ?: return@doOnError
            Toaster.show(msg)
        }
    }

    /**
     *获取viewModel的intent
     */
    abstract fun outputViewIntent(intent: I)
}
