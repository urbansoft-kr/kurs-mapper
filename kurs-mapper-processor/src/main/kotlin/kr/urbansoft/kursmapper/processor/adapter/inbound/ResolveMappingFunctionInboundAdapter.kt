package kr.urbansoft.kursmapper.processor.adapter.inbound

import kr.urbansoft.kursmapper.processor.application.port.inbound.ResolveMappingFunctionUseCase

class ResolveMappingFunctionInboundAdapter(private val resolveMappingFunctionUseCase: ResolveMappingFunctionUseCase) {
  fun resolve() = resolveMappingFunctionUseCase.resolve()
}
