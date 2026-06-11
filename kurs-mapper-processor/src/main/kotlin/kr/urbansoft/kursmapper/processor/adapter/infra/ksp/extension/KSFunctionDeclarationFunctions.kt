package kr.urbansoft.kursmapper.processor.adapter.infra.ksp.extension

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSFunctionDeclaration

fun KSFunctionDeclaration.getAnnotationOrNull(qualifiedName: String): KSAnnotation? = annotations.find {
  it.getQualifiedNameOrNull() == qualifiedName
}

fun KSFunctionDeclaration.getName(): String = simpleName.asString()

fun KSFunctionDeclaration.hasValidReturnType(kspResolver: Resolver): Boolean {
  return returnType != null &&
    when (returnType?.resolve()) {
      kspResolver.builtIns.anyType,
      kspResolver.builtIns.nothingType,
      kspResolver.builtIns.unitType -> false
      else -> true
    }
}
