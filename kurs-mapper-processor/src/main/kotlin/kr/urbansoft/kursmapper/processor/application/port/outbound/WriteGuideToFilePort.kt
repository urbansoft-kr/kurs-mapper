package kr.urbansoft.kursmapper.processor.application.port.outbound

interface WriteGuideToFilePort {
  fun writeToResolveMappingFunctionGuide(guide: String)

  fun writeToPromoteSandbox(guide: String)
}
