package kr.urbansoft.kursmapper.processor.adapter.outbound

import kr.urbansoft.kursmapper.processor.adapter.infra.registry.CollectedMappingFunctionRegistry
import kr.urbansoft.kursmapper.processor.application.port.outbound.SaveCollectedMappingFunctionPort
import kr.urbansoft.kursmapper.processor.domain.model.function.MappingFunction

class SaveCollectedMappingFunctionAdapter(private val collectedMappingFunctionRegistry: CollectedMappingFunctionRegistry) :
  SaveCollectedMappingFunctionPort {
  override fun save(mappingFunction: MappingFunction): MappingFunction {
    collectedMappingFunctionRegistry.put(mappingFunction.id, mappingFunction)
    return mappingFunction
  }
}
