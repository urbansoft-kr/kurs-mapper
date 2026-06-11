package kr.urbansoft.kursmapper.processor.domain.model.config

@JvmInline
value class FunctionNameSuffix private constructor(val value: String) {
  companion object {
    fun from(value: String): FunctionNameSuffix = FunctionNameSuffix(value.trim())

    fun default(): FunctionNameSuffix = FunctionNameSuffix("")
  }
}
