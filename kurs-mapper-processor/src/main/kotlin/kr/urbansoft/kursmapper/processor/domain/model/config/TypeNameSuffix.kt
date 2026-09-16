package kr.urbansoft.kursmapper.processor.domain.model.config

@JvmInline
value class TypeNameSuffix private constructor(val value: String) {
  companion object {
    fun from(value: String): TypeNameSuffix = TypeNameSuffix(value.trim())

    fun default(): TypeNameSuffix = TypeNameSuffix("")
  }
}
