package kr.urbansoft.shared.validation

import kr.urbansoft.shared.exception.ExceptionMessageSupport

inline fun validate(condition: Boolean, exceptionMessage: () -> ExceptionMessageSupport, vararg variables: Any?) {
  if (!condition) throw exceptionMessage().create(*variables)
}
