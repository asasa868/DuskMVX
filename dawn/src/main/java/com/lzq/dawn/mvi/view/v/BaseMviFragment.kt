package com.lzq.dawn.mvi.view.v

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewbinding.ViewBinding
import com.hjq.toast.Toaster
import com.lzq.dawn.base.model.IBaseRootRepository
import com.lzq.dawn.base.state.doOnError
import com.lzq.dawn.base.state.doOnLoading
import com.lzq.dawn.base.state.doOnSuccess
import com.lzq.dawn.base.view.BaseRootFragment
import com.lzq.dawn.mvi.view.i.BaseMviIntent
import kotlinx.coroutines.Job
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * @projectName com.lzq.dawn.mvi.v
 * @author Lzq
 * @date : Created by Lzq on 2024/1/8 16:00
 * @version 0.0.1
 * @description: MVI架构模式Fragment的基类
 *
 * 将意图收集与视图绑定限定在当前视图生命周期内的页面基类。
 */
abstract class BaseMviFragment<M : IBaseRootRepository, out VM : IBaseMviViewModel<I, M>, out VB : ViewBinding, I : BaseMviIntent> :
    BaseRootFragment(), IBaseMviView<M, VB, VM, I> {
    private var _viewBinding: VB? = null
    private var outputJob: Job? = null

    private val _viewModel: VM by lazy { createViewModel() }

    /** 仅在当前视图创建后到销毁前访问绑定。 */
    protected val mViewBinding: VB
        get() = checkNotNull(_viewBinding) { "当前视图尚未创建或已经销毁" }

    protected val mViewModel get() = _viewModel

    override fun onOutput(state: Lifecycle.State) {
        outputJob?.cancel()
        val owner = viewLifecycleOwner
        outputJob = owner.lifecycleScope.launch(start = CoroutineStart.UNDISPATCHED) {
            // 视图已存在，默认消费必须先于初始化请求，不能等到视图恢复回调才订阅。
            if (state == Lifecycle.State.CREATED) {
                collectOutput()
            } else {
                owner.repeatOnLifecycle(state) { collectOutput() }
            }
        }
    }

    private suspend fun collectOutput() = coroutineScope {
        launch(start = CoroutineStart.UNDISPATCHED) {
            _viewModel.outputIntent { intent ->
                outputViewIntent(intent)
                observerViewState(intent)
            }
        }
        launch(start = CoroutineStart.UNDISPATCHED) {
            (_viewModel as? BaseMviViewModel<*, *>)?.loadingState?.collect { loading ->
                if (loading) showLoading() else hideLoading()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return createBinding().also { _viewBinding = it }.root
    }

    override fun onViewLifecycleCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewLifecycleCreated(view, savedInstanceState)
        onOutput()
    }

    override fun onDestroyView() {
        outputJob?.cancel()
        outputJob = null
        super.onDestroyView()
        _viewBinding = null
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
    abstract fun outputViewIntent(intent: I)
}
