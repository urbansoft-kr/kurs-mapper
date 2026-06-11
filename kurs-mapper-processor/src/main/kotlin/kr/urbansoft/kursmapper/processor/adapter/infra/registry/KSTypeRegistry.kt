package kr.urbansoft.kursmapper.processor.adapter.infra.registry

import com.google.devtools.ksp.symbol.KSType
import kr.urbansoft.kursmapper.processor.domain.model.kurstype.KursTypeId

class KSTypeRegistry private constructor(private val map: MutableMap<KursTypeId, KSType>) {
  companion object {
    fun create(): KSTypeRegistry = KSTypeRegistry(mutableMapOf())
  }

  fun exists(kursTypeId: KursTypeId): Boolean = map.containsKey(kursTypeId)

  fun get(kursTypeId: KursTypeId): KSType = map[kursTypeId] ?: error("ksType is not found: $kursTypeId")

  fun getAll(): List<KSType> = map.values.toList()

  fun getOrNull(kursTypeId: KursTypeId): KSType? = map[kursTypeId]

  fun put(kursTypeId: KursTypeId, typeName: KSType): KSTypeRegistry = apply { map[kursTypeId] = typeName }

  fun putIfAbsent(kursTypeId: KursTypeId, typeName: KSType): KSTypeRegistry = apply { map.putIfAbsent(kursTypeId, typeName) }
}
