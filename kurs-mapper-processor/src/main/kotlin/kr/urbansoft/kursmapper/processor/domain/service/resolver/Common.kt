package kr.urbansoft.kursmapper.processor.domain.service.resolver

import kr.urbansoft.kursmapper.processor.domain.model.function.MappingFunction
import kr.urbansoft.kursmapper.processor.domain.service.ResolveMappingFunctionDomainService.CurrentContext
import kr.urbansoft.kursmapper.processor.domain.service.ResolveMappingFunctionDomainService.WithCallableCallee.Companion.withCallableCallee

fun CurrentContext.resolveSourceAndTargetAreIdentical(): MappingFunction {
  return current
    .embody(
      body {
        returnKeyword()
        source()
      },
      import { nothing() },
    )
    .markAsResolved()
}

fun CurrentContext.resolveNotNullToNullableOrNull(): MappingFunction? {
  val calleeId = calleeId(source.id.asNotNull() to target.id.asNotNull())
  val current = current.addCalleeId(calleeId)
  return withCallableCallee(calleeId) { (callee, calleeSource, calleeTarget) ->
    current.replaceArgumentListIfCandidate(callee.argumentList).let {
      it
        .embody(
          body {
            returnKeyword()
            source()
            dot().contextName().invoke()
            dot().functionName(calleeTarget).invoke(argumentList = callee.argumentList, caller = it)
          },
          import {
            callee(callee, calleeSource to calleeTarget)
          },
        )
        .markAsResolvedOrArgumentLacked(callee)
    }
  }
}

fun CurrentContext.resolveNullableToNotNullOrNull(): MappingFunction? {
  val calleeId = calleeId(source.id.asNotNull() to target.id.asNotNull())
  val current = current.addCalleeId(calleeId)
  return withCallableCallee(calleeId) { (callee, calleeSource, calleeTarget) ->
    current.replaceArgumentListIfCandidate(callee.argumentList).let {
      it
        .embody(
          body {
            returnKeyword()
            source()
            question().dot().let {
              it()
              dot().contextName().invoke()
              dot().functionName(calleeTarget).invoke(argumentList = callee.argumentList, caller = it)
            }
            questionColon().text("TODO(\"Implementation required\")")
          },
          import {
            callee(callee, calleeSource to calleeTarget)
          },
        )
        .markAsImplementationRequired()
    }
  }
}

fun CurrentContext.resolveNullableToNullableOrNull(): MappingFunction? {
  val calleeId = calleeId(source.id.asNotNull() to target.id.asNotNull())
  val current = current.addCalleeId(calleeId)
  return withCallableCallee(calleeId) { (callee, calleeSource, calleeTarget) ->
    current.replaceArgumentListIfCandidate(callee.argumentList).let {
      it
        .embody(
          body {
            returnKeyword()
            source()
            question().dot().let {
              it()
              dot().contextName().invoke()
              dot().functionName(calleeTarget).invoke(argumentList = callee.argumentList, caller = it)
            }
          },
          import {
            callee(callee, calleeSource to calleeTarget)
          },
        )
        .markAsResolvedOrArgumentLacked(callee)
    }
  }
}
