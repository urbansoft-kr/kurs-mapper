package kr.urbansoft.kursmapper.processor.domain.model.function

import kr.urbansoft.kursmapper.processor.domain.model.config.FunctionNamePrefix
import kr.urbansoft.kursmapper.processor.domain.model.config.FunctionNameSuffix
import kr.urbansoft.kursmapper.processor.domain.model.config.MappingFunctionNameVerb
import kr.urbansoft.kursmapper.processor.domain.model.kurstype.SymbolName
import kr.urbansoft.kursmapper.processor.shared.exception.ExceptionMessageSupport
import kr.urbansoft.kursmapper.processor.shared.exception.ExceptionType
import kr.urbansoft.kursmapper.processor.shared.validation.validate

@JvmInline
value class FunctionName private constructor(val value: String) {
  init {
    validate(value.isNotBlank(), { ExceptionMessage.VALUE_IS_REQUIRED })
  }

  companion object {
    fun from(value: String): FunctionName = FunctionName(value.trim())

    fun from(
      mappingFunctionNameVerb: MappingFunctionNameVerb,
      prefix: FunctionNamePrefix,
      value: SymbolName,
      suffix: FunctionNameSuffix,
    ): FunctionName {
      return FunctionName(value = "${mappingFunctionNameVerb.value}${prefix.value}${value.value}${suffix.value}")
    }
  }

  enum class ExceptionMessage(override val value: String, override val type: ExceptionType) : ExceptionMessageSupport {
    VALUE_IS_REQUIRED("value is required.", ExceptionType.BAD_REQUEST)
  }
}
