package kr.urbansoft.kursmapper.processor.adapter.infra.config.mapper.symbol

import com.google.devtools.ksp.symbol.KSFunctionDeclaration

fun KSFunctionDeclaration.configMapper() = KSFunctionDeclarationMapper(this)

@JvmInline value class KSFunctionDeclarationMapper(private val source: KSFunctionDeclaration)
