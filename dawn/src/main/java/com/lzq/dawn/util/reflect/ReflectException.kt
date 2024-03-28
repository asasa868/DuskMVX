package com.lzq.dawn.util.reflect

/**
 * @Name :ReflectException
 * @Time :2022/8/29 14:14
 * @Author :  Lzq
 * @Desc : 反射异常
 */
class ReflectException : RuntimeException {
    constructor(message: String?) : super(message)
    constructor(message: String?, cause: Throwable?) : super(message, cause)
    constructor(cause: Throwable?) : super(cause)

    companion object {
        private const val serialVersionUID = 858774075258496016L
    }
}
