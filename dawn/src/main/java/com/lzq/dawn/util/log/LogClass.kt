package com.lzq.dawn.util.log

/**
 * @Name :LogClass
 * @Time :2022/8/2 15:30
 * @Author :  Lzq
 * @Desc :
 */
class LogClass {
    abstract class IFormatter<T> {
        abstract fun format(t: T): String?
    }

    interface IFileWriter {
        fun write(file: String?, content: String?)
    }

    interface OnConsoleOutputListener {
        fun onConsoleOutput(@LogUtils.TYPE type: Int, tag: String?, content: String?)
    }

    interface OnFileOutputListener {
        fun onFileOutput(filePath: String?, content: String?)
    }
}