package kr.urbansoft.kursmapper.processor.adapter.infra.ksp.extension

import com.google.devtools.ksp.symbol.KSValueParameter

fun KSValueParameter.getNameOrNull(): String? = name?.asString()
