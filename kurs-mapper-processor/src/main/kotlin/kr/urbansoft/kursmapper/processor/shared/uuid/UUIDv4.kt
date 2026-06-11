package kr.urbansoft.kursmapper.processor.shared.uuid

import kr.urbansoft.kursmapper.processor.shared.exception.ExceptionMessageSupport
import kr.urbansoft.kursmapper.processor.shared.exception.ExceptionType
import kr.urbansoft.kursmapper.processor.shared.validation.validate

@JvmInline
value class UUIDv4 private constructor(val value: UUID) {
  init {
    validate(value.version() == UUID.Version.V4, { ExceptionMessage.UUID_VERSION_IS_INVALID })
  }

  companion object {
    fun from(value: UUID) = UUIDv4(value)

    fun from(value: String): UUIDv4 = UUIDv4(UUID.from(value))

    fun generate(): UUIDv4 = UUID.generate(UUID.Version.V4).toUUIDv4()
  }

  fun asString(): String = value.asString()

  enum class ExceptionMessage(override val value: String, override val type: ExceptionType) : ExceptionMessageSupport {
    UUID_VERSION_IS_INVALID("Only UUIDv4 is allowed.", ExceptionType.BAD_REQUEST)
  }
}
