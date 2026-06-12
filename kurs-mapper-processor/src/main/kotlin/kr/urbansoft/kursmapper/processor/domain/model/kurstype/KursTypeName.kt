package kr.urbansoft.kursmapper.processor.domain.model.kurstype

import kr.urbansoft.shared.exception.ExceptionMessageSupport
import kr.urbansoft.shared.exception.ExceptionType
import kr.urbansoft.shared.validation.validate

@JvmInline
value class KursTypeName private constructor(val value: String) {
  init {
    validate(value.isNotBlank(), { ExceptionMessage.VALUE_IS_REQUIRED })
  }

  companion object {
    fun from(value: String): KursTypeName {
      val trimmed = value.trim()
      val nullability = if (trimmed.endsWith("?") || trimmed.endsWith("!")) KursType.Nullability.NULLABLE else KursType.Nullability.NOT_NULL
      return from(value, nullability)
    }

    fun from(value: String, nullability: KursType.Nullability): KursTypeName =
      KursTypeName(value.trim().asNotNull() + if (nullability == KursType.Nullability.NULLABLE) "?" else "")

    private fun String.asNotNull(): String =
      when {
        endsWith("?") -> dropLast(1)
        endsWith("!") -> dropLast(1)
        else -> this
      }
  }

  fun asNotNull(): KursTypeName = from(value.asNotNull())

  fun containsContravariant(): Boolean = value.startsWith("in ")

  fun containsCovariant(): Boolean = value.startsWith("out ")

  fun containsGeneric(): Boolean = "<" in value

  fun containsNoGeneric(): Boolean = !containsGeneric()

  fun genericList(): List<KursTypeName> {
    if (containsNoGeneric()) return emptyList()

    val inner = value.substringAfter("<").substringBeforeLast(">")
    val result = mutableListOf<KursTypeName>()

    val current = StringBuilder()
    var depth = 0
    for (char in inner) {
      when (char) {
        '<' -> {
          depth++
          current.append(char)
        }
        '>' -> {
          depth--
          current.append(char)
        }
        ',' -> {
          if (depth == 0) {
            result.add(from(current.toString()))
            current.clear()
          } else current.append(char)
        }
        else -> current.append(char)
      }
    }
    if (current.isNotEmpty()) result.add(from(current.toString()))
    return result
  }

  fun isKotlinNothing(): Boolean = value.startsWith("kotlin.Nothing")

  fun isKotlinUnit(): Boolean = value.startsWith("kotlin.Unit")

  fun isNotKotlinNothing(): Boolean = !isKotlinNothing()

  fun isNotKotlinUnit(): Boolean = !isKotlinUnit()

  fun isNotNull(): Boolean = !isNullable()

  fun isNullable(): Boolean = value.endsWith("?")

  fun isStar(): Boolean = value == "*"

  fun removeGeneric(): KursTypeName = if (containsGeneric()) from(value.substringBefore("<") + value.substringAfterLast(">")) else this

  enum class ExceptionMessage(override val value: String, override val type: ExceptionType) : ExceptionMessageSupport {
    VALUE_IS_REQUIRED("value is required", ExceptionType.BAD_REQUEST)
  }
}
