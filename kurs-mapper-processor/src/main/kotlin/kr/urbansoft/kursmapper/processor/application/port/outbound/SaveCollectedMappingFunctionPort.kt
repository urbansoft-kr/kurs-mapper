package kr.urbansoft.kursmapper.processor.application.port.outbound

import kr.urbansoft.kursmapper.processor.domain.model.function.MappingFunction

interface SaveCollectedMappingFunctionPort {
  fun save(mappingFunction: MappingFunction): MappingFunction
}
