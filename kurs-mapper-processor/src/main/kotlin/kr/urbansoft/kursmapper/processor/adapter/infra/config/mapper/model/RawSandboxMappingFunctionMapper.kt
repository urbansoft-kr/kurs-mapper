package kr.urbansoft.kursmapper.processor.adapter.infra.config.mapper.model

import kr.urbansoft.kursmapper.processor.adapter.infra.config.model.RawSandboxMappingFunction
import kr.urbansoft.kursmapper.processor.adapter.infra.ksp.mapper.model.kspMapper
import kr.urbansoft.kursmapper.processor.adapter.infra.ksp.mapper.symbol.kspMapper
import kr.urbansoft.kursmapper.processor.adapter.infra.ksp.model.CheckKSTypeTrait
import kr.urbansoft.kursmapper.processor.adapter.infra.ksp.model.HandleMappedKursTypeId
import kr.urbansoft.kursmapper.processor.adapter.infra.ksp.model.LoadMappedKursTypeId
import kr.urbansoft.kursmapper.processor.domain.model.function.FunctionBody
import kr.urbansoft.kursmapper.processor.domain.model.function.FunctionName
import kr.urbansoft.kursmapper.processor.domain.model.function.MappingFunction
import kr.urbansoft.kursmapper.processor.domain.model.mapper.MapperName

fun RawSandboxMappingFunction.configMapper() = RawSandboxMappingFunctionMapper(this)

@JvmInline
value class RawSandboxMappingFunctionMapper(private val source: RawSandboxMappingFunction) {
  fun asMappingFunction(
    loadMappedKursTypeId: LoadMappedKursTypeId,
    checkKsTypeTrait: CheckKSTypeTrait,
    handleMappedKursTypeId: HandleMappedKursTypeId,
    functionBodyBuilderContext: FunctionBody.BuilderContext,
  ): MappingFunction =
    MappingFunction.createSandbox(
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
      functionBodyBuilderContext = functionBodyBuilderContext,
      mapperName = MapperName.from(source.mapperName),
      mappingFunctionName = FunctionName.from(source.mappingFunctionName),
    )
}
