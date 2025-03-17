package com.dusk.auto_service_ksp

import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider

/**
 * @projectName com.dusk.auto_service_ksp.AutoServiceProcessorProvider
 * @author Lzq
 * @date : Created by Lzq on 2025
 * @version 0.0.29
 * @description: 提供 AutoServiceProcessor 实例
 *
 */
class AutoServiceProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return AutoServiceProcessor(environment)
    }
}
