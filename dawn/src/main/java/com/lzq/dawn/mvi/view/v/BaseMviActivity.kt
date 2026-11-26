package com.lzq.dawn.mvi.view.v

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewbinding.ViewBinding
import com.hjq.toast.Toaster
import com.lzq.dawn.base.model.IBaseRootRepository
import com.lzq.dawn.base.state.doOnError
import com.lzq.dawn.base.state.doOnLoading
import com.lzq.dawn.base.state.doOnSuccess
import com.lzq.dawn.base.view.BaseRootActivity
import com.lzq.dawn.mvi.view.i.BaseMviIntent
import kotlinx.coroutines.Job
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * @projectName com.lzq.dawn.mvi.v
 * @author Lzq
 * @date : Created by Lzq on 2024/1/5 11:19
 * @version 0.0.1
 * @description: MVI架构模式的Activity基类
 *
 * 在页面初始化前建立内容与意图收集的活动基类。
 */
abstract class BaseMviActivity<M : IBaseRootRepository, out VM : IBaseMviViewModel<I, M>, out VB : ViewBinding, I : BaseMviIntent> :
    BaseRootActivity(), IBaseMviView<M, VB, VM, I> {
    private val _viewBinding: VB by lazy { createBinding() }

    private val _viewModel: VM by lazy { createViewModel() }
    private var outputJob: Job? = null

    protected val mViewBinding get() = _viewBinding

    protected val mViewModel get() = _viewModel

    override fun onOutput(state: Lifecycle.State) {
        outputJob?.cancel()
        outputJob = lifecycleScope.launch(start = CoroutineStart.UNDISPATCHED) {
            if (state == Lifecycle.State.CREATED) {
                collectOutput()
            } else {
                repeatOnLifecycle(state) { collectOutput() }
            }
        }
    }

    private suspend fun collectOutput() = coroutineScope {
        launch(start = CoroutineStart.UNDISPATCHED) {
            _viewModel.outputIntent { intent ->
                outputIntentModel(intent)
                observerViewState(intent)
            }
        }
        launch(start = CoroutineStart.UNDISPATCHED) {
            (_viewModel as? BaseMviViewModel<*, *>)?.loadingState?.collect { loading ->
                if (loading) showLoading() else hideLoading()
            }
        }
    }

    override fun onRootContentReady() {
        super.onRootContentReady()
        setContentView(_viewBinding.root)
        onOutput()
    }

    override fun onDestroy() {
        outputJob?.cancel()
        outputJob = null
        super.onDestroy()
    }

    private fun observerViewState(intent: BaseMviIntent) {
        intent.mViewState.doOnLoading {
            if (_viewModel !is BaseMviViewModel<*, *>) showLoading()
        }.doOnSuccess {
            if (_viewModel !is BaseMviViewModel<*, *>) hideLoading()
        }.doOnError { _, msg ->
            if (_viewModel !is BaseMviViewModel<*, *>) hideLoading()
            msg ?: return@doOnError
            Toaster.show(msg)
        }
    }


    /**
     *获取viewModel的intent
     */
    abstract fun outputIntentModel(intent: I)

}
