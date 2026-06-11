package kr.urbansoft.kursmapper.processor.adapter.infra.ksp.model

import com.google.devtools.ksp.symbol.KSType

data class RawArgument(val name: String, val type: KSType)
