package kr.urbansoft.kursmapper.processor.adapter.infra.config.model

import com.google.devtools.ksp.symbol.KSType

data class RawKursTypeRuleConfig(val ksType: KSType, val rule: RawRuleConfig?)
