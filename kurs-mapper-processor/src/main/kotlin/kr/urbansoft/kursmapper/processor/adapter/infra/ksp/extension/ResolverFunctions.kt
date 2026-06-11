package kr.urbansoft.kursmapper.processor.adapter.infra.ksp.extension

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.validate

fun Resolver.getValidAnnotatedKSClassDeclarationList(qualifiedName: String): List<KSClassDeclaration> =
  getSymbolsWithAnnotation(qualifiedName).filterIsInstance<KSClassDeclaration>().filter { it.validate() }.toList()
