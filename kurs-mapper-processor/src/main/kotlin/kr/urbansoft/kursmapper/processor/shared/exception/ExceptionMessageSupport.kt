package kr.urbansoft.kursmapper.processor.shared.exception

interface ExceptionMessageSupport {
  val namespace: String
    get() = this::class.qualifiedName?.split('.')?.dropLast(1)?.lastOrNull() ?: "Global"

  val name: String

  val value: String

  val type: ExceptionType

  fun code(): String = name

  fun create(vararg variables: Any?) = ApplicationException(this, *variables)
}
