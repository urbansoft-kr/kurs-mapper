package kr.urbansoft.kursmapper.processor.adapter.infra.config.mapper.model

import kr.urbansoft.kursmapper.processor.adapter.infra.config.model.RawRuleConfig
import kr.urbansoft.kursmapper.processor.domain.model.config.RuleConfig

fun RawRuleConfig?.configMapper() = NullableRawRuleConfigMapper(this)

@JvmInline
value class NullableRawRuleConfigMapper(private val source: RawRuleConfig?) {
  fun asNullableRuleConfig(): RuleConfig? = source?.configMapper()?.asRuleConfig()
}
