package kr.urbansoft.kursmapper.processor.domain.model.config

import kr.urbansoft.kursmapper.processor.domain.model.packages.PackageName

@ConsistentCopyVisibility
data class PackageRuleConfig private constructor(val packageName: PackageName, val rule: RuleConfig) {
  companion object {
    fun from(packageName: PackageName, rule: RuleConfig): PackageRuleConfig = PackageRuleConfig(packageName = packageName, rule = rule)
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other == null || this::class != other::class) return false
    other as PackageRuleConfig
    return packageName == other.packageName
  }

  override fun hashCode(): Int = packageName.hashCode()
}
