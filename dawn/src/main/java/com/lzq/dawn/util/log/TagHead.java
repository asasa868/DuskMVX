package com.lzq.dawn.util.log;

/**
 * @Name :TagHead
 * @Time :2022/8/2 15:24
 * @Author :  Lzq
 * @Desc :
 */
public final class TagHead {

    String tag;
    String[] consoleHead;
    String fileHead;

    TagHead(String tag, String[] consoleHead, String fileHead) {
        this.tag = tag;
        this.consoleHead = consoleHead;
        this.fileHead = fileHead;
    }
}
