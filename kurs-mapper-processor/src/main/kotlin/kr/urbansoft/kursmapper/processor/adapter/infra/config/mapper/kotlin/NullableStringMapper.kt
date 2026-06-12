package kr.urbansoft.kursmapper.processor.adapter.infra.config.mapper.kotlin

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

fun String?.configMapper() = NullableStringMapper(this)

@JvmInline
value class NullableStringMapper(private val source: String?) {
  fun asFunctionNamePrefix(): FunctionNamePrefix = source?.configMapper()?.asFunctionNamePrefix() ?: FunctionNamePrefix.default()

  fun asFunctionNameSuffix(): FunctionNameSuffix = source?.configMapper()?.asFunctionNameSuffix() ?: FunctionNameSuffix.default()

  fun asMapperNameGlobalSuffix(): MapperNameGlobalSuffix =
    source?.configMapper()?.asMapperNameGlobalSuffix() ?: MapperNameGlobalSuffix.default()

  fun asMapperNamePrefix(): MapperNamePrefix = source?.configMapper()?.asMapperNamePrefix() ?: MapperNamePrefix.default()

  fun asMapperNameSuffix(): MapperNameSuffix = source?.configMapper()?.asMapperNameSuffix() ?: MapperNameSuffix.default()

  fun asMapperSourceVariableName(): MapperSourceVariableName =
    source?.configMapper()?.asMapperSourceVariableName() ?: MapperSourceVariableName.default()

  fun asMappingFunctionNameVerb(): MappingFunctionNameVerb =
    source?.configMapper()?.asMappingFunctionNameVerb() ?: MappingFunctionNameVerb.default()

  fun asNullableContextName(): ContextName? = source?.configMapper()?.asContextName()

  fun asNullablePackageName(): PackageName? = source?.configMapper()?.asPackageName()

  fun asNullableSymbolName(): SymbolName? = source?.configMapper()?.asSymbolName()

  fun asPackageNamePart(): PackageNamePart = source?.configMapper()?.asPackageNamePart() ?: PackageNamePart.empty()
}
