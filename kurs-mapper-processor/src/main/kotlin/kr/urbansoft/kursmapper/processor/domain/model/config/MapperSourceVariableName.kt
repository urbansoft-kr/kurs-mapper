package kr.urbansoft.kursmapper.processor.domain.model.config

import kr.urbansoft.kursmapper.processor.shared.exception.ExceptionMessageSupport
import kr.urbansoft.kursmapper.processor.shared.exception.ExceptionType
import kr.urbansoft.kursmapper.processor.shared.validation.validate

@JvmInline
value class MapperSourceVariableName private constructor(val value: String) {
  init {
    validate(value.isNotBlank(), { ExceptionMessage.VALUE_IS_REQUIRED })
  }

  companion object {
    fun from(value: String): MapperSourceVariableName = MapperSourceVariableName(value.trim())

    fun default(): MapperSourceVariableName = MapperSourceVariableName("source")
  }

  enum class ExceptionMessage(override val value: String, override val type: ExceptionType) : ExceptionMessageSupport {
    VALUE_IS_REQUIRED("value is required.", ExceptionType.BAD_REQUEST)
  }
}
