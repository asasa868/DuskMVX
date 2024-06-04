package com.lzq.dawn.mvi.view.v

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lzq.dawn.base.model.IBaseRootRepository
import com.lzq.dawn.base.state.BaseViewState
import com.lzq.dawn.base.state.ViewStateException
import com.lzq.dawn.mvi.view.i.BaseMviIntent
import com.lzq.dawn.mvi.view.i.IMviFlowResult
import com.lzq.dawn.mvi.view.i.IMviViewStateFlowResult
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

/**
 * @projectName com.lzq.dawn.mvi.i
 * @author Lzq
 * @date : Created by Lzq on 2024/1/4 17:46
 * @version 0.0.1
 * @description: MVI架构模式的ViewModel基类
 */
abstract class BaseMviViewModel<I : BaseMviIntent, M : IBaseRootRepository> :
    ViewModel(), IBaseMviViewModel<I, M> {
    private val _viewStateFlow: MutableSharedFlow<I> = MutableSharedFlow(1, 3, BufferOverflow.DROP_OLDEST)

    /**
     * 外部可访问的viewState
     */
    val viewStateFlow: SharedFlow<I> get() = _viewStateFlow

    /**
     * 子类实现此方法来进行处理intent
     */
    abstract fun handleViewState(intent: I)

    /**
     * 创建数据层
     */
    protected val mModel by lazy { createModel() }

    /**
     * 外部通过此方法进行intent传递
     */
    override fun inputViewState(intent: I) {
        handleViewState(intent)
    }

    /**
     * 发送viewState
     */
    override suspend fun outputViewState(result: IMviViewStateFlowResult<I>) {
        _viewStateFlow.collect { result.onResult(it) }
    }

    fun <T> flowResult(
        intent: I,
        flow: Flow<T>,
        result: IMviFlowResult<I, T>,
    ) {
        viewModelScope.launch {
            flow.onStart { _viewStateFlow.emit(intent.also { it.mViewState = BaseViewState.Loading }) }
                .onCompletion { _viewStateFlow.emit(intent.also { it.mViewState = BaseViewState.Success }) }
                .catch { catch ->
                    if (catch is ViewStateException) {
                        _viewStateFlow.emit(
                            intent.also {
                                it.mViewState =
                                    BaseViewState.Error(BaseViewState.Error.VIEW_STATE, msg = catch.message)
                            },
                        )
                    } else {
                        _viewStateFlow.emit(
                            intent.also {
                                it.mViewState =
                                    BaseViewState.Error(BaseViewState.Error.DEFAULT, msg = catch.message)
                            },
                        )
                    }
                }.collect {
                    _viewStateFlow.emit(
                        result.onResult(it).also { event -> event.mViewState = BaseViewState.Result },
                    )
                }
        }
    }
}
