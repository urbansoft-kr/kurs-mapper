package kr.urbansoft.kursmapper.processor.adapter.infra.ksp.extension

import com.google.devtools.ksp.symbol.KSFile

fun KSFile.getPackageName(): String = packageName.asString()
