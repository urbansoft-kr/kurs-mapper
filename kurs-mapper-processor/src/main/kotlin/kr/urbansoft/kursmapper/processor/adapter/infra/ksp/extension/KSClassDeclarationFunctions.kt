package kr.urbansoft.kursmapper.processor.adapter.infra.ksp.extension

import com.google.devtools.ksp.isAbstract
import com.google.devtools.ksp.isPublic
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.Modifier
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toClassNameOrNull
import com.squareup.kotlinpoet.ksp.toTypeName
import kotlin.reflect.KClass
import kr.urbansoft.kursmapper.processor.domain.model.packages.PackageName

fun KSClassDeclaration.getAllMemberFunctionDeclarations(kspResolver: Resolver): List<KSFunctionDeclaration> {
  return getAllFunctions()
    .filter { it.isPublic() }
    .filterNot { it.isAbstract }
    .filterNot { it.modifiers.contains(Modifier.JAVA_STATIC) }
    .filter { it.hasValidReturnType(kspResolver) }
    .filter {
      when (it.simpleName.asString()) {
        "<init>",
        "equals",
        "hashCode",
        "toString",
        "clone",
        "copy" -> false
        else -> true
      }
    }
    .toList()
}

fun KSClassDeclaration.getAllStaticOrCompanionFunctionDeclaration(kspResolver: Resolver): List<KSFunctionDeclaration> {
  val staticMethods =
    this.getAllFunctions().filter { it.modifiers.contains(Modifier.JAVA_STATIC) }.filter { it.hasValidReturnType(kspResolver) }
  val companionMethods =
    this.declarations
      .filterIsInstance<KSClassDeclaration>()
      .filter { it.isCompanionObject }
      .flatMap { it.getAllMemberFunctionDeclarations(kspResolver) }
  return (staticMethods + companionMethods).toList()
}

fun KSClassDeclaration.getAnnotatedProperties(qualifiedName: String): List<KSPropertyDeclaration> =
  getAllProperties().filter { it.getAnnotationOrNull(qualifiedName) != null }.toList()

inline fun <reified T> KSClassDeclaration.getAnnotationArgumentValueAsListOrNull(qualifiedName: String, argumentName: String): List<T?>? {
  return getAnnotationOrNull(qualifiedName)?.getArgumentValueAsListOrNull(argumentName)
}

inline fun <reified T, S : Any> KSClassDeclaration.getAnnotationArgumentValueAsListOrNull(
  qualifiedName: String,
  argumentName: String,
  rawType: KClass<S>,
  converter: S.() -> T?,
): List<T?>? {
  return getAnnotationOrNull(qualifiedName)?.getArgumentValueAsListOrNull(argumentName, rawType, converter)
}

fun KSClassDeclaration.getAnnotationArgumentValueAsTrimmedStringOrNull(qualifiedName: String, argumentName: String): String? =
  getAnnotationOrNull(qualifiedName)?.getArgumentValueAsTrimmedStringOrNull(argumentName)

fun KSClassDeclaration.getAnnotationOrNull(qualifiedName: String): KSAnnotation? = annotations.find {
  it.getQualifiedNameOrNull() == qualifiedName
}

fun KSClassDeclaration.getInstantiatorOrNull(
  rootPackageName: PackageName,
  kspResolver: Resolver,
  thisKsType: KSType? = null,
): KSFunctionDeclaration? {
  val primaryConstructorDeclaration = getPrimaryConstructorDeclaration(rootPackageName) ?: return null
  if (primaryConstructorDeclaration.isPublic()) return primaryConstructorDeclaration

  val constructorParameters = primaryConstructorDeclaration.parameters.toList()

  return getAllStaticOrCompanionFunctionDeclaration(kspResolver)
    .filter { it.isPublic() }
    .filter { it.parameters.size == constructorParameters.size }
    .filter { candidate ->
      val returnKsType = candidate.returnType?.resolve() ?: return@filter false
      thisKsType?.let { it.makeNotNullable().toTypeName() == returnKsType.toTypeName() }
        ?: (toClassName() == returnKsType.toClassNameOrNull())
    }
    .singleOrNull {
      constructorParameters.zip(it.parameters).all { (constructorParameter, functionParameter) ->
        val constructorParameterName = constructorParameter.name?.asString()
        val functionParameterName = functionParameter.name?.asString()
        val isNameMatch = constructorParameterName == functionParameterName

        val constructorParameterTypeName = constructorParameter.type.resolve().toTypeName()
        val functionParameterTypeName = functionParameter.type.resolve().toTypeName()
        val isTypeMatch = constructorParameterTypeName == functionParameterTypeName

        isNameMatch && isTypeMatch
      }
    }
}

fun KSClassDeclaration.getPackageName(): String = packageName.asString()

fun KSClassDeclaration.getPrimaryConstructorDeclaration(rootPackageName: PackageName): KSFunctionDeclaration? {
  if (isOutsideOfPackage(rootPackageName)) return null
  if (isAbstract()) return null
  if (Modifier.SEALED in modifiers) return null
  if (Modifier.INNER in modifiers) return null
  return primaryConstructor
}

fun KSClassDeclaration.getQualifiedNameOrNull(): String? = qualifiedName?.asString()

fun KSClassDeclaration.getSimpleName(): String = simpleName.asString()

fun KSClassDeclaration.isInsideOfPackage(packageName: PackageName): Boolean = getPackageName() in packageName

fun KSClassDeclaration.isInterface(): Boolean = this.classKind == ClassKind.INTERFACE

fun KSClassDeclaration.isOutsideOfPackage(packageName: PackageName): Boolean = !isInsideOfPackage(packageName)

inline fun List<KSClassDeclaration>.validateAllInterface(
  whenNotInterface: KSClassDeclaration.() -> Unit = error("declaration is not an interface.")
): List<KSClassDeclaration> =
  partition { it.isInterface() }
    .let { (interfaces, notInterfaces) ->
      notInterfaces.forEach { whenNotInterface(it) }
      interfaces
    }

inline fun <K> List<KSClassDeclaration>.validateUnique(
  keySelector: KSClassDeclaration.() -> K,
  whenDuplicated: KSClassDeclaration.(K) -> Unit = error("key is duplicated: $this"),
): List<KSClassDeclaration> =
  groupBy { ksClassDeclaration -> keySelector(ksClassDeclaration) }
    .also { grouped ->
      grouped.entries
        .find { it.value.size > 1 }
        ?.let { (contextName, configInterfaceDeclarationList) -> whenDuplicated(configInterfaceDeclarationList.first(), contextName) }
    }
    .mapValues { it.value.first() }
    .values
    .toList()
