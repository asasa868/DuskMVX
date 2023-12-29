package com.lzq.dawn.util.permission.task



/**
 * className :RequestChain
 * createTime :2022/5/19 15:51
 * @Author :  Lzq
 */
class RequestChain {


    /**
     * 任务流的第一个任务
     */
    private var firstTask: BaseTask? = null

    /**
     * 任务流的最后一个任务
     */
    private var endTask: BaseTask? = null

    /**
     * 添加请求权限的任务到任务链
     */
    internal fun addTaskToChain(task: BaseTask) {
        if (firstTask == null) {
            firstTask = task
        }
        endTask?.nextTask = task
        endTask = task
    }

    /**
     * 开始执行请求权限
     */
    internal fun runTask() {
        firstTask?.request()
    }
}