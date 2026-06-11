package kr.urbansoft.kursmapper.processor.adapter.infra.config.model

import kr.urbansoft.kursmapper.annotation.MapperSubPackageCreationMode

data class RawRuleConfig(
  val mapperSubPackageCreationMode: MapperSubPackageCreationMode?,
  val mapperSubPackageName: String?,
  val mapperNamePrefix: String?,
  val mapperName: String?,
  val mapperNameSuffix: String?,
  val mappingFunctionNamePrefix: String?,
  val mappingFunctionName: String?,
  val mappingFunctionNameSuffix: String?,
)
