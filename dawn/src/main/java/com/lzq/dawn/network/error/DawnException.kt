package com.lzq.dawn.network.error

import java.lang.RuntimeException

/**
 * @projectName com.lzq.dawn.network
 * @author Lzq
 * @date : Created by Lzq on 2024/1/26 16:19
 * @version 0.0.21
 * @description: 全局异常
 */
class DawnException (val code: Int, message: String?) : RuntimeException(message)