package com.dusk.auto_service_ksp

import com.google.auto.service.AutoService
import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*
import java.io.OutputStreamWriter

/**
 * 处理 @AutoService 注解的 KSP 处理器
 */
class AutoServiceProcessor(
    private val environment: SymbolProcessorEnvironment,
) : SymbolProcessor {
    private val logger = environment.logger


    // 处理带有 @AutoService 注解的类
    override fun process(resolver: Resolver): List<KSAnnotated> {
        val symbols = resolver.getSymbolsWithAnnotation(AutoService::class.java.name)
        // 遍历每一个带有 @AutoService 注解的类
        for (symbol in symbols) {
            if (symbol is KSClassDeclaration) {
                val annotation = symbol.annotations.find { it.shortName.asString() == AutoService::class.simpleName }
                annotation?.let { it ->
                    // 获取注解的值，即要生成服务文件的接口
                    val services =
                        it.arguments
                            .filter { it.name?.asString() == "value" }
                            .flatMap { arg ->
                                (arg.value as List<KSType>)
                                    .mapNotNull { it.declaration as? KSClassDeclaration }
                            }
                    // 为每个服务接口生成对应的服务文件
                    services.forEach { service ->
                        generateServiceFile(service, symbol)
                    }
                }
            } else {
                log("Only classes can be annotated with @AutoService")
            }
        }

        return emptyList()
    }

    // 生成服务文件
    private fun generateServiceFile(
        service: KSClassDeclaration,
        implementation: KSClassDeclaration,
    ) {
        val serviceName = service.qualifiedName?.asString() ?: return
        val implementationName = implementation.qualifiedName?.asString() ?: return
        val file = environment.codeGenerator.createNewFile(
            dependencies = Dependencies(false), // 不依赖任何文件
            packageName = "", // 包名为空，因为是资源文件
            fileName = "META-INF/services/$serviceName",
            extensionName = ""
        )

        val writer = OutputStreamWriter(file)
        try {
            writer.use {
                // 使用一个Set来记录已经写入的实现类名
                val existingImplementations = mutableSetOf<String>()
                // 读取已存在的内容，并将其添加到set中
                val kspGeneratedFile = environment.codeGenerator.generatedFile.firstOrNull {
                    it.name == "META-INF/services/$serviceName"
                }
                if (kspGeneratedFile != null && kspGeneratedFile.exists()) {
                    kspGeneratedFile.readLines().forEach {
                        existingImplementations.add(it)
                    }
                }
                if (!existingImplementations.contains(implementationName)) {
                    if (existingImplementations.isNotEmpty()) {
                        writer.append("\n")
                    }
                    writer.append(implementationName)
                    log("$implementationName 创建新文件")
                } else {
                    log("$implementationName 已经存在")
                }
            }
        } catch (e: Exception) {
            // Handle exception, e.g., log the error
            logger.error("Error writing to file: ${e.message}")
        }
    }

    private fun log(message: String) {
        logger.warn(message)
    }

}
