package kr.urbansoft.kursmapper.processor.domain.service

import kr.urbansoft.kursmapper.processor.domain.model.config.ContextConfig
import kr.urbansoft.kursmapper.processor.domain.model.function.MappingFunction
import kr.urbansoft.kursmapper.processor.domain.model.function.MappingFunctionId
import kr.urbansoft.kursmapper.processor.domain.model.kurstype.KursType
import kr.urbansoft.kursmapper.processor.domain.model.kurstype.KursTypeId
import kr.urbansoft.kursmapper.processor.domain.service.guide.LanguageProvider
import kr.urbansoft.kursmapper.processor.shared.exception.ExceptionMessageSupport
import kr.urbansoft.kursmapper.processor.shared.exception.ExceptionType

class GenerateGuideDomainService {
  interface Context {
    val contextConfig: ContextConfig
    val languageProvider: LanguageProvider
    val mappingFunction: MappingFunction
    val source: KursType
    val target: KursType
    val userDefinedMapperQualifiedNameSet: Set<String>
    val overwrittenSandboxIdSet: Set<MappingFunctionId>

    fun MappingFunctionId.loadMappingFunction(): MappingFunction
  }

  fun List<MappingFunction>.findUserDefinedMapperQualifiedNameSet(asKursType: KursTypeId.() -> KursType?): Set<String> =
    filter { it.isUser() }
      .map {
        val source = it.sourceId.asKursType() ?: throw ExceptionMessage.SOURCE_IS_NOT_FOUND.create(it.sourceId.name.value)
        source.mapperQualifiedName()
      }
      .toSet()

  fun List<MappingFunction>.findMappingFunctionsToReport(overwrittenSandboxIdSet: Set<MappingFunctionId>): List<MappingFunction> =
    filter { it.shouldReport() || it.id in overwrittenSandboxIdSet }.sortedWith(compareBy({ it.calleeIdSet.size }, { it.id.value }))

  fun List<MappingFunction>.findOverwrittenSandboxIdSet(sandboxMappingFunctionIdSet: Set<MappingFunctionId>): Set<MappingFunctionId> =
    filter { it.isUser() }.filter { it.id in sandboxMappingFunctionIdSet }.map { it.id }.toSet()

  fun List<MappingFunction>.findAllSandboxToReport(): List<MappingFunction> =
    filter { it.isSandbox() }.sortedWith(compareBy({ it.calleeIdSet.size }, { it.id.value }))

  fun Context.generateToResolveMappingFunction(): String {
    return when {
      mappingFunction.isArgumentLacked() -> generateForArgumentLacked()
      mappingFunction.isImplementationRequired() -> generateForImplementationRequired()
      mappingFunction.isUser() && mappingFunction.id in overwrittenSandboxIdSet -> generateForOverwrittenSandbox()
      else -> error("Theoretically impossible case: ${mappingFunction.id.value}")
    }
  }

  fun Context.generateForArgumentLacked(): String {
    if (!mappingFunction.isPurpose()) error("Theoretically impossible case: ${mappingFunction.id.value}")

    var argumentAdded = mappingFunction
    mappingFunction.calleeIdSet.map { it.loadMappingFunction() }.forEach { argumentAdded = argumentAdded.addArgumentList(it.argumentList) }
    val arguments = mappingFunction.argumentList.joinToString(separator = ", ") { "${it.name.value}: ${it.typeId.simpleName().value}" }
    val finalArguments = argumentAdded.argumentList.joinToString(separator = ", ") { "${it.name.value}: ${it.typeId.simpleName().value}" }

    var number = 1
    with(languageProvider) {
      return buildString {
        appendLine(kursMapperGuideStart)
        appendLine()
        appendLine(argumentLackedReferToBelow)
        appendLine()
        appendLine("${number++}. $openConfigInterfaceFile")
        appendLine("   - ${configInterfaceFQCN}: ${contextConfig.configInterfacePackageName.value}.${contextConfig.configInterfaceSimpleName.value}")
        appendLine()
        appendLine("${number++}. $changeArgumentsToFinal")
        appendLine("   - $purposeFunction")
        appendLine("        fun ${source.id.simpleName().value}.${target.mappingFunctionName.value}(${arguments}): ${target.id.simpleName().value}")
        appendLine("   - $reference ${finalArgument}: $finalArguments")
        appendLine(
          "        fun ${source.id.simpleName().value}.${target.mappingFunctionName.value}(${finalArguments}): ${target.id.simpleName().value}"
        )
        appendLine("   - $reference ${sourceFQCN}: ${source.id.qualifiedName().value}")
        appendLine("   - $reference ${targetFQCN}: ${target.id.qualifiedName().value}")
        appendLine("   - $reference $finalArgumentFQCN")
        argumentAdded.argumentList.map { "      · ${it.name.value}: ${it.typeId.qualifiedName().value}" }.forEach { appendLine(it) }
        appendLine("   - $reference $explanationOfPurposeFunctionName")
        appendLine("   - $reference $explanationOfArgumentLacked")
        appendLine()
        appendLine("${number}. $rebuildAfterChangingArguments")
        appendLine()
        appendLine(kursMapperGuideEnd)
      }
    }
  }

