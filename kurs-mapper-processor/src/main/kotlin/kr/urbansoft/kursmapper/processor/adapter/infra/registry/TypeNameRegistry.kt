package kr.urbansoft.kursmapper.processor.adapter.infra.registry

import com.squareup.kotlinpoet.TypeName
import kr.urbansoft.kursmapper.processor.domain.model.kurstype.KursTypeId

class TypeNameRegistry private constructor(private val map: MutableMap<KursTypeId, TypeName>) {
  companion object {
    fun create(): TypeNameRegistry = TypeNameRegistry(mutableMapOf())
  }

  fun exists(sourceId: KursTypeId): Boolean = map.containsKey(sourceId)

  fun get(sourceId: KursTypeId): TypeName = map[sourceId] ?: error("typeName is not found: $sourceId")

  fun getAll(): List<TypeName> = map.values.toList()

  fun getOrNull(sourceId: KursTypeId): TypeName? = map[sourceId]

  fun put(sourceId: KursTypeId, typeName: TypeName): TypeNameRegistry = apply { map[sourceId] = typeName }

  fun putIfAbsent(sourceId: KursTypeId, typeName: TypeName): TypeNameRegistry = apply { map.putIfAbsent(sourceId, typeName) }
}
