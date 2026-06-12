package kr.urbansoft.kursmapper.processor.domain.service

import kr.urbansoft.kursmapper.processor.domain.model.config.ContextConfig
import kr.urbansoft.kursmapper.processor.domain.model.function.FunctionBody
import kr.urbansoft.kursmapper.processor.domain.model.function.MappingFunction
import kr.urbansoft.kursmapper.processor.domain.model.function.MappingFunctionId
import kr.urbansoft.kursmapper.processor.domain.model.kurstype.KursType
import kr.urbansoft.kursmapper.processor.domain.model.kurstype.KursType.Kind
import kr.urbansoft.kursmapper.processor.domain.model.kurstype.KursTypeId
import kr.urbansoft.kursmapper.processor.domain.service.resolver.resolveBlackboxToBlackbox
import kr.urbansoft.kursmapper.processor.domain.service.resolver.resolveBlackboxToMultiple
import kr.urbansoft.kursmapper.processor.domain.service.resolver.resolveBlackboxToSingle
import kr.urbansoft.kursmapper.processor.domain.service.resolver.resolveMultipleToBlackbox
import kr.urbansoft.kursmapper.processor.domain.service.resolver.resolveMultipleToMultiple
import kr.urbansoft.kursmapper.processor.domain.service.resolver.resolveMultipleToSingle
import kr.urbansoft.kursmapper.processor.domain.service.resolver.resolveSingleToBlackbox
import kr.urbansoft.kursmapper.processor.domain.service.resolver.resolveSingleToMultiple
import kr.urbansoft.kursmapper.processor.domain.service.resolver.resolveSingleToSingle
import kr.urbansoft.kursmapper.processor.domain.service.resolver.resolveSourceAndTargetAreIdentical
import kr.urbansoft.shared.exception.ExceptionMessageSupport
import kr.urbansoft.shared.exception.ExceptionType

class ResolveMappingFunctionDomainService {
  interface Context {
    val contextConfig: ContextConfig
    val purposeMappingFunctionIdSet: Set<MappingFunctionId>

    fun KursTypeId.asKursTypeOrNull(): KursType?

    fun KursTypeId.asNotNull(): KursTypeId

    fun KursTypeId.asNullable(): KursTypeId

    fun loadAllMappingFunctionList(): List<MappingFunction>

    fun saveMappingFunction(mappingFunction: MappingFunction): MappingFunction
  }

  interface CurrentContext {
    val currentAllMappingFunctionMap: Map<MappingFunctionId, MappingFunction>
    val current: MappingFunction
    val source: KursType
    val target: KursType

    fun functionBodyBuilderContext(): FunctionBody.BuilderContext

    fun KursTypeId.asKursTypeOrNull(): KursType?

    fun KursTypeId.asNotNull(): KursTypeId

    fun KursTypeId.asNullable(): KursTypeId

    fun calleeId(pair: Pair<KursTypeId, KursTypeId>): MappingFunctionId = MappingFunctionId.from(pair.first, pair.second)

    fun callableCallee(calleeId: MappingFunctionId): MappingFunction? =
      currentAllMappingFunctionMap[calleeId]?.takeIf { it.maybeCallable() }

    fun body(block: FunctionBody.Builder.() -> FunctionBody.Builder): FunctionBody =
      block(FunctionBody.Builder(functionBodyBuilderContext())).build()

    fun import(block: FunctionBody.Import.Builder.() -> FunctionBody.Import.Builder): List<FunctionBody.Import> =
      block(FunctionBody.Import.Builder(functionBodyBuilderContext())).build()
  }

  fun Context.resolve() {
    val functionBodyBuilderContext =
      object : FunctionBody.BuilderContext {
        override val contextConfig: ContextConfig = this@resolve.contextConfig
      }

    var shouldContinue = true
    while (shouldContinue) {
      shouldContinue = false

      loadAllMappingFunctionList()
        .filter { it.isRemoved() }
        .forEach { callee ->
          loadAllMappingFunctionList()
            .filter { it.hasCalleeId(callee.id) }
            .forEach { caller ->
              saveMappingFunction(caller.removeCalleeId(callee.id))
              shouldContinue = true
            }
        }

      loadAllMappingFunctionList()
        .filter { it.shouldResolve() }
        .forEach { current ->
          val source = current.sourceId.asKursTypeOrNull() ?: throw ExceptionMessage.SOURCE_IS_NOT_FOUND.create(current.sourceId.name.value)
          val target = current.targetId.asKursTypeOrNull() ?: throw ExceptionMessage.TARGET_IS_NOT_FOUND.create(current.targetId.name.value)
          val currentAllMappingFunctionList = loadAllMappingFunctionList()
          val currentAllMappingFunctionMap = currentAllMappingFunctionList.associateBy { it.id }

          val currentContext =
            object : CurrentContext {
              override val currentAllMappingFunctionMap = currentAllMappingFunctionMap
              override val current = current
              override val source = source
              override val target = target

              override fun functionBodyBuilderContext(): FunctionBody.BuilderContext = functionBodyBuilderContext

              override fun KursTypeId.asKursTypeOrNull(): KursType? {
                val id = this
                return with(this@resolve) { id.asKursTypeOrNull() }
              }

              override fun KursTypeId.asNotNull(): KursTypeId {
                val id = this
                return with(this@resolve) { id.asNotNull() }
              }

              override fun KursTypeId.asNullable(): KursTypeId {
                val id = this
                return with(this@resolve) { id.asNullable() }
              }
            }

          val result =
            if (source == target) currentContext.resolveSourceAndTargetAreIdentical()
            else if (source.isSameExceptNullabilityAndResolvable(target)) currentContext.resolveSourceAndTargetAreIdentical()
            else
              when (source.kind to target.kind) {
                Kind.BLACKBOX to Kind.BLACKBOX -> currentContext.resolveBlackboxToBlackbox()
                Kind.BLACKBOX to Kind.SINGLE -> currentContext.resolveBlackboxToSingle()
                Kind.BLACKBOX to Kind.MULTIPLE -> currentContext.resolveBlackboxToMultiple()
                Kind.SINGLE to Kind.BLACKBOX -> currentContext.resolveSingleToBlackbox()
                Kind.SINGLE to Kind.SINGLE -> currentContext.resolveSingleToSingle()
                Kind.SINGLE to Kind.MULTIPLE -> currentContext.resolveSingleToMultiple()
                Kind.MULTIPLE to Kind.BLACKBOX -> currentContext.resolveMultipleToBlackbox()
                Kind.MULTIPLE to Kind.SINGLE -> currentContext.resolveMultipleToSingle()
                Kind.MULTIPLE to Kind.MULTIPLE -> currentContext.resolveMultipleToMultiple()
                else -> error("Unreachable case")
              }

          if (current.isChanged(result)) {
            saveMappingFunction(result)
            shouldContinue = true
          }
        }

      if (!shouldContinue && removeIsolatedImplementationRequiredMappingFunctions()) shouldContinue = true
      if (!shouldContinue && removeIsolatedPreDefinedMappingFunctions()) shouldContinue = true
    }
  }

