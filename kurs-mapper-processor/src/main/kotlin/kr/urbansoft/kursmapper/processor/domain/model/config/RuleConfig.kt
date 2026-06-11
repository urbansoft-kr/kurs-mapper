package kr.urbansoft.kursmapper.processor.domain.model.config

import kr.urbansoft.kursmapper.annotation.MapperSubPackageCreationMode
import kr.urbansoft.kursmapper.processor.domain.model.function.FunctionName
import kr.urbansoft.kursmapper.processor.domain.model.kurstype.KursTypeId
import kr.urbansoft.kursmapper.processor.domain.model.kurstype.SymbolName
import kr.urbansoft.kursmapper.processor.domain.model.mapper.MapperName
import kr.urbansoft.kursmapper.processor.domain.model.packages.PackageName
import kr.urbansoft.kursmapper.processor.domain.model.packages.PackageNamePart
import kr.urbansoft.kursmapper.processor.shared.exception.ExceptionMessageSupport
import kr.urbansoft.kursmapper.processor.shared.exception.ExceptionType
import kr.urbansoft.kursmapper.processor.shared.validation.validate

@ConsistentCopyVisibility
data class RuleConfig
private constructor(
  val mapperSubPackageCreationMode: MapperSubPackageCreationMode,
  val mapperSubPackageName: PackageNamePart,
  val mapperNamePrefix: MapperNamePrefix,
  val mapperName: SymbolName?,
  val mapperNameSuffix: MapperNameSuffix,
  val mappingFunctionNamePrefix: FunctionNamePrefix,
  val mappingFunctionName: SymbolName?,
  val mappingFunctionNameSuffix: FunctionNameSuffix,
) {
  init {
    when (mapperSubPackageCreationMode) {
      MapperSubPackageCreationMode.AUTO,
      MapperSubPackageCreationMode.FLAT ->
        validate(mapperSubPackageName.isBlank(), { ExceptionMessage.MAPPER_SUB_PACKAGE_NAME_MUST_BE_BLANK_UNLESS_CREATION_MODE_IS_MANUAL })
      MapperSubPackageCreationMode.MANUAL ->
        validate(mapperSubPackageName.isNotBlank(), { ExceptionMessage.MAPPER_SUB_PACKAGE_NAME_IS_REQUIRED_WHEN_CREATION_MODE_IS_MANUAL })
    }
  }

  companion object {
    fun from(
      mapperSubPackageCreationMode: MapperSubPackageCreationMode,
      mapperSubPackageName: PackageNamePart,
      mapperNamePrefix: MapperNamePrefix,
      mapperName: SymbolName?,
      mapperNameSuffix: MapperNameSuffix,
      mappingFunctionNamePrefix: FunctionNamePrefix,
      mappingFunctionName: SymbolName?,
      mappingFunctionNameSuffix: FunctionNameSuffix,
    ) =
      RuleConfig(
        mapperSubPackageCreationMode = mapperSubPackageCreationMode,
        mapperSubPackageName = mapperSubPackageName,
        mapperNamePrefix = mapperNamePrefix,
        mapperName = mapperName,
        mapperNameSuffix = mapperNameSuffix,
        mappingFunctionNamePrefix = mappingFunctionNamePrefix,
        mappingFunctionName = mappingFunctionName,
        mappingFunctionNameSuffix = mappingFunctionNameSuffix,
      )

    fun default() =
      RuleConfig(
        mapperSubPackageCreationMode = MapperSubPackageCreationMode.AUTO,
        mapperSubPackageName = PackageNamePart.empty(),
        mapperNamePrefix = MapperNamePrefix.default(),
        mapperName = null,
        mapperNameSuffix = MapperNameSuffix.default(),
        mappingFunctionNamePrefix = FunctionNamePrefix.default(),
        mappingFunctionName = null,
        mappingFunctionNameSuffix = FunctionNameSuffix.default(),
      )
  }

  fun mapperPackageName(kursTypeId: KursTypeId, contextConfig: ContextConfig): PackageName =
    contextConfig.configInterfacePackageName +
      when (mapperSubPackageCreationMode) {
        MapperSubPackageCreationMode.AUTO -> kursTypeId.packageName().lastPart()
        MapperSubPackageCreationMode.FLAT -> PackageNamePart.empty()
        MapperSubPackageCreationMode.MANUAL -> mapperSubPackageName
      }

  fun mapperName(kursTypeId: KursTypeId, contextConfig: ContextConfig): MapperName =
    MapperName.from(
      prefix = mapperNamePrefix,
      value = this.mapperName ?: kursTypeId.symbolName(),
      suffix = mapperNameSuffix,
      globalSuffix = contextConfig.mapperNameGlobalSuffix,
    )

  fun mappingFunctionName(kursTypeId: KursTypeId, contextConfig: ContextConfig): FunctionName =
    FunctionName.from(
      mappingFunctionNameVerb = contextConfig.mappingFunctionNameVerb,
      prefix = mappingFunctionNamePrefix,
      value = mappingFunctionName ?: kursTypeId.symbolName(),
      suffix = mappingFunctionNameSuffix,
    )

  enum class ExceptionMessage(override val value: String, override val type: ExceptionType) : ExceptionMessageSupport {
    MAPPER_SUB_PACKAGE_NAME_IS_REQUIRED_WHEN_CREATION_MODE_IS_MANUAL(
      "mapperSubPackageName is required when mapperSubPackageCreationMode is MANUAL.",
      ExceptionType.BAD_REQUEST,
    ),
    MAPPER_SUB_PACKAGE_NAME_MUST_BE_BLANK_UNLESS_CREATION_MODE_IS_MANUAL(
      "mapperSubPackageName must be null unless mapperSubPackageCreationMode is MANUAL.",
      ExceptionType.BAD_REQUEST,
    ),
  }
}
