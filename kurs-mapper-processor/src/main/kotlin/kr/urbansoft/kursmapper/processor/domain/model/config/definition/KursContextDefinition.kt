package kr.urbansoft.kursmapper.processor.domain.model.config.definition

object KursContextDefinition {
  const val SIMPLE_NAME = "KursContext"
  const val PACKAGE_NAME = "kr.urbansoft.kursmapper.annotation"
  const val QUALIFIED_NAME = "kr.urbansoft.kursmapper.annotation.KursContext"
  const val CANONICAL_NAME = "kr.urbansoft.kursmapper.annotation.KursContext"

  object Property {
    const val CONTEXT_NAME = "contextName"
    const val ROOT_PACKAGE_NAME = "rootPackageName"
    const val MAPPER_NAME_GLOBAL_SUFFIX = "mapperNameGlobalSuffix"
    const val MAPPER_SOURCE_VARIABLE_NAME = "mapperSourceVariableName"
    const val MAPPING_FUNCTION_NAME_VERB = "mappingFunctionNameVerb"
    const val PACKAGE_RULES = "packageRules"
    const val GUIDE_LANGUAGE = "guideLanguage"
  }
}
