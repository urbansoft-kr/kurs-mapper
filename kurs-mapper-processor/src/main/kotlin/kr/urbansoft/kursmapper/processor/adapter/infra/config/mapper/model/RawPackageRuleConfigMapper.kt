package kr.urbansoft.kursmapper.processor.adapter.infra.config.mapper.model

import kr.urbansoft.kursmapper.processor.adapter.infra.config.mapper.kotlin.configMapper
import kr.urbansoft.kursmapper.processor.adapter.infra.config.model.RawPackageRuleConfig
import kr.urbansoft.kursmapper.processor.domain.model.config.PackageRuleConfig

fun RawPackageRuleConfig.configMapper() = RawPackageRuleConfigMapper(this)

@JvmInline
value class RawPackageRuleConfigMapper(private val source: RawPackageRuleConfig) {
  fun asNullablePackageRuleConfig(): PackageRuleConfig? {
    val packageName = source.packageName.configMapper().asNullablePackageName() ?: return null
    val rule = source.rule.configMapper().asNullableRuleConfig() ?: return null
    return PackageRuleConfig.from(packageName = packageName, rule = rule)
  }
}
