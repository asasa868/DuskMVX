package com.lzq.dawn.util.shell;

/**
 * @Name :CommandResult
 * @Time :2022/8/29 16:14
 * @Author :  Lzq
 * @Desc :
 */
public class CommandResult {

    public int    result;
    public String successMsg;
    public String errorMsg;

    public CommandResult(final int result, final String successMsg, final String errorMsg) {
        this.result = result;
        this.successMsg = successMsg;
        this.errorMsg = errorMsg;
    }

    @Override
    public String toString() {
        return "result: " + result + "\n" +
                "successMsg: " + successMsg + "\n" +
                "errorMsg: " + errorMsg;
    }
}