  fun Context.generateForImplementationRequired(): String {
    var number = 1
    with(languageProvider) {
      return buildString {
        appendLine(kursMapperGuideStart)
        appendLine()
        appendLine(implementationRequiredReferToBelow)
        appendLine()
        if (source.mapperQualifiedName() !in userDefinedMapperQualifiedNameSet) {
          appendLine("${number++}. $createMapper")
          appendLine("    - ${mapperPackage}: ${source.mapperPackageName.value}")
          appendLine("    - ${fileName}: ${source.mapperName.value}.kt")
          appendLine("    - $createMapperFile")
          appendLine("        @JvmInline")
          appendLine(
            "        value class ${source.mapperName.value}(val ${contextConfig.mapperSourceVariableName.value}: ${source.id.simpleName().value})"
          )
          appendLine("    - $reference ${sourceFQCN}: ${source.id.qualifiedName().value}")
          appendLine()
        }
        val arguments = mappingFunction.argumentList.joinToString(separator = ", ") { "${it.name.value}: ${it.typeId.simpleName().value}" }
        val argumentFQCNs = mappingFunction.argumentList.map { "      · ${it.name.value}: ${it.typeId.qualifiedName().value}" }
        appendLine("${number++}. $addFunctionAndImplementationIt")
        appendLine("    - ${mapperFQCN}: ${source.mapperQualifiedName()}")
        appendLine("    - $addFunction")
        appendLine(
          "        fun ${target.mappingFunctionName.value}(${arguments}): ${target.id.simpleName().value} ${mappingFunction.body.value.takeIf { it.isNotBlank() }?.let { "{ ${mappingFunction.body.value} }" } ?: "= TODO(\"${implementHere}\")"}"
        )
        appendLine("    - $reference ${sourceFQCN}: ${source.id.qualifiedName().value}")
        appendLine("    - $reference ${targetFQCN}: ${target.id.qualifiedName().value}")
        if (argumentFQCNs.isNotEmpty()) appendLine("    - $reference $functionArgumentFQCN")
        argumentFQCNs.forEach { appendLine(it) }
        appendLine()
        appendLine("${number}. $rebuildAfterImplementation")
        appendLine()
        appendLine(kursMapperGuideEnd)
      }
    }
  }

  fun Context.generateForOverwrittenSandbox(): String {
    var number = 1
    with(languageProvider) {
      return buildString {
        appendLine(kursMapperGuideStart)
        appendLine()
        appendLine(sandboxOverwrittenReferToBelow)
        appendLine()
        appendLine("${number++}. $openConfigInterfaceFile")
        appendLine("   - ${configInterfaceFQCN}: ${contextConfig.configInterfacePackageName.value}.${contextConfig.configInterfaceSimpleName.value}")
        appendLine()
        appendLine("${number++}. $deleteSandbox")
        appendLine("    @JvmInline")
        appendLine(
          "    value class ${source.mapperName.value}(val ${contextConfig.mapperSourceVariableName.value}: ${source.id.simpleName().value}) {"
        )
        appendLine("      fun ${target.mappingFunctionName.value}(): ${target.id.simpleName().value}")
        appendLine("    }")
        appendLine("    - $reference ${sourceFQCN}: ${source.id.qualifiedName().value}")
        appendLine("    - $reference ${targetFQCN}: ${target.id.qualifiedName().value}")
        appendLine("    - $reference $explanationOfSandboxSignature")
        appendLine("    - $reference $explanationOfSandboxRemoval")
        appendLine()
        appendLine("${number}. $rebuildAfterRemoval")
        appendLine()
        appendLine(kursMapperGuideEnd)
      }
    }
  }

