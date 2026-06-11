package kr.urbansoft.kursmapper.processor.domain.model.packages

import kr.urbansoft.kursmapper.processor.shared.exception.ExceptionMessageSupport
import kr.urbansoft.kursmapper.processor.shared.exception.ExceptionType
import kr.urbansoft.kursmapper.processor.shared.validation.validate

@JvmInline
value class PackageNamePart private constructor(val value: String) {
  init {
    if (value.isNotBlank()) {
      validate(!value.startsWith("."), { ExceptionMessage.VALUE_MUST_NOT_START_WITH_DOT })
      validate(!value.endsWith("."), { ExceptionMessage.VALUE_MUST_NOT_END_WITH_DOT })
    }
  }

  companion object {
    fun from(value: String): PackageNamePart = PackageNamePart(value.trim())

    fun empty(): PackageNamePart = PackageNamePart("")
  }

  fun isBlank(): Boolean = value.isBlank()

  fun isNotBlank(): Boolean = value.isNotBlank()

  enum class ExceptionMessage(override val value: String, override val type: ExceptionType) : ExceptionMessageSupport {
    VALUE_MUST_NOT_END_WITH_DOT("value must not end with dot.", ExceptionType.BAD_REQUEST),
    VALUE_MUST_NOT_START_WITH_DOT("value must not start with dot.", ExceptionType.BAD_REQUEST),
  }
}
