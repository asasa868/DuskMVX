package com.lzq.dawn.util.shell

/**
 * @Name :CommandResult
 * @Time :2022/8/29 16:14
 * @Author :  Lzq
 * @Desc :
 */
class CommandResult(var result: Int, private var successMsg: String, private var errorMsg: String) {
    override fun toString(): String {
        return """
              result: $result
              successMsg: $successMsg
              errorMsg: $errorMsg
              """.trimIndent()
    }
}