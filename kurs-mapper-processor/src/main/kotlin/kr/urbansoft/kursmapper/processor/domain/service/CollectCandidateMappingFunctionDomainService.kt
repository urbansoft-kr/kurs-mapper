package kr.urbansoft.kursmapper.processor.domain.service

import kr.urbansoft.kursmapper.processor.domain.model.function.MappingFunction
import kr.urbansoft.kursmapper.processor.domain.model.kurstype.KursType
import kr.urbansoft.kursmapper.processor.domain.model.kurstype.KursTypeId
import kr.urbansoft.shared.exception.ExceptionMessageSupport
import kr.urbansoft.shared.exception.ExceptionType

class CollectCandidateMappingFunctionDomainService {
  interface Context {
    fun loadKursType(kursTypeId: KursTypeId): KursType?

    fun asNullableKursTypeId(kursTypeId: KursTypeId): KursTypeId

    fun asNotNullKursTypeId(kursTypeId: KursTypeId): KursTypeId
  }

  fun Context.collect(purposeList: List<MappingFunction>): List<MappingFunction> {
    val collector = Collector(context = this)
    purposeList.filter { it.isPurpose() }.forEach { collector.collect(it) }
    return collector.build()
  }

  class Collector(private val context: Context, private val collectedSet: MutableSet<MappingFunction> = mutableSetOf()) {
    fun collect(mappingFunction: MappingFunction) {
      // load source and target
      val source =
        context.loadKursType(mappingFunction.sourceId) ?: throw ExceptionMessage.SOURCE_IS_NOT_FOUND.create(mappingFunction.sourceId)
      val target =
        context.loadKursType(mappingFunction.targetId) ?: throw ExceptionMessage.TARGET_IS_NOT_FOUND.create(mappingFunction.targetId)

      // check if mapping function already collected
      if (mappingFunction in collectedSet) return

      // collect current mapping function
      collectedSet.add(
        mappingFunction
          .changeSourceIdAndTargetId(
            context.asNotNullKursTypeId(mappingFunction.sourceId) to context.asNotNullKursTypeId(mappingFunction.targetId)
          )
          .takeIf { it.id != mappingFunction.id }
          ?.changeToCandidate() ?: mappingFunction
      )
      collectedSet.add(
        mappingFunction
          .changeSourceIdAndTargetId(
            context.asNotNullKursTypeId(mappingFunction.sourceId) to context.asNullableKursTypeId(mappingFunction.targetId)
          )
          .takeIf { it.id != mappingFunction.id }
          ?.changeToCandidate() ?: mappingFunction
      )
      collectedSet.add(
        mappingFunction
          .changeSourceIdAndTargetId(
            context.asNullableKursTypeId(mappingFunction.sourceId) to context.asNotNullKursTypeId(mappingFunction.targetId)
          )
          .takeIf { it.id != mappingFunction.id }
          ?.changeToCandidate() ?: mappingFunction
      )
      collectedSet.add(
        mappingFunction
          .changeSourceIdAndTargetId(
            context.asNullableKursTypeId(mappingFunction.sourceId) to context.asNullableKursTypeId(mappingFunction.targetId)
          )
          .takeIf { it.id != mappingFunction.id }
          ?.changeToCandidate() ?: mappingFunction
      )

      // no more collection if source and target are same
      if (source == target) return

      when (source.kind to target.kind) {
        // no more collection
        KursType.Kind.BLACKBOX to KursType.Kind.BLACKBOX -> {
          if (source.isListOrSet() && target.isListOrSet()) {
            val genericSourceId = source.genericIdList().firstOrNull()
            val genericTargetId = target.genericIdList().firstOrNull()
            if (genericSourceId != null && genericTargetId != null)
              collect(MappingFunction.createCandidate(sourceId = genericSourceId, targetId = genericTargetId))
          }
        }

        // no more collection if source type and target.parameter type are same
        // or collect source -> target.parameter
        KursType.Kind.BLACKBOX to KursType.Kind.SINGLE -> {
          val targetParameter = target.parameterList().first()
          if (source.id != targetParameter.typeId)
            collect(mappingFunction = MappingFunction.createCandidate(sourceId = source.id, targetId = targetParameter.typeId))
        }

        // no more collection
        KursType.Kind.BLACKBOX to KursType.Kind.MULTIPLE -> {}

        // no more collection if source.parameter type and target type are same
        // or collect source.parameter -> target
        KursType.Kind.SINGLE to KursType.Kind.BLACKBOX -> {
          val sourceParameter = source.parameterList().first()
          if (sourceParameter.typeId != target.id)
            collect(MappingFunction.createCandidate(sourceId = sourceParameter.typeId, targetId = target.id))
        }

        // no more collection if source.parameter type and target type are same
        // no more collection if source type and target.parameter type are same
        // no more collection if source.parameter type and target.parameter type are same
        // or collect source.parameter -> target
        KursType.Kind.SINGLE to KursType.Kind.SINGLE -> {
          val sourceParameter = source.parameterList().first()
          val targetParameter = target.parameterList().first()
          when {
            sourceParameter.typeId == target.id -> {}
            source.id == targetParameter.typeId -> {}
            sourceParameter.typeId == targetParameter.typeId -> {}
            else -> collect(MappingFunction.createCandidate(sourceId = sourceParameter.typeId, targetId = target.id))
          }
        }

        // no more collection
        KursType.Kind.SINGLE to KursType.Kind.MULTIPLE -> {}

        // no more collection
        KursType.Kind.MULTIPLE to KursType.Kind.BLACKBOX -> {}

        // collect source.parameter -> target.parameter if source.parameter name is same as target.parameter name
        KursType.Kind.MULTIPLE to KursType.Kind.SINGLE -> {
          val targetParameter = target.parameterList().first()
          source
            .parameterList()
            .firstOrNull { it.name == targetParameter.name }
            ?.takeIf { sourceParameter -> sourceParameter.typeId != targetParameter.typeId }
            ?.let { sourceParameter ->
              collect(MappingFunction.createCandidate(sourceId = sourceParameter.typeId, targetId = targetParameter.typeId))
            }
        }

        // collect source.parameter -> target.parameter if source.parameter name is same as target.parameter name
        KursType.Kind.MULTIPLE to KursType.Kind.MULTIPLE -> {
          target.parameterList().forEach { targetParameter ->
            source
              .parameterList()
              .singleOrNull { it.name == targetParameter.name }
              ?.takeIf { sourceParameter -> sourceParameter.typeId != targetParameter.typeId }
              ?.let { sourceParameter ->
                collect(MappingFunction.createCandidate(sourceId = sourceParameter.typeId, targetId = targetParameter.typeId))
              }
          }
        }
      }
    }

    fun build(): List<MappingFunction> = collectedSet.toList()

    enum class ExceptionMessage(override val value: String, override val type: ExceptionType) : ExceptionMessageSupport {
      SOURCE_IS_NOT_FOUND("source is not found: {}", ExceptionType.NOT_FOUND),
      TARGET_IS_NOT_FOUND("target is not found: {}", ExceptionType.NOT_FOUND),
    }
  }
}
