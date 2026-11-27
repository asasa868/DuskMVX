package com.dusk.auto_service_ksp

import com.google.auto.service.AutoService
import com.google.devtools.ksp.ApiFeatures
import com.google.devtools.ksp.KSApiFeatures
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFile
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.validate

/**
 * @projectName com.dusk.auto_service_ksp.AutoServiceProcessor
 * @author Lzq
 * @date : Created by Lzq on 2025/3/17 16:47:19
 * @version 0.0.29
 * @description: 处理 @AutoService 注解的 KSP 处理器
 *
 * 聚合所有处理轮次中的服务实现，并在处理结束时生成服务注册文件。
 *
 * @param environment 当前处理运行的代码生成器和日志环境。
 */
class AutoServiceProcessor(
    private val environment: SymbolProcessorEnvironment,
) : SymbolProcessor {
    override val apiFeatures: ApiFeatures = KSApiFeatures.DEFAULT
    private val implementations = sortedMapOf<String, MutableSet<String>>()
    private val sources = mutableMapOf<String, MutableMap<String, KSFile>>()

    /** 将已就绪的服务声明加入当前运行，未解析的源符号留给下一轮。 */
    override fun process(resolver: Resolver): List<KSAnnotated> {
        val deferred = mutableListOf<KSAnnotated>()
        resolver.getSymbolsWithAnnotation(AutoService::class.java.name).forEach { symbol ->
            if (!symbol.validate(apiFeatures = apiFeatures)) {
                deferred += symbol
                return@forEach
            }
            if (symbol !is KSClassDeclaration) {
                environment.logger.error("服务注解只能用于类", symbol)
                return@forEach
            }
            val annotation = symbol.annotations.firstOrNull {
                it.annotationType.resolve().declaration.qualifiedName?.asString() == AutoService::class.java.name
            } ?: return@forEach
            // 获取注解的值，即要生成服务文件的接口
            // 先安全转换为 List<*> 避免 unchecked cast
            val serviceTypes = annotation.arguments
                .firstOrNull { it.name?.asString() == "value" }?.value as? List<*> ?: emptyList<Any>()
            if (serviceTypes.any { it !is KSType || it.isError }) {
                deferred += symbol
                return@forEach
            }
            val source = symbol.containingFile ?: return@forEach
            // 过滤出真正的 KSType 类型
            serviceTypes.filterIsInstance<KSType>().forEach service@{ type ->
                val service = type.declaration as? KSClassDeclaration ?: return@service
                val serviceName = binaryName(service)
                val implementationName = binaryName(symbol)
                implementations.getOrPut(serviceName) { sortedSetOf() }.add(implementationName)
                sources.getOrPut(serviceName) { mutableMapOf() }[source.filePath] = source
            }
        }
        return deferred
    }

    /** 所有轮次完成后每个服务仅写一次，来源集合用于聚合增量失效。 */
    override fun finish() {
        implementations.forEach { (serviceName, names) ->
            val dependencies = Dependencies(
                true,
                *sources.getValue(serviceName).values.toTypedArray(),
            )
            environment.codeGenerator.createNewFile(
                dependencies = dependencies,
                packageName = "", // 包名为空，因为是资源文件
                fileName = "META-INF/services/$serviceName",
                extensionName = "",
            ).bufferedWriter(Charsets.UTF_8).use { writer ->
                // 固定顺序和换行让干净构建与增量构建可以直接比较。
                writer.write(names.joinToString(separator = "\n", postfix = "\n"))
            }
        }
    }

    private fun binaryName(declaration: KSClassDeclaration): String {
        val names = generateSequence(declaration) { it.parentDeclaration as? KSClassDeclaration }
            .map { it.simpleName.asString() }.toList().asReversed()
        val packageName = declaration.packageName.asString()
        return listOf(packageName, names.joinToString("$")).filter { it.isNotEmpty() }.joinToString(".")
    }
}
