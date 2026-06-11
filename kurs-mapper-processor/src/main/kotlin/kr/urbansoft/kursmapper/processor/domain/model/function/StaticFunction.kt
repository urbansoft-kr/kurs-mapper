package kr.urbansoft.kursmapper.processor.domain.model.function

import kr.urbansoft.kursmapper.processor.domain.model.kurstype.KursTypeId

@ConsistentCopyVisibility
data class StaticFunction
private constructor(
  override val kursTypeId: KursTypeId,
  override val name: FunctionName,
  override val argumentList: List<Argument>,
) : KursTypeInstantiator {
  override val kind: KursTypeInstantiator.Kind = KursTypeInstantiator.Kind.STATIC_FUNCTION

  companion object {
    fun from(kursTypeId: KursTypeId, name: FunctionName, argumentList: List<Argument>): StaticFunction =
      StaticFunction(kursTypeId = kursTypeId, name = name, argumentList = argumentList.toList())
  }
}
