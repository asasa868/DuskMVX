package com.lzq.dawn.util.shell

import com.lzq.dawn.DawnBridge
import com.lzq.dawn.DawnBridge.doAsync
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets

/**
 * @Name :ShellUtils
 * @Time :2022/8/29 16:13
 * @Author :  Lzq
 * @Desc : shell
 */
object ShellUtils {
    private val LINE_SEP = System.getProperty("line.separator")

    /**
     * 异步执行命令。
     *
     * @param command  命令。
     * @param isRooted 使用root时为True，否则为false。
     * @param consumer 消费者。
     * @return task
     */
    fun execCmdAsync(
        command: String, isRooted: Boolean, consumer: DawnBridge.Consumer<CommandResult>
    ): DawnBridge.Task<CommandResult> {
        return execCmdAsync(arrayOf(command), isRooted, true, consumer)
    }

    /**
     * 异步执行命令
     *
     * @param commands 命令
     * @param isRooted 使用root时为True，否则为false。
     * @param consumer 消费者。
     * @return task
     */
    fun execCmdAsync(
        commands: Array<String>?, isRooted: Boolean, consumer: DawnBridge.Consumer<CommandResult>
    ): DawnBridge.Task<CommandResult> {
        return execCmdAsync(commands, isRooted, true, consumer)
    }

    /**
     * 异步执行命令
     *
     * @param command         命令
     * @param isRooted        使用root时为True，否则为false。
     * @param isNeedResultMsg True返回结果消息，否则为false。
     * @param consumer        消费者
     * @return task
     */
    fun execCmdAsync(
        command: String,
        isRooted: Boolean,
        isNeedResultMsg: Boolean,
        consumer: DawnBridge.Consumer<CommandResult>
    ): DawnBridge.Task<CommandResult> {
        return execCmdAsync(arrayOf(command), isRooted, isNeedResultMsg, consumer)
    }

    /**
     * 异步执行命令
     *
     * @param commands        命令
     * @param isRooted        使用root时为True，否则为false。
     * @param isNeedResultMsg True返回结果消息，否则为false。
     * @param consumer        消费者
     * @return task
     */
    fun execCmdAsync(
        commands: Array<String>?,
        isRooted: Boolean,
        isNeedResultMsg: Boolean,
        consumer: DawnBridge.Consumer<CommandResult>
    ): DawnBridge.Task<CommandResult> {
        return doAsync(object : DawnBridge.Task<CommandResult>(consumer) {
            override fun doInBackground(): CommandResult {
                return execCmd(commands, isRooted, isNeedResultMsg)
            }
        })
    }

    /**
     * 执行命令。
     *
     * @param command  命令
     * @param isRooted 使用root时为True，否则为false。
     * @return [CommandResult] instance
     */
    fun execCmd(command: String, isRooted: Boolean): CommandResult {
        return execCmd(arrayOf(command), isRooted, true)
    }

    /**
     * 执行命令。
     *
     * @param command         命令
     * @param isRooted        使用root时为True，否则为false。
     * @param isNeedResultMsg True返回结果消息，否则为false。
     * @return [CommandResult] instance
     */
    fun execCmd(
        command: String, isRooted: Boolean, isNeedResultMsg: Boolean
    ): CommandResult {
        return execCmd(arrayOf(command), isRooted, isNeedResultMsg)
    }

    /**
     * 执行命令。
     *
     * @param command         命令
     * @param envp            环境变量设置。
     * @param isRooted        使用root时为True，否则为false。
     * @param isNeedResultMsg True返回结果消息，否则为false。
     * @return [CommandResult] instance
     */
    fun execCmd(
        command: String, envp: Array<String?>?, isRooted: Boolean, isNeedResultMsg: Boolean
    ): CommandResult {
        return execCmd(arrayOf(command), envp, isRooted, isNeedResultMsg)
    }

    /**
     * 执行命令。
     *
     * @param commands        命令
     * @param isRooted        使用root时为True，否则为false。
     * @param isNeedResultMsg True返回结果消息，否则为false。
     * @return [CommandResult] instance
     */
    @JvmOverloads
    fun execCmd(
        commands: Array<String>?, isRooted: Boolean, isNeedResultMsg: Boolean = true
    ): CommandResult {
        return execCmd(commands, null, isRooted, isNeedResultMsg)
    }

    /**
     * 执行命令。
     *
     * @param commands        命令
     * @param envp            字符串数组，其中每个元素的环境变量设置格式为
     * ＜i＞name＜i＞＝＜i＞value＜i＞或＜tt＞null＜tt＞
     * 如果子进程应继承当前进程的环境。
     * @param isRooted        使用root时为True，否则为false。
     * @param isNeedResultMsg True返回结果消息，否则为false。
     * @return [CommandResult] instance
     */
    fun execCmd(
        commands: Array<String>?, envp: Array<String?>?, isRooted: Boolean, isNeedResultMsg: Boolean
    ): CommandResult {
        var result = -1
        if (commands.isNullOrEmpty()) {
            return CommandResult(result, "", "")
        }
        var process: Process? = null
        var successResult: BufferedReader? = null
        var errorResult: BufferedReader? = null
        var successMsg: StringBuilder? = null
        var errorMsg: StringBuilder? = null
        var os: DataOutputStream? = null
        try {
            process = Runtime.getRuntime().exec(if (isRooted) "su" else "sh", envp, null)
            os = DataOutputStream(process.outputStream)
            for (command in commands) {
                os.write(command.toByteArray())
                os.writeBytes(LINE_SEP)
                os.flush()
            }
            os.writeBytes("exit$LINE_SEP")
            os.flush()
            result = process.waitFor()
            if (isNeedResultMsg) {
                successMsg = StringBuilder()
                errorMsg = StringBuilder()
                successResult = BufferedReader(
                    InputStreamReader(process.inputStream, StandardCharsets.UTF_8)
                )
                errorResult = BufferedReader(
                    InputStreamReader(process.errorStream, StandardCharsets.UTF_8)
                )
                var line: String?
                if (successResult.readLine().also { line = it } != null) {
                    successMsg.append(line)
                    while (successResult.readLine().also { line = it } != null) {
                        successMsg.append(LINE_SEP).append(line)
                    }
                }
                if (errorResult.readLine().also { line = it } != null) {
                    errorMsg.append(line)
                    while (errorResult.readLine().also { line = it } != null) {
                        errorMsg.append(LINE_SEP).append(line)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                os?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            try {
                successResult?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            try {
                errorResult?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            process?.destroy()
        }
        return CommandResult(
            result, successMsg?.toString() ?: "", errorMsg?.toString() ?: ""
        )
    }
}