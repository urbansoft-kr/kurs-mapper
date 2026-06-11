package kr.urbansoft.kursmapper.processor.domain.model.config.definition

object KursRuleDefinition {
  const val SIMPLE_NAME = "KursRule"
  const val PACKAGE_NAME = "kr.urbansoft.kursmapper.annotation"
  const val QUALIFIED_NAME = "kr.urbansoft.kursmapper.annotation.KursRule"
  const val CANONICAL_NAME = "kr.urbansoft.kursmapper.annotation.KursRule"

  object Property {
    const val MAPPER_SUB_PACKAGE_CREATION_MODE = "mapperSubPackageCreationMode"
    const val MAPPER_SUB_PACKAGE_NAME = "mapperSubPackageName"
    const val MAPPER_NAME_PREFIX = "mapperNamePrefix"
    const val MAPPER_NAME = "mapperName"
    const val MAPPER_NAME_SUFFIX = "mapperNameSuffix"
    const val MAPPING_FUNCTION_NAME_PREFIX = "mappingFunctionNamePrefix"
    const val MAPPING_FUNCTION_NAME = "mappingFunctionName"
    const val MAPPING_FUNCTION_NAME_SUFFIX = "mappingFunctionNameSuffix"
  }

  object MapperSubPackageCreationModeValue {
    const val AUTO = "AUTO"
    const val FLAT = "FLAT"
    const val MANUAL = "MANUAL"
  }
}
