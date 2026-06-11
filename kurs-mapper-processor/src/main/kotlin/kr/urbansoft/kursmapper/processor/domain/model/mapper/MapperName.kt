package kr.urbansoft.kursmapper.processor.domain.model.mapper

import kr.urbansoft.kursmapper.processor.domain.model.config.MapperNameGlobalSuffix
import kr.urbansoft.kursmapper.processor.domain.model.config.MapperNamePrefix
import kr.urbansoft.kursmapper.processor.domain.model.config.MapperNameSuffix
import kr.urbansoft.kursmapper.processor.domain.model.kurstype.SymbolName
import kr.urbansoft.kursmapper.processor.shared.exception.ExceptionMessageSupport
import kr.urbansoft.kursmapper.processor.shared.exception.ExceptionType
import kr.urbansoft.kursmapper.processor.shared.validation.validate

@JvmInline
value class MapperName private constructor(val value: String) {
  init {
    validate(value.isNotBlank(), { ExceptionMessage.VALUE_IS_REQUIRED })
  }

  companion object {
    fun from(value: String): MapperName = MapperName(value.trim())

    fun from(
      prefix: MapperNamePrefix,
      value: SymbolName,
      suffix: MapperNameSuffix,
      globalSuffix: MapperNameGlobalSuffix,
    ): MapperName {
      return MapperName(value = "${prefix.value}${value.value}${suffix.value}${globalSuffix.value}")
    }
  }

  enum class ExceptionMessage(override val value: String, override val type: ExceptionType) : ExceptionMessageSupport {
    VALUE_IS_REQUIRED("value is required.", ExceptionType.BAD_REQUEST)
  }
}
