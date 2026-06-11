package kr.urbansoft.kursmapper.processor.adapter.infra.ksp.extension

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSValueArgument
import kotlin.reflect.KClass
import kotlin.reflect.safeCast

fun KSValueArgument.getNameOrNull(): String? = name?.asString()

inline fun <reified E : Enum<E>> KSValueArgument.getValueAsEnumOrNull(): E? =
  getValueOrNull<KSClassDeclaration>()?.run { simpleName.asString() }?.runCatching { enumValueOf<E>(this) }?.getOrNull()

inline fun <reified T> KSValueArgument.getValueAsListOrNull(): List<T?>? {
  val list = (value as? List<*>) ?: return null
  if (list.any { it !is T }) return null
  return list.map { it as? T }
}

inline fun <reified T, S : Any> KSValueArgument.getValueAsListOrNull(rawType: KClass<S>, converter: S.() -> T?): List<T?>? {
  val list = (value as? List<*>) ?: return null
  if (list.any { !rawType.isInstance(it) }) return null
  return list.map { rawType.safeCast(it) }.map { it?.let { converter(it) } }
}

fun KSValueArgument.getValueAsTrimmedStringOrNull(): String? = getValueOrNull<String>()?.trim()?.takeIf { it.isNotBlank() }

inline fun <reified T> KSValueArgument.getValueOrNull(): T? = value as? T
