package kr.urbansoft.kursmapper.processor.domain.service.resolver

import kr.urbansoft.kursmapper.processor.domain.model.function.MappingFunction
import kr.urbansoft.kursmapper.processor.domain.model.kurstype.KursType.Nullability
import kr.urbansoft.kursmapper.processor.domain.service.ResolveMappingFunctionDomainService.CurrentContext
import kr.urbansoft.kursmapper.processor.domain.service.ResolveMappingFunctionDomainService.WithCallableCallee.Companion.withCallableCallee

fun CurrentContext.resolveMultipleToSingle(): MappingFunction {
  return when (source.nullability() to target.nullability()) {
    Nullability.NOT_NULL to Nullability.NOT_NULL -> notNullToNotNull()
    Nullability.NOT_NULL to Nullability.NULLABLE -> notNullToNullable()
    Nullability.NULLABLE to Nullability.NOT_NULL -> nullableToNotNull()
    Nullability.NULLABLE to Nullability.NULLABLE -> nullableToNullable()
    else -> error("Unreachable case")
  }
}

private fun CurrentContext.notNullToNotNull(): MappingFunction {
  val targetParameter = target.parameterList().first()

  // Resolve if exactly one source parameter matches the target type
  source
    .parameterList()
    .singleOrNull { sourceParameter -> sourceParameter.typeId == target.id }
    ?.let { sourceParameter ->
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
    }

  // Resolve if one source parameter matches the target parameter name and type
  source
    .parameterList()
    .firstOrNull { sourceParameter -> sourceParameter.name == targetParameter.name && sourceParameter.typeId == targetParameter.typeId }
    ?.let { sourceParameter ->
      return current
        .embody(
          body {
            returnKeyword()
            create(target).invoke {
              nameOf(targetParameter)
              equal()
              source()
              dot().nameOf(sourceParameter)
            }
          },
          import { create(target) },
        )
        .markAsResolved()
    }

  // Resolve if one source parameter matches the target parameter name but not type
  source
    .parameterList()
    .firstOrNull { sourceParameter -> sourceParameter.name == targetParameter.name && sourceParameter.typeId != targetParameter.typeId }
    ?.let { sourceParameter ->
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

  return current.clearBody().markAsImplementationRequired()
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
