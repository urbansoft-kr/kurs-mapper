package kr.urbansoft.shared.uuid

import kr.urbansoft.shared.exception.ExceptionMessageSupport
import kr.urbansoft.shared.exception.ExceptionType
import kr.urbansoft.shared.uuid.UUID.Version
import kr.urbansoft.shared.validation.validate

@JvmInline
value class UUIDv4 private constructor(val value: UUID) {
  init {
    validate(value.version() == Version.V4, { ExceptionMessage.UUID_VERSION_IS_INVALID })
  }

  companion object {
    fun from(value: UUID) = UUIDv4(value)

    fun from(value: String): UUIDv4 = UUIDv4(UUID.from(value))

    fun generate(): UUIDv4 = from(UUID.generate(Version.V4))
  }

  fun asString(): String = value.value

  enum class ExceptionMessage(override val value: String, override val type: ExceptionType) : ExceptionMessageSupport {
    UUID_VERSION_IS_INVALID("Only UUIDv4 is allowed.", ExceptionType.BAD_REQUEST)
  }
}
