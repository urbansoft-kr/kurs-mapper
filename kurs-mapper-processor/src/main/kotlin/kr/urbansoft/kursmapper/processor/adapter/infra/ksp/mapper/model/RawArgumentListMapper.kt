package kr.urbansoft.kursmapper.processor.adapter.infra.ksp.mapper.model

import kr.urbansoft.kursmapper.processor.adapter.infra.ksp.model.CheckKSTypeTrait
import kr.urbansoft.kursmapper.processor.adapter.infra.ksp.model.HandleMappedKursTypeId
import kr.urbansoft.kursmapper.processor.adapter.infra.ksp.model.LoadMappedKursTypeId
import kr.urbansoft.kursmapper.processor.adapter.infra.ksp.model.RawArgument
import kr.urbansoft.kursmapper.processor.domain.model.function.Argument

fun List<RawArgument>.kspMapper() = RawArgumentListMapper(this)

@JvmInline
value class RawArgumentListMapper(private val source: List<RawArgument>) {
  fun asArgumentList(
    loadMappedKursTypeId: LoadMappedKursTypeId,
    checkKsTypeTrait: CheckKSTypeTrait,
    handleMappedKursTypeId: HandleMappedKursTypeId,
  ): List<Argument> = source.map {
    it
      .kspMapper()
      .asArgument(loadMappedKursTypeId = loadMappedKursTypeId, checkKsTypeTrait = checkKsTypeTrait, handleMappedKursTypeId = handleMappedKursTypeId)
  }
}
