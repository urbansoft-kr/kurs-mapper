package kr.urbansoft.kursmapper.processor.adapter.infra.registry

import kr.urbansoft.kursmapper.processor.domain.model.config.PackageRuleConfig

class PackageRuleConfigRegistry private constructor() {
  private var value: List<PackageRuleConfig>? = null

  companion object {
    fun create(): PackageRuleConfigRegistry = PackageRuleConfigRegistry()
  }

  fun load(): List<PackageRuleConfig> = loadOrNull() ?: error("PackageRuleConfig is not registered.")

  fun loadOrNull(): List<PackageRuleConfig>? = value

  fun register(value: List<PackageRuleConfig>): PackageRuleConfigRegistry = apply { this.value = value.toList() }
}
