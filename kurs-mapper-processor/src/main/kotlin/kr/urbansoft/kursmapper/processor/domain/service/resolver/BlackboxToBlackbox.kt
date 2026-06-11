package kr.urbansoft.kursmapper.processor.domain.service.resolver

import kr.urbansoft.kursmapper.processor.domain.model.function.MappingFunction
import kr.urbansoft.kursmapper.processor.domain.model.kurstype.KursType.Nullability
import kr.urbansoft.kursmapper.processor.domain.service.ResolveMappingFunctionDomainService.CurrentContext
import kr.urbansoft.kursmapper.processor.domain.service.ResolveMappingFunctionDomainService.WithCallableCallee.Companion.withCallableCallee

fun CurrentContext.resolveBlackboxToBlackbox(): MappingFunction =
  when (source.nullability() to target.nullability()) {
    Nullability.NOT_NULL to Nullability.NOT_NULL -> notNullToNotNull()
    Nullability.NOT_NULL to Nullability.NULLABLE -> notNullToNullable()
    Nullability.NULLABLE to Nullability.NOT_NULL -> nullableToNotNull()
    Nullability.NULLABLE to Nullability.NULLABLE -> nullableToNullable()
    else -> error("Unreachable case")
  }

private fun CurrentContext.notNullToNotNull(): MappingFunction {
  if (source.isListOrSet() && target.isListOrSet()) {
    val unwrappedSourceId = source.genericIdList().firstOrNull() ?: return current.clearBody().markAsRemovedOrImplementationRequired()
    val unwrappedTargetId = target.genericIdList().firstOrNull() ?: return current.clearBody().markAsRemovedOrImplementationRequired()
    val calleeId = calleeId(unwrappedSourceId to unwrappedTargetId)
    val current = current.addCalleeId(calleeId)
    return withCallableCallee(calleeId) { (callee, calleeSource, calleeTarget) ->
      current
        .addArgumentListIfAvailable(callee.argumentList)
        .embody(
          body {
            returnKeyword()
            source()
            dot().map {
              it()
              dot().contextName().invoke()
              dot().functionName(calleeTarget).invoke(argumentList = callee.argumentList, caller = current)
            }
            if (target.isSet()) dot().toSet() else this
          },
          import { callee(callee, calleeSource to calleeTarget) },
        )
        .markAsResolvedOrArgumentLacked(callee)
    } ?: current.clearBody().markAsImplementationRequired()
  }

  if (source.isInternal() || target.isInternal()) return current.clearBody().markAsImplementationRequired()

  return current.clearBody().markAsRemovedOrImplementationRequired()
}

private fun CurrentContext.notNullToNullable(): MappingFunction {
  return resolveNotNullToNullableOrNull() ?: current.clearBody().markAsRemovedOrImplementationRequired()
}

private fun CurrentContext.nullableToNotNull(): MappingFunction {
  return resolveNullableToNotNullOrNull() ?: current.clearBody().markAsRemovedOrImplementationRequired()
}

private fun CurrentContext.nullableToNullable(): MappingFunction {
  return resolveNullableToNullableOrNull() ?: current.clearBody().markAsRemovedOrImplementationRequired()
}
