package kr.urbansoft.kursmapper.processor.domain.model.config

@JvmInline
value class TypeNamePrefix private constructor(val value: String) {
  companion object {
    fun from(value: String): TypeNamePrefix = TypeNamePrefix(value.trim())

    fun default(): TypeNamePrefix = TypeNamePrefix("")
  }
}
