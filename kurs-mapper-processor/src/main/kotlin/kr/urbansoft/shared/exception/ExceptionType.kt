package kr.urbansoft.shared.exception

enum class ExceptionType(val messageToDev: String) {
  BAD_REQUEST("Bad Request"),
  CONFLICT("Conflict"),
  GUIDE("Guide"),
  NOT_FOUND("Not Found"),
}
