package kr.urbansoft.kursmapper.processor.domain.model.config

@JvmInline
value class ConfigInterfaceSimpleName private constructor(val value: String) {
  companion object {
    fun from(value: String): ConfigInterfaceSimpleName = ConfigInterfaceSimpleName(value.trim())
  }
}
