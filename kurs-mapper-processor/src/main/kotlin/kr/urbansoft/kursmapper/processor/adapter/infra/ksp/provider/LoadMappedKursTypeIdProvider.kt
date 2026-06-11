package kr.urbansoft.kursmapper.processor.adapter.infra.ksp.provider

import kr.urbansoft.kursmapper.processor.adapter.infra.ksp.model.LoadMappedKursTypeId
import kr.urbansoft.kursmapper.processor.adapter.infra.registry.KursTypeIdRegistry

class LoadMappedKursTypeIdProvider(private val kursTypeIdRegistry: KursTypeIdRegistry) {
  fun get(): LoadMappedKursTypeId = LoadMappedKursTypeId { kursTypeIdRegistry.getOrNull(this) }
}
