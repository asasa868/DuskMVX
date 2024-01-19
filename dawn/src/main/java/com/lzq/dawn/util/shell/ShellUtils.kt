package com.lzq.dawn.util.shell;

import androidx.annotation.NonNull;

import com.lzq.dawn.DawnBridge;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

/**
 * @Name :ShellUtils
 * @Time :2022/8/29 16:13
 * @Author :  Lzq
 * @Desc : shell
 */
public final class ShellUtils {

    private static final String LINE_SEP = System.getProperty("line.separator");

    private ShellUtils() {

    }

    /**
     * 异步执行命令。
     *
     * @param command  命令。
     * @param isRooted 使用root时为True，否则为false。
     * @param consumer 消费者。
     * @return task
     */
    public static DawnBridge.Task<CommandResult> execCmdAsync(final String command,
                                                            final boolean isRooted,
                                                            final DawnBridge.Consumer<CommandResult> consumer) {
        return execCmdAsync(new String[]{command}, isRooted, true, consumer);
    }

    /**
     * 异步执行命令。
     *
     * @param commands 命令
     * @param isRooted 使用root时为True，否则为false。
     * @param consumer 消费者。
     * @return task
     */
    public static DawnBridge.Task<CommandResult> execCmdAsync(final List<String> commands,
                                                            final boolean isRooted,
                                                            final DawnBridge.Consumer<CommandResult> consumer) {
        return execCmdAsync(commands == null ? null : commands.toArray(new String[]{}), isRooted, true, consumer);
    }

