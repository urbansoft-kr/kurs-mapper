package kr.urbansoft.kursmapper.processor.domain.model.function

import kr.urbansoft.kursmapper.processor.domain.model.kurstype.KursTypeId

interface KursTypeInstantiator {
  val kursTypeId: KursTypeId
  val kind: Kind
  val name: FunctionName
  val argumentList: List<Argument>

  enum class Kind {
    PRIMARY_CONSTRUCTOR,
    STATIC_FUNCTION,
  }
}
