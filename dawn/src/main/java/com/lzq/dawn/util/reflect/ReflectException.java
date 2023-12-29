package com.lzq.dawn.util.reflect;

/**
 * @Name :ReflectException
 * @Time :2022/8/29 14:14
 * @Author :  Lzq
 * @Desc : 反射异常
 */
public class ReflectException extends RuntimeException  {

    private static final long serialVersionUID = 858774075258496016L;

    public ReflectException(String message) {
        super(message);
    }

    public ReflectException(String message, Throwable cause) {
        super(message, cause);
    }

    public ReflectException(Throwable cause) {
        super(cause);
    }
}
