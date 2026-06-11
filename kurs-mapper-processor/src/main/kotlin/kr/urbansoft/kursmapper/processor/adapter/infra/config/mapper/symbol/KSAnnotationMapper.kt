package kr.urbansoft.kursmapper.processor.adapter.infra.config.mapper.symbol

import com.google.devtools.ksp.symbol.KSAnnotation
import kr.urbansoft.kursmapper.processor.adapter.infra.config.model.RawContextConfig
import kr.urbansoft.kursmapper.processor.adapter.infra.config.model.RawRuleConfig
import kr.urbansoft.kursmapper.processor.adapter.infra.ksp.extension.getArgumentValueAsEnumOrNull
import kr.urbansoft.kursmapper.processor.adapter.infra.ksp.extension.getArgumentValueAsTrimmedStringOrNull
import kr.urbansoft.kursmapper.processor.domain.model.config.definition.KursContextDefinition
import kr.urbansoft.kursmapper.processor.domain.model.config.definition.KursRuleDefinition

fun KSAnnotation.configMapper() = KSAnnotationMapper(this)

@JvmInline
value class KSAnnotationMapper(private val source: KSAnnotation) {
  fun asRawContextConfig(configInterfacePackageName: String, configInterfaceSimpleName: String): RawContextConfig =
    RawContextConfig(
      configInterfacePackageName = configInterfacePackageName,
      configInterfaceSimpleName = configInterfaceSimpleName,
      contextName = source.getArgumentValueAsTrimmedStringOrNull(KursContextDefinition.Property.CONTEXT_NAME),
      rootPackageName = source.getArgumentValueAsTrimmedStringOrNull(KursContextDefinition.Property.ROOT_PACKAGE_NAME),
      mapperNameGlobalSuffix = source.getArgumentValueAsTrimmedStringOrNull(KursContextDefinition.Property.MAPPER_NAME_GLOBAL_SUFFIX),
      mapperSourceVariableName = source.getArgumentValueAsTrimmedStringOrNull(KursContextDefinition.Property.MAPPER_SOURCE_VARIABLE_NAME),
      mappingFunctionNameVerb = source.getArgumentValueAsTrimmedStringOrNull(KursContextDefinition.Property.MAPPING_FUNCTION_NAME_VERB),
      guideLanguage = source.getArgumentValueAsEnumOrNull(KursContextDefinition.Property.GUIDE_LANGUAGE),
    )

  fun asRawRuleConfig(): RawRuleConfig =
    RawRuleConfig(
      mapperSubPackageCreationMode = source.getArgumentValueAsEnumOrNull(KursRuleDefinition.Property.MAPPER_SUB_PACKAGE_CREATION_MODE),
      mapperSubPackageName = source.getArgumentValueAsTrimmedStringOrNull(KursRuleDefinition.Property.MAPPER_SUB_PACKAGE_NAME),
      mapperNamePrefix = source.getArgumentValueAsTrimmedStringOrNull(KursRuleDefinition.Property.MAPPER_NAME_PREFIX),
      mapperName = source.getArgumentValueAsTrimmedStringOrNull(KursRuleDefinition.Property.MAPPER_NAME),
      mapperNameSuffix = source.getArgumentValueAsTrimmedStringOrNull(KursRuleDefinition.Property.MAPPER_NAME_SUFFIX),
      mappingFunctionNamePrefix = source.getArgumentValueAsTrimmedStringOrNull(KursRuleDefinition.Property.MAPPING_FUNCTION_NAME_PREFIX),
      mappingFunctionName = source.getArgumentValueAsTrimmedStringOrNull(KursRuleDefinition.Property.MAPPING_FUNCTION_NAME),
      mappingFunctionNameSuffix = source.getArgumentValueAsTrimmedStringOrNull(KursRuleDefinition.Property.MAPPING_FUNCTION_NAME_SUFFIX),
    )
}
