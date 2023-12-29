package com.lzq.dawn.base.view

/**
 * @projectName com.lzq.dawn.base.activity
 * @author Lzq
 * @date : Created by Lzq on 2023/12/25 16:12
 * @version
 * @description: 最初始的Activity接口
 */
interface IBaseRootView {

    /**
     * 获取Activity的LayoutId
     */
    fun getLayoutId(): Int

    /**
     * 初始化View
     */
    fun initView()

    /**
     * 初始化数据
     */
    fun initData()

    /**
     * 请求数据
     */
    fun initRequest()

    /**
     * 显示占位弹框
     */
    fun showLoading(content: String? ="加载中...")

    /**
     * 隐藏占位弹框
     */
    fun hideLoading()


    /**
     * 返回网络状态
     */
    fun getNetConnected():Boolean?

}