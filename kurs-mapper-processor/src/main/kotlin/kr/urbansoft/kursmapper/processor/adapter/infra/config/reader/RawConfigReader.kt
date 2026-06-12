package kr.urbansoft.kursmapper.processor.adapter.infra.config.reader

import com.google.devtools.ksp.isPublic
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import kr.urbansoft.kursmapper.processor.adapter.infra.config.mapper.kotlin.configMapper
import kr.urbansoft.kursmapper.processor.adapter.infra.config.mapper.symbol.configMapper
import kr.urbansoft.kursmapper.processor.adapter.infra.config.model.RawContextConfig
import kr.urbansoft.kursmapper.processor.adapter.infra.config.model.RawKursTypeRuleConfig
import kr.urbansoft.kursmapper.processor.adapter.infra.config.model.RawPackageRuleConfig
import kr.urbansoft.kursmapper.processor.adapter.infra.config.model.RawPurposeMappingFunction
import kr.urbansoft.kursmapper.processor.adapter.infra.config.model.RawSandboxMappingFunction
import kr.urbansoft.kursmapper.processor.adapter.infra.ksp.extension.getAllMemberFunctionDeclarations
import kr.urbansoft.kursmapper.processor.adapter.infra.ksp.extension.getAnnotationOrNull
import kr.urbansoft.kursmapper.processor.adapter.infra.ksp.extension.getArgumentValueAsListOrNull
import kr.urbansoft.kursmapper.processor.adapter.infra.ksp.extension.getArgumentValueAsTrimmedStringOrNull
import kr.urbansoft.kursmapper.processor.adapter.infra.ksp.extension.getArgumentValueOrNull
import kr.urbansoft.kursmapper.processor.adapter.infra.ksp.extension.getName
import kr.urbansoft.kursmapper.processor.adapter.infra.ksp.extension.getPackageName
import kr.urbansoft.kursmapper.processor.adapter.infra.ksp.extension.getPrimaryConstructorDeclaration
import kr.urbansoft.kursmapper.processor.adapter.infra.ksp.extension.getSimpleName
import kr.urbansoft.kursmapper.processor.adapter.infra.ksp.mapper.symbol.kspMapper
import kr.urbansoft.kursmapper.processor.domain.model.config.definition.KursContextDefinition
import kr.urbansoft.kursmapper.processor.domain.model.config.definition.KursPackageRuleDefinition
import kr.urbansoft.kursmapper.processor.domain.model.config.definition.KursRuleDefinition

class RawConfigReader(
  private val configInterface: KSClassDeclaration,
  private val kspResolver: Resolver,
) {
  private val contextAnnotation: KSAnnotation =
    configInterface.getAnnotationOrNull(KursContextDefinition.QUALIFIED_NAME) ?: error("KursContext annotation is not found")
  private val contextFunctions: List<KSFunctionDeclaration> = configInterface.getAllFunctions().toList()
  private val rawContextConfig: RawContextConfig =
    contextAnnotation
      .configMapper()
      .asRawContextConfig(
        configInterfacePackageName = configInterface.getPackageName(),
        configInterfaceSimpleName = configInterface.getSimpleName(),
      )

  fun readRawContextConfig(): RawContextConfig = rawContextConfig

  fun readRawPackageRuleConfigList(): List<RawPackageRuleConfig> = contextAnnotation.run {
    getArgumentValueAsListOrNull(KursContextDefinition.Property.PACKAGE_RULES, rawType = KSAnnotation::class) {
        getArgumentValueOrNull<KSAnnotation>(KursPackageRuleDefinition.Property.RULE)?.configMapper()?.asRawRuleConfig()?.let {
          RawPackageRuleConfig(
            packageName = getArgumentValueAsTrimmedStringOrNull(KursPackageRuleDefinition.Property.PACKAGE_NAME),
            rule = it,
          )
        }
      }
      ?.filterNotNull() ?: emptyList()
  }

  fun readRawKursTypeRuleConfigList(): List<RawKursTypeRuleConfig> =
    contextFunctions
      .asSequence()
      .filter { it.isAbstract }
      .filter { it.isPublic() }
      .filter { it.extensionReceiver != null }
      .filter { it.returnType?.resolve()?.declaration?.qualifiedName?.asString() == "kotlin.Unit" }
      .filter { it.simpleName.asString() == "rule" }
      .mapNotNull { test ->
        test.extensionReceiver?.resolve()?.let { ksType ->
          test.getAnnotationOrNull(KursRuleDefinition.QUALIFIED_NAME)?.configMapper()?.asRawRuleConfig()?.let {
            RawKursTypeRuleConfig(ksType = ksType, rule = it)
          }
        }
      }
      .toList()

  fun readRawPurposeMappingFunctionList(): List<RawPurposeMappingFunction> =
    contextFunctions
      .asSequence()
      .filter { it.isAbstract }
      .filter { it.isPublic() }
      .filter { it.extensionReceiver != null }
      .filter { it.returnType != null }
      .filter { it.returnType?.resolve()?.declaration?.qualifiedName?.asString() != "kotlin.Unit" }
      .filter { it.simpleName.asString() != "rule" }
      .mapNotNull { functionDeclaration ->
        val sourceKsType = functionDeclaration.extensionReceiver?.resolve() ?: return@mapNotNull null
        val targetKsType = functionDeclaration.returnType?.resolve() ?: return@mapNotNull null
        val argumentList = functionDeclaration.parameters.map { it.kspMapper().asNullableRawArgument() ?: return@mapNotNull null }
        RawPurposeMappingFunction(sourceKsType = sourceKsType, targetKsType = targetKsType, argumentList = argumentList)
      }
      .toList()

  fun readSandboxMappingFunctionList(): List<RawSandboxMappingFunction> {
    return configInterface.declarations
      .filterIsInstance<KSClassDeclaration>()
      .filter { classDeclaration -> classDeclaration.classKind == ClassKind.CLASS }
      .mapNotNull { classDeclaration ->
        rawContextConfig.rootPackageName
          .configMapper()
          .asNullablePackageName()
          ?.let { rootPackageName -> classDeclaration.getPrimaryConstructorDeclaration(rootPackageName)?.parameters?.singleOrNull() }
          ?.let { parameter -> classDeclaration to parameter.type.resolve() }
      }
      .flatMap { (classDeclaration, sourceKsType) ->
        classDeclaration.getAllMemberFunctionDeclarations(kspResolver).mapNotNull { functionDeclaration ->
          val targetKsType = functionDeclaration.returnType?.resolve() ?: return@mapNotNull null
          val argumentList = functionDeclaration.parameters.map { it.kspMapper().asNullableRawArgument() ?: return@mapNotNull null }
          RawSandboxMappingFunction(
            sourceKsType = sourceKsType,
            targetKsType = targetKsType,
            argumentList = argumentList,
            mapperName = classDeclaration.getSimpleName(),
            mappingFunctionName = functionDeclaration.getName(),
          )
        }
      }
      .toList()
  }
}
