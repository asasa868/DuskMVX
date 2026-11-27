package com.lzq.dawn.network.core

import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.lzq.dawn.network.error.DawnException

/**
 * 从任意结构的响应中提取业务数据，字段映射和成功条件由调用方决定。
 *
 * 只负责响应信封，数据类型仍由转换器使用原有泛型和序列化配置解析。
 * 同一解析器可能被并发调用，实现应避免共享可变状态。
 */
fun interface IResponseEnvelopeParser {
    /**
     * 检查业务状态并提取数据节点。
     *
     * @param response 完整响应的根节点，可以是对象、数组、基础值或空节点。
     * @return 需要反序列化的数据节点；返回空引用或空节点时沿用原有空响应语义。
     * @throws DawnException 业务失败；原业务异常交给错误处理器一次后继续传播。
     * @throws JsonParseException 响应结构不符合预期；不作为业务失败处理。
     */
    fun parse(response: JsonElement): JsonElement?
}
