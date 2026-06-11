package kr.urbansoft.kursmapper.processor.domain.model.function.predefined

import kr.urbansoft.kursmapper.processor.domain.model.function.MappingFunction
import kr.urbansoft.kursmapper.processor.domain.service.CollectPreDefinedMappingFunctionDomainService

interface PreDefinedMappingFunctions {
  fun CollectPreDefinedMappingFunctionDomainService.Context.collect(): List<MappingFunction>

  interface Source {
    companion object
  }
}
