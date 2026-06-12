package kr.urbansoft.kursmapper.processor.adapter.outbound

import kr.urbansoft.kursmapper.processor.application.port.outbound.RaiseCompileErrorPort
import kr.urbansoft.shared.exception.ExceptionMessageSupport
import kr.urbansoft.shared.exception.ExceptionType

class RaiseCompileErrorAdapter : RaiseCompileErrorPort {
  override fun raise(guide: String) {
    throw ExceptionMessage.UNRESOLVED_MAPPING_FUNCTIONS_DETECTED.create(
      buildString {
        appendLine()
        appendLine("[KursMapper]")
        appendLine("Unresolved mapping functions detected.")
        appendLine("Please refer to the KursMapper guide and resolve them.")
        appendLine("You can find the guide in the build console")
        appendLine("or at '/build/generated/ksp/main/resources/KursMapperGuide.txt'")
        appendLine()
        append(guide)
      }
    )
  }

  enum class ExceptionMessage(override val value: String, override val type: ExceptionType) : ExceptionMessageSupport {
    UNRESOLVED_MAPPING_FUNCTIONS_DETECTED("{}", ExceptionType.GUIDE)
  }
}
