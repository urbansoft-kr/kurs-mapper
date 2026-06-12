package kr.urbansoft.shared.exception

open class ApplicationException : RuntimeException {
  val namespace: String
  val code: String
  val type: ExceptionType

  constructor(namespace: String, code: String, message: String, type: ExceptionType, cause: Throwable? = null) : super(message, cause) {
    this.namespace = namespace
    this.code = code
    this.type = type
  }

  constructor(
    message: ExceptionMessageSupport,
    vararg variables: Any?,
  ) : this(
    namespace = message.namespace,
    code = message.code(),
    message = createMessage(message, *variables),
    type = message.type,
    cause = createCause(*variables),
  )

  companion object {
    private fun createMessage(message: ExceptionMessageSupport, vararg variables: Any?): String {
      val pattern = message.value

      if (variables.isEmpty()) return pattern

      var variableCount = variables.size
      if (variables.last() is Throwable) variableCount--
      if (variableCount <= 0) return pattern

      val formattedMessageBuilder = StringBuilder(pattern.length)

      var lastCopiedIndex = 0
      var nextArgumentIndex = 0
      var currentIndex = 0

      while (currentIndex < pattern.length) {
        // 다음 치환자 찾기
        val placeholderIndex = pattern.indexOf("{}", currentIndex)

        // 더 이상 치환자가 없으면 종료
        if (placeholderIndex == -1) break

        // "{}" 앞의 백슬래시 개수 파악
        var consecutiveBackslashCount = 0
        var scanIndex = placeholderIndex - 1
        while (scanIndex >= lastCopiedIndex && pattern[scanIndex] == '\\') {
          consecutiveBackslashCount++
          scanIndex--
        }

        // 백슬래시가 시작되기 전까지의 텍스트 복사
        val textEndIndex = placeholderIndex - consecutiveBackslashCount
        formattedMessageBuilder.append(pattern, lastCopiedIndex, textEndIndex)

        // 백슬래시 처리 (홀수면 escape, 짝수면 치환)
        val isEscaped = (consecutiveBackslashCount % 2) == 1
        val literalBackslashCount = consecutiveBackslashCount / 2

        formattedMessageBuilder.append("\\".repeat(literalBackslashCount))

        if (isEscaped) {
          // escape된 경우: 백슬래시 절반 + {} 그대로 출력
          formattedMessageBuilder.append("{}")
        } else {
          // escape 안 된 경우: 백슬래시 절반 + 변수 치환
          if (nextArgumentIndex < variableCount) {
            formattedMessageBuilder.append(variables[nextArgumentIndex++])
          } else {
            formattedMessageBuilder.append("{}")
          }
        }

        // 인덱스 갱신
        currentIndex = placeholderIndex + 2
        lastCopiedIndex = currentIndex
      }

      // 남은 부분 복사
      if (lastCopiedIndex < pattern.length) {
        formattedMessageBuilder.append(pattern, lastCopiedIndex, pattern.length)
      }

      return formattedMessageBuilder.toString()
    }

    private fun createCause(vararg variables: Any?): Throwable? {
      val last = variables.lastOrNull()
      return last as? Throwable
    }
  }

  fun translator(): Translator = Translator(this)

  override val message: String
    get() = "[$namespace.$code] ${message()}"

  fun message(): String? = super.message

  @JvmInline
  value class Translator(private val applicationException: ApplicationException) {
    fun wrapIf(fromMessage: ExceptionMessageSupport, toMessage: ExceptionMessageSupport, vararg variables: Any?): Translator {
      if (fromMessage == toMessage) return this
      if (applicationException.namespace == fromMessage.namespace && applicationException.code == fromMessage.code()) {
        val last = variables.lastOrNull()
        return Translator(
          if (last is Throwable)
            throw IllegalArgumentException(
              "The wrap or wrapIf method automatically includes the original exception as a cause. If you intended to manually provide a different cause or replace it, use map or mapIf instead."
            )
          else ApplicationException(toMessage, *variables, applicationException)
        )
      }
      return this
    }

    fun wrap(messages: Pair<ExceptionMessageSupport, ExceptionMessageSupport>, vararg variables: Any?): Translator {
      val (fromMessage, toMessage) = messages
      return wrapIf(fromMessage, toMessage, *variables)
    }

    fun mapIf(fromMessage: ExceptionMessageSupport, toMessage: ExceptionMessageSupport, vararg variables: Any?): Translator {
      if (fromMessage == toMessage) return this
      if (applicationException.namespace == fromMessage.namespace && applicationException.code == fromMessage.code()) {
        val last = variables.lastOrNull()
        return Translator(
          if (last !is Throwable && applicationException.cause != null)
            ApplicationException(toMessage, *variables, applicationException.cause)
          else ApplicationException(toMessage, *variables)
        )
      }
      return this
    }

    fun map(messages: Pair<ExceptionMessageSupport, ExceptionMessageSupport>, vararg variables: Any?): Translator {
      val (fromMessage, toMessage) = messages
      return mapIf(fromMessage, toMessage, *variables)
    }

    fun build(): ApplicationException = applicationException
  }
}
