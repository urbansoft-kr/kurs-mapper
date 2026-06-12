package kr.urbansoft.kursmapper.processor.domain.model.kurstype

import kr.urbansoft.shared.exception.ExceptionMessageSupport
import kr.urbansoft.shared.exception.ExceptionType
import kr.urbansoft.shared.validation.validate

@JvmInline
value class KursTypeQualifiedName private constructor(val value: String) {
  init {
    validate(value.isNotBlank(), { ExceptionMessage.VALUE_IS_REQUIRED })
  }

  companion object {
    fun from(value: String): KursTypeQualifiedName = KursTypeQualifiedName(value.trim())
  }

  enum class ExceptionMessage(override val value: String, override val type: ExceptionType) : ExceptionMessageSupport {
    VALUE_IS_REQUIRED("value is required.", ExceptionType.BAD_REQUEST)
  }
}
