package com.dusk.auto_service_ksp.fixture

import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.google.devtools.ksp.symbol.KSAnnotated

/**
 * @projectName com.dusk.auto_service_ksp.fixture.LateImplementationProcessorProvider
 * @author Lzq
 * @date : Created by Lzq on 2026/11/25 13:56:59
 * @version 0.0.30
 * @description: 提供延迟生成服务实现的测试处理器以验证跨轮次聚合
 *
 * 测试用处理器，在第一轮结束后才产生另一个服务实现。
 */
class LateImplementationProcessorProvider : SymbolProcessorProvider {
    /** 创建独立的后轮生成器。 */
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor =
        object : SymbolProcessor {
            private var generated = false

            override fun process(resolver: Resolver): List<KSAnnotated> {
                if (!generated && environment.options["fixture.generateLate"] == "true") {
                    generated = true
                    environment.codeGenerator.createNewFile(
                        Dependencies(true, *resolver.getAllFiles().toList().toTypedArray()),
                        "fixture",
                        "LateImplementation",
                    ).bufferedWriter().use { writer ->
                        writer.write(
                            "package fixture\n" +
                                "interface LateBase : Service\n" +
                                "@com.google.auto.service.AutoService(Service::class)\n" +
                                "class LateImplementation : Service\n",
                        )
                    }
                }
                return emptyList()
            }
        }
}
