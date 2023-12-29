package com.lzq.dawn.util.permission.callback


/**
 * className :RequestCallback
 * createTime :2022/5/19 15:37
 * @Author :  Lzq
 * 请求权限结果接口
 */
interface RequestCallback {


    /**
     * 请求接口回调
     * @param allGranted 是否全部请求成功
     * @param grantedList 请求成功权限
     * @param deniedList 请求失败权限
     */
    fun onResult(allGranted: Boolean, grantedList: List<String>, deniedList: List<String>)
}