package com.lzq.dawn.util.log;

/**
 * @Name :LogClass
 * @Time :2022/8/2 15:30
 * @Author :  Lzq
 * @Desc :
 */
public final class LogClass {
    public abstract static class IFormatter<T> {
        public abstract String format(T t);
    }

    public interface IFileWriter {
        void write(String file, String content);
    }

    public interface OnConsoleOutputListener {
        void onConsoleOutput(@LogUtils.TYPE int type, String tag, String content);
    }

    public interface OnFileOutputListener {
        void onFileOutput(String filePath, String content);
    }
}
