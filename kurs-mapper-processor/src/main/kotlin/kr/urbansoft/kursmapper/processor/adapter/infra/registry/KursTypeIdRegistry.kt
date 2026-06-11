package kr.urbansoft.kursmapper.processor.adapter.infra.registry

import kr.urbansoft.kursmapper.processor.domain.model.kurstype.KursTypeId
import kr.urbansoft.kursmapper.processor.domain.model.kurstype.KursTypeName

class KursTypeIdRegistry private constructor(private val map: MutableMap<KursTypeName, KursTypeId>) {
  companion object {
    fun create(): KursTypeIdRegistry = KursTypeIdRegistry(mutableMapOf())
  }

  fun exists(kursTypeName: KursTypeName): Boolean = map.containsKey(kursTypeName)

  fun get(kursTypeName: KursTypeName): KursTypeId = map[kursTypeName] ?: error("kursTypeId is not found: $kursTypeName")

  fun getAll(): List<KursTypeId> = map.values.toList()

  fun getOrNull(kursTypeName: KursTypeName): KursTypeId? = map[kursTypeName]

  fun put(kursTypeId: KursTypeId): KursTypeIdRegistry = apply { map[kursTypeId.name] = kursTypeId }

  fun putIfAbsent(kursTypeId: KursTypeId): KursTypeIdRegistry = apply { map.putIfAbsent(kursTypeId.name, kursTypeId) }
}
