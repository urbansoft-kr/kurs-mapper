package kr.urbansoft.kursmapper.processor.adapter.infra.ksp.extension

import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSValueArgument
import kotlin.reflect.KClass

fun KSAnnotation.getArgumentOrNull(name: String): KSValueArgument? = arguments.find { it.getNameOrNull() == name }

inline fun <reified E : Enum<E>> KSAnnotation.getArgumentValueAsEnumOrNull(name: String): E? = getArgumentOrNull(name)?.getValueAsEnumOrNull()

inline fun <reified T> KSAnnotation.getArgumentValueAsListOrNull(name: String): List<T?>? = getArgumentOrNull(name)?.getValueAsListOrNull()

inline fun <reified T, S : Any> KSAnnotation.getArgumentValueAsListOrNull(name: String, rawType: KClass<S>, converter: S.() -> T?): List<T?>? =
  getArgumentOrNull(name)?.getValueAsListOrNull(rawType, converter)

fun KSAnnotation.getArgumentValueAsTrimmedStringOrNull(name: String): String? = getArgumentOrNull(name)?.getValueAsTrimmedStringOrNull()

inline fun <reified T> KSAnnotation.getArgumentValueOrNull(name: String): T? = getArgumentOrNull(name)?.getValueOrNull()

fun KSAnnotation.getQualifiedNameOrNull(): String? = annotationType.resolve().declaration.qualifiedName?.asString()
