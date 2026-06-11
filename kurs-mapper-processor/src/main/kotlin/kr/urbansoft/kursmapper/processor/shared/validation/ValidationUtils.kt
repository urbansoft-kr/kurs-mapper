package kr.urbansoft.kursmapper.processor.shared.validation

import kr.urbansoft.kursmapper.processor.shared.exception.ExceptionMessageSupport

inline fun validate(condition: Boolean, exceptionMessage: () -> ExceptionMessageSupport, vararg variables: Any?) {
  if (!condition) throw exceptionMessage().create(*variables)
}
