package kr.urbansoft.kursmapper.processor.domain.model.function

import kr.urbansoft.kursmapper.processor.domain.model.kurstype.KursTypeId
import kr.urbansoft.kursmapper.processor.domain.model.mapper.MapperName
import kr.urbansoft.shared.exception.ExceptionMessageSupport
import kr.urbansoft.shared.exception.ExceptionType
import kr.urbansoft.shared.validation.validate

@ConsistentCopyVisibility
data class MappingFunction
private constructor(
  val state: State,
  val origin: Origin,
  val sourceId: KursTypeId,
  val targetId: KursTypeId,
  val argumentList: List<Argument>,
  val body: FunctionBody,
  val importList: List<FunctionBody.Import>,
  val calleeIdSet: Set<MappingFunctionId>,
) {
  val id: MappingFunctionId = MappingFunctionId.from(sourceId to targetId)

  init {
    validate(state to origin in AvailableStateAndOrigin, { ExceptionMessage.STATE_INVALID }, state.name, origin.name)
  }

  companion object {
    fun createPurpose(
      sourceId: KursTypeId,
      targetId: KursTypeId,
      argumentList: List<Argument>,
    ): MappingFunction =
      from(
        state = State.CREATED,
        origin = Origin.PURPOSE,
        sourceId = sourceId,
        targetId = targetId,
        argumentList = argumentList,
        body = FunctionBody.blank(),
        importList = emptyList(),
        calleeIdSet = emptySet(),
      )

    fun createCandidate(
      sourceId: KursTypeId,
      targetId: KursTypeId,
      argumentList: List<Argument> = emptyList(),
    ): MappingFunction =
      from(
        state = State.CREATED,
        origin = Origin.CANDIDATE,
        sourceId = sourceId,
        targetId = targetId,
        argumentList = argumentList,
        body = FunctionBody.blank(),
        importList = emptyList(),
        calleeIdSet = emptySet(),
      )

    fun createPreDefined(
      sourceId: KursTypeId,
      targetId: KursTypeId,
      argumentList: List<Argument> = emptyList(),
      body: FunctionBody,
      importList: List<FunctionBody.Import>,
    ): MappingFunction =
      from(
        state = State.RESOLVED,
        origin = Origin.PRE_DEFINED,
        sourceId = sourceId,
        targetId = targetId,
        argumentList = argumentList,
        body = body,
        importList = importList,
      )

    fun createSandbox(
      sourceId: KursTypeId,
      targetId: KursTypeId,
      argumentList: List<Argument>,
      functionBodyBuilderContext: FunctionBody.BuilderContext,
      mapperName: MapperName,
      mappingFunctionName: FunctionName,
    ): MappingFunction =
      from(
        state = State.RESOLVED,
        origin = Origin.SANDBOX,
        sourceId = sourceId,
        targetId = targetId,
        argumentList = argumentList,
        body =
          FunctionBody.build(functionBodyBuilderContext) {
            returnKeyword()
            configInterface()
            dot().text(mapperName.value).invoke { source() }
            dot().text(mappingFunctionName.value).invoke(argumentList)
          },
        importList = FunctionBody.Import.buildList(functionBodyBuilderContext) { configInterface() },
      )

    fun createUserDefined(
      sourceId: KursTypeId,
      targetId: KursTypeId,
      argumentList: List<Argument>,
    ): MappingFunction =
      from(
        state = State.RESOLVED,
        origin = Origin.USER,
        sourceId = sourceId,
        targetId = targetId,
        argumentList = argumentList,
      )

    private fun from(
      state: State,
      origin: Origin,
      sourceId: KursTypeId,
      targetId: KursTypeId,
      argumentList: List<Argument>,
      body: FunctionBody = FunctionBody.blank(),
      importList: List<FunctionBody.Import> = emptyList(),
      calleeIdSet: Set<MappingFunctionId> = emptySet(),
    ): MappingFunction =
      MappingFunction(
        state = state,
        origin = origin,
        sourceId = sourceId,
        targetId = targetId,
        argumentList = argumentList.toList(),
        body = body,
        importList = importList.toList(),
        calleeIdSet = calleeIdSet.toSet(),
      )
  }

  fun addArgumentListIfAvailable(argumentList: List<Argument>): MappingFunction {
    return when (origin) {
      Origin.PURPOSE -> this
      Origin.CANDIDATE -> addArgumentList(argumentList)
      Origin.PRE_DEFINED -> this
      Origin.SANDBOX -> this
      Origin.USER -> this
    }
  }

  fun addArgumentList(argumentList: List<Argument>): MappingFunction {
    return argumentList
      .filter { it !in this.argumentList }
      .takeIf { it.isNotEmpty() }
      ?.let { copy(argumentList = (this.argumentList + it).deduplicate()) } ?: this
  }

  fun addCalleeId(calleeId: MappingFunctionId): MappingFunction = copy(calleeIdSet = calleeIdSet + calleeId)

  fun changeSourceIdAndTargetId(pair: Pair<KursTypeId, KursTypeId>): MappingFunction = copy(sourceId = pair.first, targetId = pair.second)

  fun changeToCandidate(): MappingFunction = copy(origin = Origin.CANDIDATE)

  fun clearBody(): MappingFunction =
    when (origin) {
      Origin.PURPOSE -> copy(body = FunctionBody.blank(), importList = emptyList())
      Origin.CANDIDATE -> copy(body = FunctionBody.blank(), importList = emptyList())
      Origin.PRE_DEFINED -> throw ExceptionMessage.BODY_CANNOT_BE_CLEARED.create(origin.name)
      Origin.SANDBOX -> throw ExceptionMessage.BODY_CANNOT_BE_CLEARED.create(origin.name)
      Origin.USER -> this
    }

  fun embody(body: FunctionBody, imports: List<FunctionBody.Import>): MappingFunction =
    when (origin) {
      Origin.PURPOSE -> copy(body = body, importList = imports.toList())
      Origin.CANDIDATE -> copy(body = body, importList = imports.toList())
      Origin.PRE_DEFINED -> throw ExceptionMessage.BODY_CANNOT_BE_EMBODIED.create(origin.name)
      Origin.SANDBOX -> throw ExceptionMessage.BODY_CANNOT_BE_EMBODIED.create(origin.name)
      Origin.USER -> throw ExceptionMessage.BODY_MUST_BE_BLANK.create(origin.name)
    }

  fun hasAllRequiredArguments(calleeList: List<MappingFunction>): Boolean {
    if (isPurpose()) {
      val argumentSet = argumentList.map { it.key }.toSet()
      return calleeList.all { callee -> callee.argumentList.all { argument -> argument.key in argumentSet } }
    }

    val argumentSet = argumentList.map { it.id }.toSet()
    return calleeList.all { callee -> callee.argumentList.all { argument -> argument.id in argumentSet } }
  }

  fun hasAllRequiredArguments(vararg calleeList: MappingFunction): Boolean = hasAllRequiredArguments(calleeList.toList())

  fun hasCalleeId(calleeId: MappingFunctionId): Boolean = calleeId in calleeIdSet

  fun isArgumentLacked(): Boolean = state == State.ARGUMENT_LACKED

  fun isChanged(other: MappingFunction): Boolean {
    if (id != other.id) error("MappingFunction ID mismatch during change verification: ${id.value} != ${other.id.value}")
    return origin != other.origin || state != other.state || body != other.body
  }

  fun isImplementationRequired(): Boolean = state == State.IMPLEMENTATION_REQUIRED

  fun isNotResolved(): Boolean = !isResolved()

  fun isPreDefined(): Boolean = origin == Origin.PRE_DEFINED

  fun isPurpose(): Boolean = origin == Origin.PURPOSE

  fun isReadyToGenerate(): Boolean =
    when (AvailableStateAndOrigin.from(state, origin)) {
      AvailableStateAndOrigin.CREATED_PURPOSE -> true
      AvailableStateAndOrigin.RESOLVED_PURPOSE -> true
      AvailableStateAndOrigin.ARGUMENT_LACKED_PURPOSE -> true
      AvailableStateAndOrigin.IMPLEMENTATION_REQUIRED_PURPOSE -> true
      AvailableStateAndOrigin.CREATED_CANDIDATE -> true
      AvailableStateAndOrigin.RESOLVED_CANDIDATE -> true
      AvailableStateAndOrigin.REMOVED_CANDIDATE -> false
      AvailableStateAndOrigin.IMPLEMENTATION_REQUIRED_CANDIDATE -> true
      AvailableStateAndOrigin.RESOLVED_PRE_DEFINED -> true
      AvailableStateAndOrigin.REMOVED_PRE_DEFINED -> false
      AvailableStateAndOrigin.RESOLVED_SANDBOX -> true
      AvailableStateAndOrigin.RESOLVED_USER -> false
    }

  fun isStateFixed(): Boolean = !shouldResolve()

  fun isRemovable(): Boolean = origin in AvailableStateAndOrigin.removableOriginSet()

  fun isRemoved(): Boolean = state == State.REMOVED

  fun isResolved(): Boolean = state == State.RESOLVED

  fun isSandbox(): Boolean = origin == Origin.SANDBOX

  fun isUser(): Boolean = origin == Origin.USER

  fun markAsArgumentLacked(): MappingFunction = copy(state = State.ARGUMENT_LACKED)

  fun markAsImplementationRequired(): MappingFunction = copy(state = State.IMPLEMENTATION_REQUIRED)

  fun markAsRemoved(): MappingFunction = copy(state = State.REMOVED)

  fun markAsResolved(): MappingFunction = copy(state = State.RESOLVED)

  fun markAsRemovedOrImplementationRequired(): MappingFunction = if (isRemovable()) markAsRemoved() else markAsImplementationRequired()

  fun markAsResolvedOrArgumentLacked(calleeList: List<MappingFunction>): MappingFunction =
    if (hasAllRequiredArguments(calleeList)) markAsResolved() else markAsArgumentLacked()

  fun markAsResolvedOrArgumentLacked(vararg calleeList: MappingFunction): MappingFunction =
    markAsResolvedOrArgumentLacked(calleeList.toList())

  fun maybeCallable(): Boolean =
    when (state) {
      State.CREATED -> true
      State.RESOLVED -> true
      State.REMOVED -> false
      State.ARGUMENT_LACKED -> true
      State.IMPLEMENTATION_REQUIRED -> true
    }

  fun removeCalleeId(calleeId: MappingFunctionId): MappingFunction = copy(calleeIdSet = calleeIdSet - calleeId)

  fun shouldReport(): Boolean {
    return when (state) {
      State.CREATED -> false
      State.RESOLVED -> false
      State.REMOVED -> false
      State.ARGUMENT_LACKED -> true
      State.IMPLEMENTATION_REQUIRED -> true
    }
  }

  fun shouldResolve(): Boolean {
    if (origin == Origin.PRE_DEFINED) return false
    if (origin == Origin.SANDBOX) return false
    if (origin == Origin.USER) return false
    return when (state) {
      State.CREATED -> true
      State.RESOLVED -> true
      State.REMOVED -> false
      State.ARGUMENT_LACKED -> true
      State.IMPLEMENTATION_REQUIRED -> true
    }
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other == null || this::class != other::class) return false
    other as MappingFunction
    return id == other.id
  }

  override fun hashCode(): Int {
    return id.hashCode()
  }

  enum class State {
    CREATED,
    RESOLVED,
    REMOVED,
    ARGUMENT_LACKED,
    IMPLEMENTATION_REQUIRED,
  }

  enum class Origin {
    PURPOSE,
    CANDIDATE,
    PRE_DEFINED,
    SANDBOX,
    USER,
  }

  private enum class AvailableStateAndOrigin(private val state: State, private val origin: Origin) {
    CREATED_PURPOSE(State.CREATED, Origin.PURPOSE),
    RESOLVED_PURPOSE(State.RESOLVED, Origin.PURPOSE),
    ARGUMENT_LACKED_PURPOSE(State.ARGUMENT_LACKED, Origin.PURPOSE),
    IMPLEMENTATION_REQUIRED_PURPOSE(State.IMPLEMENTATION_REQUIRED, Origin.PURPOSE),
    CREATED_CANDIDATE(State.CREATED, Origin.CANDIDATE),
    RESOLVED_CANDIDATE(State.RESOLVED, Origin.CANDIDATE),
    REMOVED_CANDIDATE(State.REMOVED, Origin.CANDIDATE),
    IMPLEMENTATION_REQUIRED_CANDIDATE(State.IMPLEMENTATION_REQUIRED, Origin.CANDIDATE),
    RESOLVED_PRE_DEFINED(State.RESOLVED, Origin.PRE_DEFINED),
    REMOVED_PRE_DEFINED(State.REMOVED, Origin.PRE_DEFINED),
    RESOLVED_SANDBOX(State.RESOLVED, Origin.SANDBOX),
    RESOLVED_USER(State.RESOLVED, Origin.USER);

    companion object {
      fun from(state: State, origin: Origin): AvailableStateAndOrigin =
        entries.find { it.state == state && it.origin == origin } ?: throw ExceptionMessage.STATE_INVALID.create(state.name, origin.name)

      fun removableOriginSet(): Set<Origin> = entries.filter { it.state == State.REMOVED }.map { it.origin }.toSet()

      operator fun contains(pair: Pair<State, Origin>): Boolean = entries.any { it.state == pair.first && it.origin == pair.second }
    }
  }

  enum class ExceptionMessage(override val value: String, override val type: ExceptionType) : ExceptionMessageSupport {
    BODY_CANNOT_BE_CLEARED("Body cannot be cleared when origin is {}.", ExceptionType.CONFLICT),
    BODY_CANNOT_BE_EMBODIED("Body cannot be embodied when origin is {}.", ExceptionType.CONFLICT),
    BODY_MUST_BE_BLANK("Body must be blank when origin is {}.", ExceptionType.CONFLICT),
    STATE_INVALID("State must not be {} when origin is {}", ExceptionType.CONFLICT),
  }
}
