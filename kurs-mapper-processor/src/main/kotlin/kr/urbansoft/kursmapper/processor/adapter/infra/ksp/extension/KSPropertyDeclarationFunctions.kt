package kr.urbansoft.kursmapper.processor.adapter.infra.ksp.extension

import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSPropertyDeclaration

fun KSPropertyDeclaration.getAnnotationOrNull(qualifiedName: String): KSAnnotation? {
  return annotations.find { it.getQualifiedNameOrNull() == qualifiedName }
}
