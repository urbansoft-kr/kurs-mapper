package kr.urbansoft.kursmapper.processor.adapter.infra.registry

import kr.urbansoft.kursmapper.processor.domain.model.function.MappingFunction
import kr.urbansoft.kursmapper.processor.domain.model.function.MappingFunctionId

class CollectedMappingFunctionRegistry private constructor(private val map: MutableMap<MappingFunctionId, MappingFunction>) {
  companion object {
    fun create(): CollectedMappingFunctionRegistry = CollectedMappingFunctionRegistry(mutableMapOf())
  }

  fun exists(mappingFunctionId: MappingFunctionId): Boolean = map.containsKey(mappingFunctionId)

  fun get(mappingFunctionId: MappingFunctionId): MappingFunction =
    map[mappingFunctionId] ?: error("mappingFunction is not found: $mappingFunctionId")

  fun getAll(): List<MappingFunction> = map.values.toList()

  fun getOrNull(mappingFunctionId: MappingFunctionId): MappingFunction? = map[mappingFunctionId]

  fun put(mappingFunctionId: MappingFunctionId, mappingFunction: MappingFunction): CollectedMappingFunctionRegistry = apply {
    map[mappingFunctionId] = mappingFunction
  }

  fun putIfAbsent(mappingFunctionId: MappingFunctionId, mappingFunction: MappingFunction): CollectedMappingFunctionRegistry = apply {
    map.putIfAbsent(mappingFunctionId, mappingFunction)
  }
}
