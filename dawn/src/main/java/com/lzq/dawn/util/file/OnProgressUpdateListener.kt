package com.lzq.dawn.util.file;

/**
 * @Name :OnProgressUpdateListener
 * @Time :2022/7/21 16:26
 * @Author :  Lzq
 * @Desc :
 */
public interface OnProgressUpdateListener {
    /**
     *  文件io进度
     * @param progress 进度
     */
    void onProgressUpdate(double progress);
}
