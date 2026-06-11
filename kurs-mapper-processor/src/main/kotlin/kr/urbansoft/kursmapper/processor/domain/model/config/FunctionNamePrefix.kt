package kr.urbansoft.kursmapper.processor.domain.model.config

@JvmInline
value class FunctionNamePrefix private constructor(val value: String) {
  companion object {
    fun from(value: String): FunctionNamePrefix = FunctionNamePrefix(value.trim())

    fun default(): FunctionNamePrefix = FunctionNamePrefix("")
  }
}
