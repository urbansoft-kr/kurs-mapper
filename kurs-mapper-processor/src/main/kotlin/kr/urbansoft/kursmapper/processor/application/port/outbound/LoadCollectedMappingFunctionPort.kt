package kr.urbansoft.kursmapper.processor.application.port.outbound

import kr.urbansoft.kursmapper.processor.domain.model.function.MappingFunction
import kr.urbansoft.kursmapper.processor.domain.model.function.MappingFunctionId

interface LoadCollectedMappingFunctionPort {
  fun loadAll(): List<MappingFunction>

  fun loadById(id: MappingFunctionId): MappingFunction?
}
