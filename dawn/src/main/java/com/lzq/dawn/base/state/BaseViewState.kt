package com.lzq.dawn.base.state

/**
 * @projectName com.lzq.dawn.base.state
 * @author Lzq
 * @date : Created by Lzq on 2024/1/2 11:01
 * @version 0.0.1
 * @description: 最初始的ViewState
 */
sealed class BaseViewState {

    /**
     * 默认
     */
    data object Default : BaseViewState()

    /**
     * 加载弹框
     */
    data object Loading : BaseViewState()

    /**
     * 成功
     */
    data object Success : BaseViewState()

    /**
     * 失败
     */
    data class Error(val code: Int = DEFAULT, val msg: String? = null) : BaseViewState() {
        companion object {
            const val DEFAULT = -1
            const val VIEW_STATE = -2
        }
    }

    /**
     * 空
     */
    data object Empty : BaseViewState()

    /**
     * 不为空的结果
     */
    data object Result : BaseViewState()

    var data: Any? = null
}

class ViewStateException(msg: String?, throwable: Throwable? = null) : Exception(msg, throwable)

fun BaseViewState.doOnLoading(block: () -> Unit): BaseViewState {
    if (this is BaseViewState.Loading) block()
    return this
}

fun BaseViewState.doOnSuccess(block: () -> Unit): BaseViewState {
    if (this is BaseViewState.Success) block()
    return this
}

fun BaseViewState.doOnError(block: (Int, String?) -> Unit): BaseViewState {
    if (this is BaseViewState.Error) block(code, msg)
    return this
}

fun BaseViewState.doOnEmpty(block: () -> Unit): BaseViewState {
    if (this is BaseViewState.Empty) block()
    return this
}

fun BaseViewState.doOnResult(block: () -> Unit): BaseViewState {
    if (this is BaseViewState.Result) block()
    return this
}