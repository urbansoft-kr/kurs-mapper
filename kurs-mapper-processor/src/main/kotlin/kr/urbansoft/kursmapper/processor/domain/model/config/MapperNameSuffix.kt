package kr.urbansoft.kursmapper.processor.domain.model.config

@JvmInline
value class MapperNameSuffix private constructor(val value: String) {
  companion object {
    fun from(value: String): MapperNameSuffix = MapperNameSuffix(value.trim())

    fun default(): MapperNameSuffix = MapperNameSuffix("")
  }
}
