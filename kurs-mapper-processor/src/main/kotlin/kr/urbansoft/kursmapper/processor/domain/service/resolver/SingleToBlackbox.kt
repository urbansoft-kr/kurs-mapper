package kr.urbansoft.kursmapper.processor.domain.service.resolver

import kr.urbansoft.kursmapper.processor.domain.model.function.MappingFunction
import kr.urbansoft.kursmapper.processor.domain.model.kurstype.KursType.Nullability
import kr.urbansoft.kursmapper.processor.domain.service.ResolveMappingFunctionDomainService.CurrentContext
import kr.urbansoft.kursmapper.processor.domain.service.ResolveMappingFunctionDomainService.WithCallableCallee.Companion.withCallableCallee

fun CurrentContext.resolveSingleToBlackbox(): MappingFunction {
  return when (source.nullability() to target.nullability()) {
    Nullability.NOT_NULL to Nullability.NOT_NULL -> notNullToNotNull()
    Nullability.NOT_NULL to Nullability.NULLABLE -> notNullToNullable()
    Nullability.NULLABLE to Nullability.NOT_NULL -> nullableToNotNull()
    Nullability.NULLABLE to Nullability.NULLABLE -> nullableToNullable()
    else -> error("Unreachable case")
  }
}

private fun CurrentContext.notNullToNotNull(): MappingFunction {
  val sourceParameter = source.parameterList().first()

  if (sourceParameter.typeId == target.id)
    return current
      .embody(
        body {
          returnKeyword()
          source()
          dot().nameOf(sourceParameter)
        },
        import { nothing() },
      )
      .markAsResolved()

  // TODO: Someday, when the opportunity arises,
  //  consider implementing semi-automatic mapping code for Single to Map<String, Any> or Map<String, Any?>

  val calleeId = calleeId(sourceParameter.typeId to target.id)
  val current = current.addCalleeId(calleeId)
  return withCallableCallee(calleeId) { (callee, calleeSource, calleeTarget) ->
    current
      .addArgumentListIfAvailable(callee.argumentList)
      .embody(
        body {
          returnKeyword()
          source()
          dot().nameOf(sourceParameter)
          dot().contextName().invoke()
          dot().functionName(calleeTarget).invoke(argumentList = callee.argumentList, caller = current)
        },
        import {
          callee(callee, calleeSource to calleeTarget)
        },
      )
      .markAsResolvedOrArgumentLacked(callee)
  } ?: current.clearBody().markAsImplementationRequired()
}

private fun CurrentContext.notNullToNullable(): MappingFunction {
  return resolveNotNullToNullableOrNull() ?: current.clearBody().markAsImplementationRequired()
}

private fun CurrentContext.nullableToNotNull(): MappingFunction {
  return resolveNullableToNotNullOrNull() ?: current.clearBody().markAsImplementationRequired()
}

private fun CurrentContext.nullableToNullable(): MappingFunction {
  return resolveNullableToNullableOrNull() ?: current.clearBody().markAsImplementationRequired()
}
