package kr.urbansoft.kursmapper.processor.adapter.outbound

import kr.urbansoft.kursmapper.processor.adapter.infra.registry.CollectedMappingFunctionRegistry
import kr.urbansoft.kursmapper.processor.application.port.outbound.LoadCollectedMappingFunctionPort
import kr.urbansoft.kursmapper.processor.domain.model.function.MappingFunction
import kr.urbansoft.kursmapper.processor.domain.model.function.MappingFunctionId

class LoadCollectedMappingFunctionAdapter(private val collectedMappingFunctionRegistry: CollectedMappingFunctionRegistry) :
  LoadCollectedMappingFunctionPort {
  override fun loadAll(): List<MappingFunction> = collectedMappingFunctionRegistry.getAll()

  override fun loadById(id: MappingFunctionId): MappingFunction? = collectedMappingFunctionRegistry.getOrNull(id)
}
