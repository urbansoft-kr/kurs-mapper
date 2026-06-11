package kr.urbansoft.kursmapper.processor.domain.model.config

@JvmInline
value class MapperNameGlobalSuffix private constructor(val value: String) {
  companion object {
    fun from(value: String): MapperNameGlobalSuffix = MapperNameGlobalSuffix(value.trim())

    fun default(): MapperNameGlobalSuffix = MapperNameGlobalSuffix("Mapper")
  }
}
