package kr.urbansoft.kursmapper.processor.adapter.infra.config.mapper.model

import kr.urbansoft.kursmapper.processor.adapter.infra.config.model.RawPurposeMappingFunction
import kr.urbansoft.kursmapper.processor.adapter.infra.ksp.mapper.model.kspMapper
import kr.urbansoft.kursmapper.processor.adapter.infra.ksp.mapper.symbol.kspMapper
import kr.urbansoft.kursmapper.processor.adapter.infra.ksp.model.CheckKSTypeTrait
import kr.urbansoft.kursmapper.processor.adapter.infra.ksp.model.HandleMappedKursTypeId
import kr.urbansoft.kursmapper.processor.adapter.infra.ksp.model.LoadMappedKursTypeId
import kr.urbansoft.kursmapper.processor.domain.model.function.MappingFunction

fun RawPurposeMappingFunction.configMapper() = RawPurposeMappingFunctionMapper(this)

@JvmInline
value class RawPurposeMappingFunctionMapper(private val source: RawPurposeMappingFunction) {
  fun asMappingFunction(
    loadMappedKursTypeId: LoadMappedKursTypeId,
    checkKsTypeTrait: CheckKSTypeTrait,
    handleMappedKursTypeId: HandleMappedKursTypeId,
  ): MappingFunction =
    MappingFunction.createPurpose(
      sourceId =
        source.sourceKsType
          .kspMapper()
          .asKursTypeId(
            loadMappedKursTypeId = loadMappedKursTypeId,
            checkKsTypeTrait = checkKsTypeTrait,
            handleMappedKursTypeId = handleMappedKursTypeId,
          ),
      targetId =
        source.targetKsType
          .kspMapper()
          .asKursTypeId(
            loadMappedKursTypeId = loadMappedKursTypeId,
            checkKsTypeTrait = checkKsTypeTrait,
            handleMappedKursTypeId = handleMappedKursTypeId,
          ),
      argumentList =
        source.argumentList
          .kspMapper()
          .asArgumentList(
            loadMappedKursTypeId = loadMappedKursTypeId,
            checkKsTypeTrait = checkKsTypeTrait,
            handleMappedKursTypeId = handleMappedKursTypeId,
          ),
    )
}
