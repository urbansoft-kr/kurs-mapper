package kr.urbansoft.kursmapper.processor.adapter.infra.registry

import kr.urbansoft.kursmapper.processor.domain.model.kurstype.KursType
import kr.urbansoft.kursmapper.processor.domain.model.kurstype.KursTypeId

class KursTypeRegistry private constructor(private val map: MutableMap<KursTypeId, KursType>) {
  companion object {
    fun create(): KursTypeRegistry = KursTypeRegistry(mutableMapOf())
  }

  fun exists(kursTypeId: KursTypeId): Boolean = map.containsKey(kursTypeId)

  fun get(kursTypeId: KursTypeId): KursType = map[kursTypeId] ?: error("kursType is not found: $kursTypeId")

  fun getAll(): List<KursType> = map.values.toList()

  fun getOrNull(kursTypeId: KursTypeId): KursType? = map[kursTypeId]

  fun put(kursType: KursType): KursTypeRegistry = apply { map[kursType.id] = kursType }

  fun putIfAbsent(kursType: KursType): KursTypeRegistry = apply { map.putIfAbsent(kursType.id, kursType) }
}
