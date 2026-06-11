package kr.urbansoft.kursmapper.processor.adapter.infra.ksp.mapper.model

import kr.urbansoft.kursmapper.processor.adapter.infra.ksp.mapper.symbol.kspMapper
import kr.urbansoft.kursmapper.processor.adapter.infra.ksp.model.CheckKSTypeTrait
import kr.urbansoft.kursmapper.processor.adapter.infra.ksp.model.HandleMappedKursTypeId
import kr.urbansoft.kursmapper.processor.adapter.infra.ksp.model.LoadMappedKursTypeId
import kr.urbansoft.kursmapper.processor.adapter.infra.ksp.model.RawArgument
import kr.urbansoft.kursmapper.processor.domain.model.function.Argument
import kr.urbansoft.kursmapper.processor.domain.model.function.ArgumentName

fun RawArgument.kspMapper() = RawArgumentMapper(this)

@JvmInline
value class RawArgumentMapper(private val source: RawArgument) {
  fun asArgument(
    loadMappedKursTypeId: LoadMappedKursTypeId,
    checkKsTypeTrait: CheckKSTypeTrait,
    handleMappedKursTypeId: HandleMappedKursTypeId,
  ): Argument =
    Argument.create(
      name = ArgumentName.from(source.name),
      typeId =
        source.type
          .kspMapper()
          .asKursTypeId(
            loadMappedKursTypeId = loadMappedKursTypeId,
            checkKsTypeTrait = checkKsTypeTrait,
            handleMappedKursTypeId = handleMappedKursTypeId,
          ),
    )
}
