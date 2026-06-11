package kr.urbansoft.kursmapper.processor.domain.model.config

import kr.urbansoft.kursmapper.processor.domain.model.kurstype.KursTypeId

@ConsistentCopyVisibility
data class KursTypeRuleConfig private constructor(val kursTypeId: KursTypeId, val rule: RuleConfig) {
  companion object {
    fun from(kursTypeId: KursTypeId, rule: RuleConfig): KursTypeRuleConfig = KursTypeRuleConfig(kursTypeId = kursTypeId, rule = rule)
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other == null || this::class != other::class) return false
    other as KursTypeRuleConfig
    return kursTypeId == other.kursTypeId
  }

  override fun hashCode(): Int = kursTypeId.hashCode()
}
