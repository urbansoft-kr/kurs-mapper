package kr.urbansoft.kursmapper.processor.adapter.inbound

import kr.urbansoft.kursmapper.processor.application.port.inbound.CollectMappingFunctionUseCase

class CollectMappingFunctionInboundAdapter(private val collectMappingFunctionUseCase: CollectMappingFunctionUseCase) {
  fun collect() = collectMappingFunctionUseCase.collect()
}
