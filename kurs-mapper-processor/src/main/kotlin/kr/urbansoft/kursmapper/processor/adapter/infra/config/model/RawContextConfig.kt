package kr.urbansoft.kursmapper.processor.adapter.infra.config.model

import kr.urbansoft.kursmapper.annotation.GuideLanguage

data class RawContextConfig(
  val configInterfacePackageName: String,
  val configInterfaceSimpleName: String,
  val contextName: String?,
  val rootPackageName: String?,
  val mapperNameGlobalSuffix: String?,
  val mapperSourceVariableName: String?,
  val mappingFunctionNameVerb: String?,
  val guideLanguage: GuideLanguage?,
)
