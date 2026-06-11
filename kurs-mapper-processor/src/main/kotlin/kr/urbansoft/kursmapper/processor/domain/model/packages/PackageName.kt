package kr.urbansoft.kursmapper.processor.domain.model.packages

import kr.urbansoft.kursmapper.processor.shared.exception.ExceptionMessageSupport
import kr.urbansoft.kursmapper.processor.shared.exception.ExceptionType
import kr.urbansoft.kursmapper.processor.shared.validation.validate

@JvmInline
value class PackageName private constructor(val value: String) {
  init {
    validate(value.isNotBlank(), { ExceptionMessage.VALUE_IS_REQUIRED })
    validate(!value.startsWith("."), { ExceptionMessage.VALUE_MUST_NOT_START_WITH_DOT })
    validate(!value.endsWith("."), { ExceptionMessage.VALUE_MUST_NOT_END_WITH_DOT })
  }

  companion object {
    fun from(value: String): PackageName {
      val trimmed = value.trim()
      var normalized = trimmed
      if (trimmed.startsWith(".")) normalized = trimmed.drop(1)
      if (trimmed.endsWith(".")) normalized = trimmed.dropLast(1)
      return PackageName(value = normalized)
    }
  }

  fun lastPart(): PackageNamePart = PackageNamePart.from(value = value.substringAfterLast("."))

  fun isBlank(): Boolean = value.isBlank()

  operator fun plus(part: PackageNamePart): PackageName = if (part.isBlank()) this else PackageName(value = "$value.${part.value}")

  operator fun plus(packageName: PackageName): PackageName = if (packageName.isBlank()) this else PackageName(value = "$value.${packageName.value}")

  operator fun contains(other: PackageName): Boolean = other.value == value || other.value.startsWith("$value.")

  operator fun contains(other: String): Boolean = from(other) in this

  fun length(): Int = value.length

  enum class ExceptionMessage(override val value: String, override val type: ExceptionType) : ExceptionMessageSupport {
    VALUE_IS_REQUIRED("value is required.", ExceptionType.BAD_REQUEST),
    VALUE_MUST_NOT_END_WITH_DOT("value must not end with dot.", ExceptionType.BAD_REQUEST),
    VALUE_MUST_NOT_START_WITH_DOT("value must not start with dot.", ExceptionType.BAD_REQUEST),
  }
}
