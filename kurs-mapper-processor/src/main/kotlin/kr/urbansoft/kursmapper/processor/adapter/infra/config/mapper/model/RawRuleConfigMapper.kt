package kr.urbansoft.kursmapper.processor.adapter.infra.config.mapper.model

import kr.urbansoft.kursmapper.processor.adapter.infra.config.mapper.annotation.configMapper
import kr.urbansoft.kursmapper.processor.adapter.infra.config.mapper.kotlin.configMapper
import kr.urbansoft.kursmapper.processor.adapter.infra.config.model.RawRuleConfig
import kr.urbansoft.kursmapper.processor.domain.model.config.RuleConfig

fun RawRuleConfig.configMapper() = RawRuleConfigMapper(this)

class RawRuleConfigMapper(private val source: RawRuleConfig) {
  fun asRuleConfig(): RuleConfig =
    RuleConfig.from(
      mapperSubPackageCreationMode = source.mapperSubPackageCreationMode.configMapper().asMapperSubPackageCreationMode(),
      mapperSubPackageName = source.mapperSubPackageName.configMapper().asPackageNamePart(),
      mapperNamePrefix = source.mapperNamePrefix.configMapper().asMapperNamePrefix(),
      mapperName = source.mapperName.configMapper().asNullableSymbolName(),
      mapperNameSuffix = source.mapperNameSuffix.configMapper().asMapperNameSuffix(),
      mappingFunctionNamePrefix = source.mappingFunctionNamePrefix.configMapper().asFunctionNamePrefix(),
      mappingFunctionName = source.mappingFunctionName.configMapper().asNullableSymbolName(),
      mappingFunctionNameSuffix = source.mappingFunctionNameSuffix.configMapper().asFunctionNameSuffix(),
    )
}
