package kr.urbansoft.kursmapper.processor.adapter.infra.config.mapper.model

import kr.urbansoft.kursmapper.processor.adapter.infra.config.mapper.annotation.configMapper
import kr.urbansoft.kursmapper.processor.adapter.infra.config.mapper.kotlin.configMapper
import kr.urbansoft.kursmapper.processor.adapter.infra.config.model.RawContextConfig
import kr.urbansoft.kursmapper.processor.domain.model.config.ContextConfig
import kr.urbansoft.shared.exception.ExceptionMessageSupport
import kr.urbansoft.shared.exception.ExceptionType

fun RawContextConfig.configMapper() = RawContextConfigMapper(this)

@JvmInline
value class RawContextConfigMapper(private val source: RawContextConfig) {
  fun asContextConfig(): ContextConfig =
    ContextConfig.from(
      configInterfacePackageName = source.configInterfacePackageName.configMapper().asPackageName(),
      configInterfaceSimpleName = source.configInterfaceSimpleName.configMapper().asConfigInterfaceSimpleName(),
      contextName = source.contextName.configMapper().asNullableContextName() ?: throw ExceptionMessage.CONTEXT_NAME_IS_REQUIRED.create(),
      rootPackageName =
        source.rootPackageName.configMapper().asNullablePackageName() ?: throw ExceptionMessage.ROOT_PACKAGE_NAME_IS_REQUIRED.create(),
      mapperNameGlobalSuffix = source.mapperNameGlobalSuffix.configMapper().asMapperNameGlobalSuffix(),
      mapperSourceVariableName = source.mapperSourceVariableName.configMapper().asMapperSourceVariableName(),
      mappingFunctionNameVerb = source.mappingFunctionNameVerb.configMapper().asMappingFunctionNameVerb(),
      guideLanguage = source.guideLanguage.configMapper().asGuideLanguage(),
    )

  enum class ExceptionMessage(override val value: String, override val type: ExceptionType) : ExceptionMessageSupport {
    CONTEXT_NAME_IS_REQUIRED("contextName is required.", ExceptionType.BAD_REQUEST),
    ROOT_PACKAGE_NAME_IS_REQUIRED("contextName is required.", ExceptionType.BAD_REQUEST),
  }
}
