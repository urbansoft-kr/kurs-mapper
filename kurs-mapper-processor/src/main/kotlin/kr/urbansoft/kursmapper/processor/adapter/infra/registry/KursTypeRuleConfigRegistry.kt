package kr.urbansoft.kursmapper.processor.adapter.infra.registry

import kr.urbansoft.kursmapper.processor.domain.model.config.KursTypeRuleConfig

class KursTypeRuleConfigRegistry private constructor() {
  private var value: List<KursTypeRuleConfig>? = null

  companion object {
    fun create(): KursTypeRuleConfigRegistry = KursTypeRuleConfigRegistry()
  }

  fun load(): List<KursTypeRuleConfig> = loadOrNull() ?: error("KursTypeRuleConfig is not registered.")

  fun loadOrNull(): List<KursTypeRuleConfig>? = value

  fun register(value: List<KursTypeRuleConfig>): KursTypeRuleConfigRegistry = apply { this.value = value.toList() }
}
