package kr.urbansoft.kursmapper.processor.adapter.infra.ksp.provider

import com.squareup.kotlinpoet.ksp.toTypeName
import kr.urbansoft.kursmapper.processor.adapter.infra.ksp.model.HandleMappedKursTypeId
import kr.urbansoft.kursmapper.processor.adapter.infra.registry.KSTypeRegistry
import kr.urbansoft.kursmapper.processor.adapter.infra.registry.KursTypeIdRegistry
import kr.urbansoft.kursmapper.processor.adapter.infra.registry.TypeNameRegistry

class HandleMappedKursTypeIdProvider(
  private val ksTypeRegistry: KSTypeRegistry,
  private val kursTypeIdRegistry: KursTypeIdRegistry,
  private val typeNameRegistry: TypeNameRegistry,
) {
  fun get(): HandleMappedKursTypeId = HandleMappedKursTypeId { ksType, kursTypeId ->
    ksTypeRegistry.put(kursTypeId, ksType)
    kursTypeIdRegistry.put(kursTypeId)
    typeNameRegistry.put(kursTypeId, ksType.toTypeName())
  }
}
