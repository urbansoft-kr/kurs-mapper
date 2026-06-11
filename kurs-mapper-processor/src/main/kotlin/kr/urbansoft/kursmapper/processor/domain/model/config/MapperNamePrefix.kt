package kr.urbansoft.kursmapper.processor.domain.model.config

@JvmInline
value class MapperNamePrefix private constructor(val value: String) {
  companion object {
    fun from(value: String): MapperNamePrefix = MapperNamePrefix(value.trim())

    fun default(): MapperNamePrefix = MapperNamePrefix("")
  }
}
