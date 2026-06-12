package kr.urbansoft.kursmapper.processor.adapter.inbound

import kr.urbansoft.kursmapper.processor.application.port.inbound.ReportUnresolvedMappingFunctionUseCase

class ReportUnresolvedMappingFunctionInboundAdapter(
  private val reportUnresolvedMappingFunctionUseCase: ReportUnresolvedMappingFunctionUseCase
) {
  fun report() = reportUnresolvedMappingFunctionUseCase.report()
}
