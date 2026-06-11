package kr.urbansoft.kursmapper.processor.adapter.infra.config.mapper.annotation

import kr.urbansoft.kursmapper.annotation.MapperSubPackageCreationMode

fun MapperSubPackageCreationMode?.configMapper() = NullableMapperSubPackageCreationModeMapper(this)

@JvmInline
value class NullableMapperSubPackageCreationModeMapper(private val source: MapperSubPackageCreationMode?) {
  fun asMapperSubPackageCreationMode(): MapperSubPackageCreationMode = source ?: MapperSubPackageCreationMode.AUTO
}
