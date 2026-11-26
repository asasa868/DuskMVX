package com.dusk.auto_service_ksp

import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider

/**
 * @projectName com.dusk.auto_service_ksp.AutoServiceProcessorProvider
 * @author Lzq
 * @date : Created by Lzq on 2025/3/17 16:47:19
 * @version 0.0.29
 * @description: 提供 AutoServiceProcessor 实例
 *
 * 为每次处理运行创建独立的服务聚合处理器。
 */
class AutoServiceProcessorProvider : SymbolProcessorProvider {
    /** 根据当前环境创建处理器，不跨编译共享状态。 */
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return AutoServiceProcessor(environment)
    }
}
