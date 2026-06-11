package kr.urbansoft.kursmapper.processor.adapter.infra.config.mapper.model

import kr.urbansoft.kursmapper.processor.adapter.infra.config.model.RawKursTypeRuleConfig
import kr.urbansoft.kursmapper.processor.adapter.infra.ksp.mapper.symbol.kspMapper
import kr.urbansoft.kursmapper.processor.adapter.infra.ksp.model.CheckKSTypeTrait
import kr.urbansoft.kursmapper.processor.adapter.infra.ksp.model.HandleMappedKursTypeId
import kr.urbansoft.kursmapper.processor.adapter.infra.ksp.model.LoadMappedKursTypeId
import kr.urbansoft.kursmapper.processor.domain.model.config.KursTypeRuleConfig

fun RawKursTypeRuleConfig.configMapper() = RawKursTypeRuleConfigMapper(this)

@JvmInline
value class RawKursTypeRuleConfigMapper(private val source: RawKursTypeRuleConfig) {
  fun asNullableKursTypeRuleConfig(
    loadMappedKursTypeId: LoadMappedKursTypeId,
    checkKsTypeTrait: CheckKSTypeTrait,
    handleMappedKursTypeId: HandleMappedKursTypeId,
  ): KursTypeRuleConfig? {
    val kursTypeId =
      source.ksType
        .kspMapper()
        .asKursTypeId(
          loadMappedKursTypeId = loadMappedKursTypeId,
          checkKsTypeTrait = checkKsTypeTrait,
          handleMappedKursTypeId = handleMappedKursTypeId,
        )
    val rule = source.rule.configMapper().asNullableRuleConfig() ?: return null
    return KursTypeRuleConfig.from(kursTypeId = kursTypeId, rule = rule)
  }
}
