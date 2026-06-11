package kr.urbansoft.kursmapper.processor.adapter.infra.config.mapper.kotlin

import kr.urbansoft.kursmapper.processor.domain.model.config.ConfigInterfaceSimpleName
import kr.urbansoft.kursmapper.processor.domain.model.config.ContextName
import kr.urbansoft.kursmapper.processor.domain.model.config.FunctionNamePrefix
import kr.urbansoft.kursmapper.processor.domain.model.config.FunctionNameSuffix
import kr.urbansoft.kursmapper.processor.domain.model.config.MapperNameGlobalSuffix
import kr.urbansoft.kursmapper.processor.domain.model.config.MapperNamePrefix
import kr.urbansoft.kursmapper.processor.domain.model.config.MapperNameSuffix
import kr.urbansoft.kursmapper.processor.domain.model.config.MapperSourceVariableName
import kr.urbansoft.kursmapper.processor.domain.model.config.MappingFunctionNameVerb
import kr.urbansoft.kursmapper.processor.domain.model.kurstype.SymbolName
import kr.urbansoft.kursmapper.processor.domain.model.packages.PackageName
import kr.urbansoft.kursmapper.processor.domain.model.packages.PackageNamePart

fun String.configMapper() = StringMapper(this)

@JvmInline
value class StringMapper(private val source: String) {
  fun asConfigInterfaceSimpleName(): ConfigInterfaceSimpleName = ConfigInterfaceSimpleName.from(source)

  fun asContextName(): ContextName = ContextName.from(source)

  fun asFunctionNamePrefix(): FunctionNamePrefix = FunctionNamePrefix.from(source)

  fun asFunctionNameSuffix(): FunctionNameSuffix = FunctionNameSuffix.from(source)

  fun asMapperNameGlobalSuffix(): MapperNameGlobalSuffix = MapperNameGlobalSuffix.from(source)

  fun asMapperNamePrefix(): MapperNamePrefix = MapperNamePrefix.from(source)

  fun asMapperNameSuffix(): MapperNameSuffix = MapperNameSuffix.from(source)

  fun asMapperSourceVariableName(): MapperSourceVariableName = MapperSourceVariableName.from(source)

  fun asMappingFunctionNameVerb(): MappingFunctionNameVerb = MappingFunctionNameVerb.from(source)

  fun asPackageName(): PackageName = PackageName.from(source)

  fun asPackageNamePart(): PackageNamePart = PackageNamePart.from(source)

  fun asSymbolName(): SymbolName = SymbolName.from(source)
}
