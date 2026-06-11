package kr.urbansoft.kursmapper.processor.shared.exception

enum class ExceptionType(val description: String) {
  BAD_REQUEST("잘못된 요청"),
  CONFLICT("충돌"),
  INTERNAL_ERROR("내부 오류"),
  NOT_FOUND("찾을 수 없음"),
  GUIDE("가이드"),
}
