@file:OptIn(ExperimentalUuidApi::class)

package kr.urbansoft.kursmapper.processor.shared.uuid

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import kr.urbansoft.kursmapper.processor.shared.exception.ExceptionMessageSupport
import kr.urbansoft.kursmapper.processor.shared.exception.ExceptionType
import kr.urbansoft.kursmapper.processor.shared.validation.validate

@JvmInline
value class UUID private constructor(val value: String) {
  companion object {
    fun from(value: String): UUID {
      Parser.validate(value)
      return UUID(value)
    }

    fun generate(version: Version): UUID =
      when (version) {
        Version.V4 -> UUID(Uuid.generateV4().toHexDashString())
        Version.V7 -> UUID(Uuid.generateV7().toHexDashString())
      }
  }

  fun version(): Version = Parser.version(value)

  fun toUUIDv4(): UUIDv4 = UUIDv4.from(this)

  fun toUUIDv7(): UUIDv7 = UUIDv7.from(this)

  fun asString(): String = value

  enum class Version(val version: Int) {
    V4(4),
    V7(7);

    companion object {
      val intSet = entries.map { it.version }.toSet()
    }
  }

  private object Parser {
    fun validate(value: String) {
      validate(value.length == 36, { ExceptionMessage.UUID_LENGTH_IS_INVALID })
      val uuid = Uuid.parseHexDashOrNull(value) ?: throw ExceptionMessage.UUID_FORMAT_IS_INVALID.create()
      val bytes = uuid.toByteArray()

      if (version(bytes) !in Version.intSet) throw ExceptionMessage.UUID_VERSION_IS_INVALID.create()
      validate(variant(bytes) == 2, { ExceptionMessage.UUID_VARIANT_IS_INVALID })
    }

    fun version(value: String): Version {
      // The character at index 14 of the string indicates the UUID version, as defined in RFC-9562.
      return when (value[14]) {
        '4' -> Version.V4
        '7' -> Version.V7
        else -> throw ExceptionMessage.UUID_VERSION_IS_INVALID.create()
      }
    }

    private fun version(bytes: ByteArray): Int {
      // bytes[6] contains the 4-bit version (upper) and 4 bits of other data (lower).
      //     xxxx **** : bytes[6] ('*' means irrelevant bits)
      //  >>         4 : shr 4 : Shifts right by 4 bits to move the version to the lower position.
      //     **** xxxx : result of shr 4
      // and 0000 1111 : and 0x0F : Masks out any sign-extension (garbage) bits created after shr 4.
      //     0000 xxxx : result of and 0x0F
      return (bytes[6].toInt() shr 4) and 0x0F
    }

    private fun variant(bytes: ByteArray): Int {
      // bytes[8] contains the variable number of bits(1-3 bits) variant (upper) and rest bits of other data (lower).

      //     xxx* **** : bytes[8] ('*' means irrelevant bits)
      // and 1111 1111 : and 0xFF : Masks out any sign-extension (garbage) bits created after toInt()
      //     xxx* **** : result of and 0xFF
      val b = bytes[8].toInt() and 0xFF

      //     x*** **** : result of and 0xFF
      // and 1000 0000 : and 0x80
      //     x000 0000 : result of and 0x80.
      //     0000 0000 : 0x00 to compare
      // if b and 0x80 == 0x00(the top 1 bit is 0), then Reserved. Network Computing System (NCS) backward compatibility, and includes Nil UUID
      return if (b and 0x80 == 0x00) 0 // 000 = 0

      //     xx** **** : result of and 0xFF
      // and 1100 0000 : and 0xC0
      //     xx00 0000 : result of and 0xC0
      //     1000 0000 : 0x80 to compare
      // if b and 0xC0 == 0x80(the top 2 bits are 10), then The variant specified in RFC-9562
      else if (b and 0xC0 == 0x80) 2 // 10 = 2

      //     xxx* **** : result of and 0xFF
      // and 1110 0000 : and 0xE0
      //     xxx0 0000 : result of and 0xE0
      //     1100 0000 : 0xC0 to compare
      // if b and 0xE0 == 0xC0(the top 3 bits are 110), then Reserved. Microsoft Corporation backward compatibility.
      else if (b and 0xE0 == 0xC0) 6 // 110 = 6

      // otherwise, remaining case is 111. Reserved for future definition and includes Max UUID
      else 7 // 111 = 7
    }
  }

  enum class ExceptionMessage(override val value: String, override val type: ExceptionType) : ExceptionMessageSupport {
    UUID_FORMAT_IS_INVALID("UUID format is invalid.", ExceptionType.BAD_REQUEST),
    UUID_LENGTH_IS_INVALID("UUID length is invalid.", ExceptionType.BAD_REQUEST),
    UUID_VARIANT_IS_INVALID("UUID variant is invalid.", ExceptionType.BAD_REQUEST),
    UUID_VERSION_IS_INVALID("Only UUIDv4 and UUIDv7 are allowed.", ExceptionType.BAD_REQUEST),
  }
}
