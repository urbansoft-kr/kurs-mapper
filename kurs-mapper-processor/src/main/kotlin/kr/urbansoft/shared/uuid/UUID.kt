@file:OptIn(ExperimentalUuidApi::class)

package kr.urbansoft.shared.uuid

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import kr.urbansoft.shared.exception.ExceptionMessageSupport
import kr.urbansoft.shared.exception.ExceptionType
import kr.urbansoft.shared.validation.validate

@JvmInline
value class UUID private constructor(val value: String) {
  companion object {
    fun from(value: String): UUID = UUID(Parser.validateAndNormalize(value))

    fun generate(version: Version): UUID =
      when (version) {
        Version.V4 -> UUID(Uuid.generateV4().toHexDashString())
        Version.V7 -> UUID(Uuid.generateV7().toHexDashString())
      }
  }

  fun version(): Version = Parser.version(value)

  enum class Version(val value: Int) {
    V4(4),
    V7(7);

    companion object {
      private val valueSet = entries.map { it.value }.toSet()

      fun isAllowed(value: Int): Boolean = value in valueSet
    }
  }

  private object Parser {
    fun validateAndNormalize(value: String): String {
      validate(value.length == 36, { ExceptionMessage.VALUE_LENGTH_IS_INVALID })
      val uuid = Uuid.parseHexDashOrNull(value) ?: throw ExceptionMessage.VALUE_FORMAT_IS_INVALID.create()
      val bytes = uuid.toByteArray()

      validate(Version.isAllowed(version(bytes)), { ExceptionMessage.VALUE_VERSION_IS_INVALID })
      validate(variant(bytes) == 2, { ExceptionMessage.VALUE_VARIANT_IS_INVALID })
      return uuid.toHexDashString()
    }

    fun version(value: String): Version {
      // The character at index 14 of the string indicates the UUID version, as defined in RFC-9562.
      return when (value[14]) {
        '4' -> Version.V4
        '7' -> Version.V7
        else -> throw ExceptionMessage.VALUE_VERSION_IS_INVALID.create()
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
      // if b and 0x80 == 0x00(the top 1 bit is 0), then Reserved. Network Computing System (NCS) backward compatibility, and includes Nil
      // UUID
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
    VALUE_FORMAT_IS_INVALID("value format is invalid.", ExceptionType.BAD_REQUEST),
    VALUE_LENGTH_IS_INVALID("value length is invalid.", ExceptionType.BAD_REQUEST),
    VALUE_VARIANT_IS_INVALID("value variant is invalid.", ExceptionType.BAD_REQUEST),
    VALUE_VERSION_IS_INVALID("value version is invalid: Only 4 or 7 are allowed.", ExceptionType.BAD_REQUEST),
  }
}
