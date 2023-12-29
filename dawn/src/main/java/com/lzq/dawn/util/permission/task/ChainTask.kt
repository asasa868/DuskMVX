package com.lzq.dawn.util.permission.task


/**
 * className :ChainTask
 * createTime :2022/5/18 14:00
 * @Author :  Lzq
 * 请求权限接口，定义请求权限方法
 */
interface ChainTask {

    /**
     * 请求权限
     */
    fun request()

    /**
     * 再次请求权限
     */
    fun requestAgain(permissions: List<String>)

    /**
     * 当前权限请求完成
     */
    fun finish()
}