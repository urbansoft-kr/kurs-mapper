package kr.urbansoft.kursmapper.processor.domain.model.function

import kr.urbansoft.kursmapper.processor.domain.model.kurstype.KursTypeId
import kr.urbansoft.kursmapper.processor.shared.exception.ExceptionMessageSupport
import kr.urbansoft.kursmapper.processor.shared.exception.ExceptionType
import kr.urbansoft.kursmapper.processor.shared.validation.validate

@JvmInline
value class MappingFunctionId private constructor(val value: String) {
  init {
    validate(value.isNotBlank(), { ExceptionMessage.VALUE_IS_REQUIRED })
  }

  companion object {
    fun from(sourceId: KursTypeId, targetId: KursTypeId): MappingFunctionId = MappingFunctionId(value = "${sourceId.name} -> ${targetId.name}")

    fun from(pair: Pair<KursTypeId, KursTypeId>): MappingFunctionId = from(sourceId = pair.first, targetId = pair.second)
  }

  enum class ExceptionMessage(override val value: String, override val type: ExceptionType) : ExceptionMessageSupport {
    VALUE_IS_REQUIRED("value is required", ExceptionType.BAD_REQUEST)
  }
}
