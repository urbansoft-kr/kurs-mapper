package kr.urbansoft.shared.uuid

import kr.urbansoft.shared.exception.ExceptionMessageSupport
import kr.urbansoft.shared.exception.ExceptionType
import kr.urbansoft.shared.uuid.UUID.Version
import kr.urbansoft.shared.validation.validate

@JvmInline
value class UUIDv7 private constructor(val value: UUID) : Comparable<UUIDv7> {
  init {
    validate(value.version() == Version.V7, { ExceptionMessage.UUID_VERSION_IS_INVALID })
  }

  companion object {
    fun from(value: UUID) = UUIDv7(value)

    fun from(value: String): UUIDv7 = UUIDv7(UUID.from(value))

    fun generate(): UUIDv7 = from(UUID.generate(Version.V7))
  }

  fun asString(): String = value.value

  override fun compareTo(other: UUIDv7): Int = asString().compareTo(other.asString())

  enum class ExceptionMessage(override val value: String, override val type: ExceptionType) : ExceptionMessageSupport {
    UUID_VERSION_IS_INVALID("Only UUIDv7 is allowed.", ExceptionType.BAD_REQUEST)
  }
}
