package kr.urbansoft.kursmapper.processor.adapter.infra.ksp.extension

import com.google.devtools.ksp.closestClassDeclaration
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSType
import kr.urbansoft.kursmapper.processor.domain.model.packages.PackageName

fun KSType.getInstantiatorOrNull(rootPackageName: PackageName, kspResolver: Resolver): KSFunctionDeclaration? {
  val classDeclaration = declaration.closestClassDeclaration() ?: return null
  return classDeclaration.getInstantiatorOrNull(rootPackageName, kspResolver, this)
}

fun KSType.getPackageName(): String = declaration.packageName.asString()

fun KSType.getSimpleName(): String = declaration.simpleName.asString()
