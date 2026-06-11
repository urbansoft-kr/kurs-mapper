package kr.urbansoft.kursmapper.processor.adapter.outbound

import kr.urbansoft.kursmapper.processor.adapter.infra.ksp.mapper.model.kspMapper
import kr.urbansoft.kursmapper.processor.adapter.infra.ksp.mapper.symbol.kspMapper
import kr.urbansoft.kursmapper.processor.adapter.infra.ksp.model.CheckKSTypeTrait
import kr.urbansoft.kursmapper.processor.adapter.infra.ksp.model.HandleMappedKursTypeId
import kr.urbansoft.kursmapper.processor.adapter.infra.ksp.model.LoadMappedKursTypeId
import kr.urbansoft.kursmapper.processor.adapter.infra.ksp.reader.RawUserDefinedMappingFunctionReader
import kr.urbansoft.kursmapper.processor.application.port.outbound.LoadUserDefinedMappingFunctionPort
import kr.urbansoft.kursmapper.processor.application.port.outbound.LoadUserDefinedMappingFunctionPort.Context
import kr.urbansoft.kursmapper.processor.domain.model.function.MappingFunction

class LoadUserDefinedMappingFunctionAdapter(
  private val checkKsTypeTrait: CheckKSTypeTrait,
  private val handleMappedKursTypeId: HandleMappedKursTypeId,
  private val loadMappedKursTypeId: LoadMappedKursTypeId,
  private val rawUserDefinedMappingFunctionReader: RawUserDefinedMappingFunctionReader,
) : LoadUserDefinedMappingFunctionPort {
  override fun Context.load(): List<MappingFunction> {
    return rawUserDefinedMappingFunctionReader.readRawUserDefinedMappingFunctionCandidateList(contextConfig).mapNotNull {
      rawUserDefinedMappingFunctionCandidate ->
      val sourceId =
        rawUserDefinedMappingFunctionCandidate.sourceKsType
          .kspMapper()
          .asKursTypeId(
            loadMappedKursTypeId = loadMappedKursTypeId,
            checkKsTypeTrait = checkKsTypeTrait,
            handleMappedKursTypeId = handleMappedKursTypeId,
          )
      val source = sourceId.asKursType() ?: return@mapNotNull null
      if (rawUserDefinedMappingFunctionCandidate.mapperPackageName != source.mapperPackageName) return@mapNotNull null
      if (rawUserDefinedMappingFunctionCandidate.mapperName != source.mapperName) return@mapNotNull null

      val targetId =
        rawUserDefinedMappingFunctionCandidate.targetKsType
          .kspMapper()
          .asKursTypeId(
            loadMappedKursTypeId = loadMappedKursTypeId,
            checkKsTypeTrait = checkKsTypeTrait,
            handleMappedKursTypeId = handleMappedKursTypeId,
          )
      val target = targetId.asKursType() ?: return@mapNotNull null
      if (rawUserDefinedMappingFunctionCandidate.mappingFunctionName != target.mappingFunctionName) return@mapNotNull null

      val argumentList =
        rawUserDefinedMappingFunctionCandidate.argumentList
          .kspMapper()
          .asArgumentList(
            loadMappedKursTypeId = loadMappedKursTypeId,
            checkKsTypeTrait = checkKsTypeTrait,
            handleMappedKursTypeId = handleMappedKursTypeId,
          )

      MappingFunction.createUserDefined(sourceId = sourceId, targetId = targetId, argumentList = argumentList)
    }
  }
}
