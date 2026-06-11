package kr.urbansoft.kursmapper.processor.domain.model.function

import kr.urbansoft.kursmapper.processor.domain.model.kurstype.KursTypeId
import kr.urbansoft.kursmapper.processor.shared.uuid.UUIDv4

@ConsistentCopyVisibility
data class Argument private constructor(val id: UUIDv4, val name: ArgumentName, val typeId: KursTypeId) {
  val key: Key = Key(name = name, typeId = typeId)

  companion object {
    fun from(id: UUIDv4, name: ArgumentName, typeId: KursTypeId): Argument = Argument(id = id, name = name, typeId = typeId)

    fun create(name: ArgumentName, typeId: KursTypeId): Argument = Argument(id = UUIDv4.generate(), name = name, typeId = typeId)
  }

  fun rename(name: ArgumentName): Argument = copy(name = name)

  fun rename(name: String): Argument = rename(ArgumentName.from(name))

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other == null || this::class != other::class) return false
    other as Argument
    return id == other.id
  }

  override fun hashCode(): Int {
    return id.hashCode()
  }

  data class Key(val name: ArgumentName, val typeId: KursTypeId)
}

fun List<Argument>.deduplicate(): List<Argument> {
  return if (isEmpty()) this
  else
    map { argument ->
        var normalizing = argument.name.value
        while (normalizing.contains("__dup_")) normalizing = normalizing.substringBeforeLast("__dup_")
        if (argument.name.value == normalizing) argument else argument.rename(normalizing)
      }
      .groupBy { argument -> argument.name }
      .flatMap { (_, argumentList) ->
        if (argumentList.size > 1) argumentList.mapIndexed { index, argument -> argument.rename("${argument.name.value}__dup_${index+1}") }
        else argumentList
      }
}