  private fun Context.removeIsolatedImplementationRequiredMappingFunctions(): Boolean {
    var isChanged = false

    val currentAllMappingFunctionList = loadAllMappingFunctionList()
    currentAllMappingFunctionList
      .filterNot { mappingFunction -> mappingFunction.id in purposeMappingFunctionIdSet }
      .filterNot { mappingFunction -> mappingFunction.isRemoved() }
      .filter { mappingFunction -> mappingFunction.isImplementationRequired() }
      .filterIsolatedMappingFunctions(
        allMappingFunctionList = currentAllMappingFunctionList,
        purposeMappingFunctionIdSet = purposeMappingFunctionIdSet,
      )
      .forEach { mappingFunction ->
        saveMappingFunction(mappingFunction.clearBody().markAsRemoved())
        isChanged = true
      }

    return isChanged
  }

  private fun Context.removeIsolatedPreDefinedMappingFunctions(): Boolean {
    var isChanged = false

    val currentAllMappingFunctionList = loadAllMappingFunctionList()
    currentAllMappingFunctionList
      .filterNot { mappingFunction -> mappingFunction.id in purposeMappingFunctionIdSet }
      .filterNot { mappingFunction -> mappingFunction.isRemoved() }
      .filter { mappingFunction -> mappingFunction.isPreDefined() }
      .filterIsolatedMappingFunctions(
        allMappingFunctionList = currentAllMappingFunctionList,
        purposeMappingFunctionIdSet = purposeMappingFunctionIdSet,
      )
      .forEach { mappingFunction ->
        saveMappingFunction(mappingFunction.markAsRemoved())
        isChanged = true
      }

    return isChanged
  }

  private fun List<MappingFunction>.filterIsolatedMappingFunctions(
    allMappingFunctionList: List<MappingFunction>,
    purposeMappingFunctionIdSet: Set<MappingFunctionId>,
  ): List<MappingFunction> {
    val allMappingFunctionByCalleeIdMap =
      allMappingFunctionList
        .flatMap { mappingFunction -> mappingFunction.calleeIdSet.map { calleeId -> calleeId to mappingFunction } }
        .groupBy(keySelector = { it.first }, valueTransform = { it.second })
    fun MappingFunction.isInPurposeChain(visited: MutableSet<MappingFunctionId> = mutableSetOf()): Boolean {
      if (this.id in purposeMappingFunctionIdSet) return true
      if (!visited.add(this.id)) return false
      val callerList = allMappingFunctionByCalleeIdMap[this.id] ?: return false
      return callerList.any { it.isInPurposeChain(visited) }
    }
    return filterNot { it.isInPurposeChain() }
  }

  @ConsistentCopyVisibility
  data class WithCallableCallee
  private constructor(
    val callee: MappingFunction,
    val calleeSource: KursType,
    val calleeTarget: KursType,
  ) {
    companion object {
      fun <T> CurrentContext.withCallableCallee(calleeId: MappingFunctionId, block: (WithCallableCallee) -> T): T? {
        val callee = callableCallee(calleeId) ?: return null
        val calleeSource =
          callee.sourceId.asKursTypeOrNull() ?: throw ExceptionMessage.CALLEE_SOURCE_IS_NOT_FOUND.create(callee.sourceId.name.value)
        val calleeTarget =
          callee.targetId.asKursTypeOrNull() ?: throw ExceptionMessage.CALLEE_TARGET_IS_NOT_FOUND.create(callee.targetId.name.value)
        return block(WithCallableCallee(callee, calleeSource, calleeTarget))
      }
    }
  }

  enum class ExceptionMessage(override val value: String, override val type: ExceptionType) : ExceptionMessageSupport {
    SOURCE_IS_NOT_FOUND("source is not found: {}", ExceptionType.NOT_FOUND),
    TARGET_IS_NOT_FOUND("target is not found: {}", ExceptionType.NOT_FOUND),
    CALLEE_SOURCE_IS_NOT_FOUND("callee source is not found: {}", ExceptionType.NOT_FOUND),
    CALLEE_TARGET_IS_NOT_FOUND("callee target is not found: {}", ExceptionType.NOT_FOUND),
  }
}
