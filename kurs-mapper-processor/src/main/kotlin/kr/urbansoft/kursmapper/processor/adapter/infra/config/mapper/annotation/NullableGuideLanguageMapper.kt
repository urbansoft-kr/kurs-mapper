package kr.urbansoft.kursmapper.processor.adapter.infra.config.mapper.annotation

import kr.urbansoft.kursmapper.annotation.GuideLanguage

fun GuideLanguage?.configMapper() = NullableGuideLanguageMapper(this)

@JvmInline
value class NullableGuideLanguageMapper(private val source: GuideLanguage?) {
  fun asGuideLanguage(): GuideLanguage = source ?: GuideLanguage.EN_US
}
