package kr.urbansoft.kursmapper.processor.domain.model.config

import kr.urbansoft.kursmapper.annotation.GuideLanguage
import kr.urbansoft.kursmapper.processor.domain.model.packages.PackageName

@ConsistentCopyVisibility
data class ContextConfig
private constructor(
  val configInterfacePackageName: PackageName,
  val configInterfaceSimpleName: ConfigInterfaceSimpleName,
  val contextName: ContextName,
  val rootPackageName: PackageName,
  val mapperNameGlobalSuffix: MapperNameGlobalSuffix,
  val mapperSourceVariableName: MapperSourceVariableName,
  val mappingFunctionNameVerb: MappingFunctionNameVerb,
  val guideLanguage: GuideLanguage,
) {
  companion object {
    fun from(
      configInterfacePackageName: PackageName,
      configInterfaceSimpleName: ConfigInterfaceSimpleName,
      contextName: ContextName,
      rootPackageName: PackageName,
      mapperNameGlobalSuffix: MapperNameGlobalSuffix,
      mapperSourceVariableName: MapperSourceVariableName,
      mappingFunctionNameVerb: MappingFunctionNameVerb,
      guideLanguage: GuideLanguage,
    ): ContextConfig =
      ContextConfig(
        configInterfacePackageName = configInterfacePackageName,
        configInterfaceSimpleName = configInterfaceSimpleName,
        contextName = contextName,
        rootPackageName = rootPackageName,
        mapperNameGlobalSuffix = mapperNameGlobalSuffix,
        mapperSourceVariableName = mapperSourceVariableName,
        mappingFunctionNameVerb = mappingFunctionNameVerb,
        guideLanguage = guideLanguage,
      )
  }
}