    /**
     * 异步执行命令
     *
     * @param commands 命令
     * @param isRooted 使用root时为True，否则为false。
     * @param consumer 消费者。
     * @return task
     */
    public static DawnBridge.Task<CommandResult> execCmdAsync(final String[] commands,
                                                            final boolean isRooted,
                                                            final DawnBridge.Consumer<CommandResult> consumer) {
        return execCmdAsync(commands, isRooted, true, consumer);
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
    public static DawnBridge.Task<CommandResult> execCmdAsync(final String command,
                                                            final boolean isRooted,
                                                            final boolean isNeedResultMsg,
                                                            final DawnBridge.Consumer<CommandResult> consumer) {
        return execCmdAsync(new String[]{command}, isRooted, isNeedResultMsg, consumer);
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
    public static DawnBridge.Task<CommandResult> execCmdAsync(final List<String> commands,
                                                            final boolean isRooted,
                                                            final boolean isNeedResultMsg,
                                                            final DawnBridge.Consumer<CommandResult> consumer) {
        return execCmdAsync(commands == null ? null : commands.toArray(new String[]{}),
                isRooted,
                isNeedResultMsg,
                consumer);
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
    public static DawnBridge.Task<CommandResult> execCmdAsync(final String[] commands,
                                                            final boolean isRooted,
                                                            final boolean isNeedResultMsg,
                                                            @NonNull final DawnBridge.Consumer<CommandResult> consumer) {
        return DawnBridge.doAsync(new DawnBridge.Task<CommandResult>(consumer) {
            @Override
            public CommandResult doInBackground() {
                return execCmd(commands, isRooted, isNeedResultMsg);
            }
        });
    }

    /**
     * 执行命令。
     *
     * @param command  命令
     * @param isRooted 使用root时为True，否则为false。
     * @return {@link CommandResult} instance
     */
    public static CommandResult execCmd(final String command, final boolean isRooted) {
        return execCmd(new String[]{command}, isRooted, true);
    }

    /**
     * 执行命令。
     *
     * @param command  命令
     * @param envp     环境变量设置。
     * @param isRooted 使用root时为True，否则为false。
     * @return {@link CommandResult} instance
     */
    public static CommandResult execCmd(final String command, final List<String> envp, final boolean isRooted) {
        return execCmd(new String[]{command},
                envp == null ? null : envp.toArray(new String[]{}),
                isRooted,
                true);
    }

    /**
     * 执行命令。
     *
     * @param commands 命令
     * @param isRooted 使用root时为True，否则为false。
     * @return {@link CommandResult} instance
     */
    public static CommandResult execCmd(final List<String> commands, final boolean isRooted) {
        return execCmd(commands == null ? null : commands.toArray(new String[]{}), isRooted, true);
    }

    /**
     * 执行命令。
     *
     * @param commands 命令
     * @param envp     环境变量设置。
     * @param isRooted 使用root时为True，否则为false。
     * @return {@link CommandResult} instance
     */
    public static CommandResult execCmd(final List<String> commands,
                                        final List<String> envp,
                                        final boolean isRooted) {
        return execCmd(commands == null ? null : commands.toArray(new String[]{}),
                envp == null ? null : envp.toArray(new String[]{}),
                isRooted,
                true);
    }

    /**
     * 执行命令。
     *
     * @param commands 命令
     * @param isRooted 使用root时为True，否则为false。
     * @return {@link CommandResult} instance
     */
    public static CommandResult execCmd(final String[] commands, final boolean isRooted) {
        return execCmd(commands, isRooted, true);
    }

    /**
     * 执行命令。
     *
     * @param command         命令
     * @param isRooted        使用root时为True，否则为false。
     * @param isNeedResultMsg True返回结果消息，否则为false。
     * @return {@link CommandResult} instance
     */
    public static CommandResult execCmd(final String command,
                                        final boolean isRooted,
                                        final boolean isNeedResultMsg) {
        return execCmd(new String[]{command}, isRooted, isNeedResultMsg);
    }

    /**
     * 执行命令。
     *
     * @param command         命令
     * @param envp            环境变量设置。
     * @param isRooted        使用root时为True，否则为false。
     * @param isNeedResultMsg True返回结果消息，否则为false。
     * @return {@link CommandResult} instance
     */
    public static CommandResult execCmd(final String command,
                                        final List<String> envp,
                                        final boolean isRooted,
                                        final boolean isNeedResultMsg) {
        return execCmd(new String[]{command}, envp == null ? null : envp.toArray(new String[]{}),
                isRooted,
                isNeedResultMsg);
    }

    /**
     * 执行命令。
     *
     * @param command         命令
     * @param envp            环境变量设置。
     * @param isRooted        使用root时为True，否则为false。
     * @param isNeedResultMsg True返回结果消息，否则为false。
     * @return {@link CommandResult} instance
     */
    public static CommandResult execCmd(final String command,
                                        final String[] envp,
                                        final boolean isRooted,
                                        final boolean isNeedResultMsg) {
        return execCmd(new String[]{command}, envp, isRooted, isNeedResultMsg);
    }

    /**
     * 执行命令。
     *
     * @param commands        命令
     * @param isRooted        使用root时为True，否则为false。
     * @param isNeedResultMsg True返回结果消息，否则为false。
     * @return {@link CommandResult} instance
     */
    public static CommandResult execCmd(final List<String> commands,
                                        final boolean isRooted,
                                        final boolean isNeedResultMsg) {
        return execCmd(commands == null ? null : commands.toArray(new String[]{}),
                isRooted,
                isNeedResultMsg);
    }

    /**
     * 执行命令。
     *
     * @param commands        命令
     * @param isRooted        使用root时为True，否则为false。
     * @param isNeedResultMsg True返回结果消息，否则为false。
     * @return {@link CommandResult} instance
     */
    public static CommandResult execCmd(final String[] commands,
                                        final boolean isRooted,
                                        final boolean isNeedResultMsg) {
        return execCmd(commands, null, isRooted, isNeedResultMsg);
    }

    /**
     * 执行命令。
     *
     * @param commands        命令
     * @param envp            字符串数组，其中每个元素的环境变量设置格式为
     *                        ＜i＞name＜i＞＝＜i＞value＜i＞或＜tt＞null＜tt＞
     *                        如果子进程应继承当前进程的环境。
     * @param isRooted        使用root时为True，否则为false。
     * @param isNeedResultMsg True返回结果消息，否则为false。
     * @return {@link CommandResult} instance
     */
    public static CommandResult execCmd(final String[] commands,
                                        final String[] envp,
                                        final boolean isRooted,
                                        final boolean isNeedResultMsg) {
        int result = -1;
        if (commands == null || commands.length == 0) {
            return new CommandResult(result, "", "");
        }
        Process process = null;
        BufferedReader successResult = null;
        BufferedReader errorResult = null;
        StringBuilder successMsg = null;
        StringBuilder errorMsg = null;
        DataOutputStream os = null;
        try {
            process = Runtime.getRuntime().exec(isRooted ? "su" : "sh", envp, null);
            os = new DataOutputStream(process.getOutputStream());
            for (String command : commands) {
                if (command == null) {
                    continue;
                }
                os.write(command.getBytes());
                os.writeBytes(LINE_SEP);
                os.flush();
            }
            os.writeBytes("exit" + LINE_SEP);
            os.flush();
            result = process.waitFor();
            if (isNeedResultMsg) {
                successMsg = new StringBuilder();
                errorMsg = new StringBuilder();
                successResult = new BufferedReader(
                        new InputStreamReader(process.getInputStream(), "UTF-8")
                );
                errorResult = new BufferedReader(
                        new InputStreamReader(process.getErrorStream(), "UTF-8")
                );
                String line;
                if ((line = successResult.readLine()) != null) {
                    successMsg.append(line);
                    while ((line = successResult.readLine()) != null) {
                        successMsg.append(LINE_SEP).append(line);
                    }
                }
                if ((line = errorResult.readLine()) != null) {
                    errorMsg.append(line);
                    while ((line = errorResult.readLine()) != null) {
                        errorMsg.append(LINE_SEP).append(line);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (successResult != null) {
                    successResult.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (errorResult != null) {
                    errorResult.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (process != null) {
                process.destroy();
            }
        }
        return new CommandResult(
                result,
                successMsg == null ? "" : successMsg.toString(),
                errorMsg == null ? "" : errorMsg.toString()
        );
    }
}
