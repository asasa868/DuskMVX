package com.lzq.dawn.mvi.view.v

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lzq.dawn.base.model.IBaseRootRepository
import com.lzq.dawn.base.state.BaseViewState
import com.lzq.dawn.base.state.ViewStateException
import com.lzq.dawn.mvi.view.i.BaseMviIntent
import com.lzq.dawn.mvi.view.i.IMviFlowResult
import com.lzq.dawn.mvi.view.i.IMviViewStateFlowResult
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * @projectName com.lzq.dawn.mvi.i
 * @author Lzq
 * @date : Created by Lzq on 2024/1/4 17:46
 * @version 0.0.1
 * @description: MVI架构模式的ViewModel基类
 *
 * 接收输入意图并输出不重放事件，同时保留内部加载状态的控制器。
 */
abstract class BaseMviViewModel<I : BaseMviIntent, M : IBaseRootRepository> : ViewModel(),
    IBaseMviViewModel<I, M> {
    private val _viewStateFlow = AcknowledgedIntentFlow<I>()
    private val _loadingState = MutableStateFlow(false)
    private var activeRequests = 0

    // 加载提示可恢复，结果和错误仍是没有重放的一次性输出。
    internal val loadingState = _loadingState.asStateFlow()

    /**
     * 不重放的一次性输出；活动观察者消费完成前采用背压，避免可变意图被下一状态覆盖。
     * 公开状态使用共享单例，因此准备与消费在所有模型之间串行，慢观察者也会背压其他模型。
     * 观察者若需要保留数据，应在回调内读取并保存所需值。
     *
     * 外部可访问的viewState
     */
    val viewStateFlow: SharedFlow<I> get() = _viewStateFlow

    /**
     * 子类实现此方法来进行处理intent
     */
    protected abstract fun handleIntent(intent: I)

    /**
     * 创建数据层
     */
    protected val mModel by lazy { createModel() }

    /**
     * 外部通过此方法进行intent传递
     */
    override fun inputIntent(intent: I) {
        handleIntent(intent)
    }

    /**
     * 发送viewState
     */
    override suspend fun outputIntent(result: IMviViewStateFlowResult<I>) {
        _viewStateFlow.collect {
            result.onResult(it)
        }
    }

    /** 收集请求并输出一次性结果；取消继续向上传播，不转成错误或成功事件。 */
    fun <T> flowResult(
        intent: I,
        flow: Flow<T>,
        result: IMviFlowResult<I, T>,
    ) {
        viewModelScope.launch {
            activeRequests++
            _loadingState.value = true
            try {
                publish { intent.also { it.mViewState = BaseViewState.Loading } }
                flow.collect {
                    publish {
                        intent.mViewState.data = null
                        val resultIntent = result.onResult(it)
                        resultIntent.also { output ->
                            output.mViewState = BaseViewState.Result.apply {
                                data = resultIntent.mViewState.data
                            }
                        }
                    }
                }
                publish { intent.also { it.mViewState = BaseViewState.Success } }
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (error: Exception) {
                publish {
                    intent.also {
                        it.mViewState = if (error is ViewStateException) {
                            BaseViewState.Error(BaseViewState.Error.VIEW_STATE, msg = error.message)
                        } else {
                            BaseViewState.Error(BaseViewState.Error.DEFAULT, msg = error.message)
                        }
                    }
                }
            } finally {
                activeRequests--
                _loadingState.value = activeRequests > 0
            }
        }
    }

    private suspend fun publish(prepare: suspend () -> I) {
        outputMutex.withLock { _viewStateFlow.emit(prepare()) }
    }

    /** 共享意图输出互斥锁以串行保护跨模型的状态准备与消费。 */
    private companion object {
        // 公开状态的载荷属于全局单例，锁必须覆盖所有模型的准备与消费阶段。
        val outputMutex = Mutex()
    }
}
