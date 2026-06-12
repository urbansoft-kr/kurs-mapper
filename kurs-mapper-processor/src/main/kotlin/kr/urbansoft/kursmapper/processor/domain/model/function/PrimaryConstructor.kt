package kr.urbansoft.kursmapper.processor.domain.model.function

import kr.urbansoft.kursmapper.processor.domain.model.kurstype.KursTypeId
import kr.urbansoft.shared.exception.ExceptionMessageSupport
import kr.urbansoft.shared.exception.ExceptionType

@ConsistentCopyVisibility
data class PrimaryConstructor private constructor(override val kursTypeId: KursTypeId, override val argumentList: List<Argument>) :
  KursTypeInstantiator {
  override val kind: KursTypeInstantiator.Kind = KursTypeInstantiator.Kind.PRIMARY_CONSTRUCTOR
  override val name: FunctionName
    get() = throw ExceptionMessage.PRIMARY_CONSTRUCTOR_HAS_NO_NAME.create()

  companion object {
    fun from(kursTypeId: KursTypeId, argumentList: List<Argument>): PrimaryConstructor {
      return PrimaryConstructor(kursTypeId = kursTypeId, argumentList = argumentList.toList())
    }
  }

  enum class ExceptionMessage(override val value: String, override val type: ExceptionType) : ExceptionMessageSupport {
    PRIMARY_CONSTRUCTOR_HAS_NO_NAME("Primary constructor has no name.", ExceptionType.NOT_FOUND)
  }
}
