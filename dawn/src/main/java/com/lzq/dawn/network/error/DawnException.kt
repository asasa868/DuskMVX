package com.lzq.dawn.network.error

import java.util.concurrent.atomic.AtomicBoolean

/**
 * @projectName com.lzq.dawn.network
 * @author Lzq
 * @date : Created by Lzq on 2024/1/26 16:19
 * @version 0.0.21
 * @description: 全局异常
 *
 * 携带业务或框架错误码的异常，保持原有构造入口。
 */
class DawnException(val code: Int, message: String?) : RuntimeException(message) {
    private val handled = AtomicBoolean(false)

    /** 避免同一个业务异常经过响应转换器和调用适配器时重复通知。 */
    internal fun handleOnce(handler: IErrorHandler?) {
        if (handler != null && handled.compareAndSet(false, true)) handler.handleError(this)
    }
}
