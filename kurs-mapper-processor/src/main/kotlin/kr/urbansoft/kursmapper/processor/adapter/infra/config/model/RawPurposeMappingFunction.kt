package kr.urbansoft.kursmapper.processor.adapter.infra.config.model

import com.google.devtools.ksp.symbol.KSType
import kr.urbansoft.kursmapper.processor.adapter.infra.ksp.model.RawArgument

data class RawPurposeMappingFunction(val sourceKsType: KSType, val targetKsType: KSType, val argumentList: List<RawArgument>)