  fun Context.generateToPromoteSandbox(): String {
    var number = 1
    with(languageProvider) {
      return buildString {
        appendLine(kursMapperGuideStart)
        appendLine()
        appendLine(promotingSandboxReferToBelow)
        appendLine()
        if (source.mapperQualifiedName() !in userDefinedMapperQualifiedNameSet) {
          appendLine("${number++}. $createMapper")
          appendLine("    - ${mapperPackage}: ${source.mapperPackageName.value}")
          appendLine("    - ${fileName}: ${source.mapperName.value}.kt")
          appendLine("    - $createMapperFile")
          appendLine("        @JvmInline")
          appendLine(
            "        value class ${source.mapperName.value}(val ${contextConfig.mapperSourceVariableName.value}: ${source.id.simpleName().value})"
          )
          appendLine("    - $reference ${sourceFQCN}: ${source.id.qualifiedName().value}")
          appendLine()
        }
        val arguments = mappingFunction.argumentList.joinToString(separator = ", ") { "${it.name.value}: ${it.typeId.simpleName().value}" }
        val argumentFQCNs = mappingFunction.argumentList.map { "      · ${it.name.value}: ${it.typeId.qualifiedName().value}" }
        appendLine("${number++}. $addFunctionToMapper")
        appendLine("    - ${mapperFQCN}: ${source.mapperQualifiedName()}")
        appendLine("    - $addFunction")
        appendLine(
          "        fun ${target.mappingFunctionName.value}(${arguments}): ${target.id.simpleName().value} ${mappingFunction.body.value.takeIf { it.isNotBlank() }?.let { "{ ${mappingFunction.body.value} }" } ?: "= TODO(\"${implementHere}\")"}"
        )
        appendLine("    - $reference ${sourceFQCN}: ${source.id.qualifiedName().value}")
        appendLine("    - $reference ${targetFQCN}: ${target.id.qualifiedName().value}")
        if (argumentFQCNs.isNotEmpty()) appendLine("    - $reference $functionArgumentFQCN")
        argumentFQCNs.forEach { appendLine(it) }
        appendLine()
        appendLine("${number++}. $replaceFunctionBodyWithIDEFeature")
        appendLine("    fun ${target.mappingFunctionName.value}(${arguments}): ${target.id.simpleName().value} {")
        val bridgeCode =
          mappingFunction.body.value.takeIf { it.isNotBlank() }?.let { "{ ${mappingFunction.body.value} }" } ?: "= TODO(\"${implementHere}\")"
        appendLine("      TODO(\"${pasteHere.replace("{code: bridge}", bridgeCode)}\")")
        appendLine("    }")
        appendLine()
        appendLine("${number++}. $deleteSandbox")
        appendLine("    @JvmInline")
        appendLine(
          "    value class ${source.mapperName.value}(val ${contextConfig.mapperSourceVariableName.value}: ${source.id.simpleName().value}) {"
        )
        appendLine("      fun ${target.mappingFunctionName.value}(): ${target.id.simpleName().value}")
        appendLine("    }")
        appendLine("    - $important $whyDeleteSandbox1")
        appendLine("    - $important $whyDeleteSandbox2")
        appendLine("    - $reference ${sourceFQCN}: ${source.id.qualifiedName().value}")
        appendLine("    - $reference ${targetFQCN}: ${target.id.qualifiedName().value}")
        appendLine("    - $reference $explanationOfSandboxSignature")
        appendLine()
        appendLine("${number}. $rebuildAfterApprovingSandbox")
        appendLine()
        appendLine(kursMapperGuideEnd)
      }
    }
  }

  enum class ExceptionMessage(override val value: String, override val type: ExceptionType) : ExceptionMessageSupport {
    SOURCE_IS_NOT_FOUND("source is not found: {}", ExceptionType.NOT_FOUND)
  }
}
