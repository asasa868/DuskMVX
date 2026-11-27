package com.lzq.dawn.network.core

/**
 * @projectName com.lzq.dawn.network
 * @author Lzq
 * @date : Created by Lzq on 2024/1/26 15:36
 * @version 0.0.21
 * @description: 子类 实现此接口 来定义自己服务返回的 对应字段的名称
 *
 * 字段按根对象中的字面名称匹配，业务状态为整数。
 * 嵌套字段或其他成功条件可使用 [IResponseEnvelopeParser]，原有实现无需迁移。
 */
interface IResponseCode {
    /**
     * 服务返回的code字段名字
     */
    fun getCodeName(): String

    /**
     * 服务返回的message字段名字
     */
    fun getMessageName(): String

    /**
     * 服务返回的data字段名字
     */
    fun getDataName(): String

    /**
     * 服务返回的成功的code值,根据自己的业务来定义
     *
     * @return Int code
     */
    fun isSuccessCode(): Int
}
