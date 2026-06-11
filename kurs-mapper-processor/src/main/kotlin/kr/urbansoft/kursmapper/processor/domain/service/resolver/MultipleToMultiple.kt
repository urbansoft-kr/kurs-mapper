package kr.urbansoft.kursmapper.processor.domain.service.resolver

import kr.urbansoft.kursmapper.processor.domain.model.function.FunctionBody
import kr.urbansoft.kursmapper.processor.domain.model.function.MappingFunction
import kr.urbansoft.kursmapper.processor.domain.model.kurstype.KursType.Nullability
import kr.urbansoft.kursmapper.processor.domain.service.ResolveMappingFunctionDomainService.CurrentContext
import kr.urbansoft.kursmapper.processor.domain.service.ResolveMappingFunctionDomainService.WithCallableCallee.Companion.withCallableCallee

fun CurrentContext.resolveMultipleToMultiple(): MappingFunction {
  return when (source.nullability() to target.nullability()) {
    Nullability.NOT_NULL to Nullability.NOT_NULL -> notNullToNotNull()
    Nullability.NOT_NULL to Nullability.NULLABLE -> notNullToNullable()
    Nullability.NULLABLE to Nullability.NOT_NULL -> nullableToNotNull()
    Nullability.NULLABLE to Nullability.NULLABLE -> nullableToNullable()
    else -> error("Unreachable case")
  }
}

private fun CurrentContext.notNullToNotNull(): MappingFunction {
  var isResolved = true
  var current = current
  val calleeList: MutableList<MappingFunction> = mutableListOf()
  val importBuilder = FunctionBody.Import.Builder(functionBodyBuilderContext())
  val invokeCode =
    target
      .parameterList()
      .map { targetParameter ->
        val sourceParameter =
          source.parameterList().singleOrNull { it.name == targetParameter.name }
            ?: let {
              isResolved = false
              return@map body {
                nameOf(targetParameter)
                equal()
                text("TODO(\"Implementation required\")")
              }
            }
        if (sourceParameter.typeId == targetParameter.typeId)
          return@map body {
            nameOf(targetParameter)
            equal()
            source()
            dot().nameOf(sourceParameter)
          }
        val calleeId = calleeId(sourceParameter.typeId to targetParameter.typeId)
        current = current.addCalleeId(calleeId)
        withCallableCallee(calleeId) { (callee, calleeSource, calleeTarget) ->
          current = current.addArgumentListIfAvailable(callee.argumentList)
          calleeList += callee
          importBuilder.callee(callee, calleeSource to calleeTarget)
          body {
            nameOf(targetParameter)
            equal()
            source()
            dot().nameOf(sourceParameter)
            dot().contextName().invoke()
            dot().functionName(calleeTarget).invoke(argumentList = callee.argumentList, caller = current)
          }
        }
          ?: let {
            isResolved = false
            return@map body {
              nameOf(targetParameter)
              equal()
              text("TODO(\"Implementation required\")")
            }
          }
      }
      .joinToString(separator = ", ") { it.value }

  return current
    .embody(
      body {
        returnKeyword()
        create(target).invoke {
          text(invokeCode)
        }
      },
      importBuilder.build(),
    )
    .let { if (isResolved) it.markAsResolvedOrArgumentLacked(calleeList) else it.markAsImplementationRequired() }
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
