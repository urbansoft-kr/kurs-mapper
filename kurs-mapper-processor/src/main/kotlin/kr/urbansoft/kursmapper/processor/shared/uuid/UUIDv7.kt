package kr.urbansoft.kursmapper.processor.shared.uuid

import kr.urbansoft.kursmapper.processor.shared.exception.ExceptionMessageSupport
import kr.urbansoft.kursmapper.processor.shared.exception.ExceptionType
import kr.urbansoft.kursmapper.processor.shared.validation.validate

@JvmInline
value class UUIDv7 private constructor(val value: UUID) : Comparable<UUIDv7> {
  init {
    validate(value.version() == UUID.Version.V7, { ExceptionMessage.UUID_VERSION_IS_INVALID })
  }

  companion object {
    fun from(value: UUID) = UUIDv7(value)

    fun from(value: String): UUIDv7 = UUIDv7(UUID.from(value))

    fun generate(): UUIDv7 = UUID.generate(UUID.Version.V7).toUUIDv7()
  }

  fun asString(): String = value.asString()

  override fun compareTo(other: UUIDv7): Int = value.value.compareTo(other.value.value)

  enum class ExceptionMessage(override val value: String, override val type: ExceptionType) : ExceptionMessageSupport {
    UUID_VERSION_IS_INVALID("Only UUIDv7 is allowed.", ExceptionType.BAD_REQUEST)
  }
}
