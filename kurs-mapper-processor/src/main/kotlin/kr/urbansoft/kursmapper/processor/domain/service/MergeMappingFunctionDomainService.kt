package kr.urbansoft.kursmapper.processor.domain.service

import kr.urbansoft.kursmapper.processor.domain.model.function.MappingFunction
import kr.urbansoft.kursmapper.processor.domain.model.function.MappingFunction.Origin
import kr.urbansoft.shared.exception.ExceptionMessageSupport
import kr.urbansoft.shared.exception.ExceptionType
import kr.urbansoft.shared.validation.validate

class MergeMappingFunctionDomainService {
  fun merge(vararg mappingFunctionLists: List<MappingFunction>): List<MappingFunction> {
    return mappingFunctionLists
      .flatMap { it }
      .groupBy { it.id }
      .map { (_, mappingFunctionList) -> mappingFunctionList.reduce { current, target -> merge(current, target) } }
  }

  fun merge(current: MappingFunction, target: MappingFunction): MappingFunction {
    validate(
      current.id == target.id,
      { ExceptionMessage.MAPPING_FUNCTION_ID_MUST_BE_EQUAL },
      current.id.value,
      target.id.value,
    )

    return when (current.origin to target.origin) {
      Origin.PURPOSE to Origin.PURPOSE -> target
      Origin.PURPOSE to Origin.CANDIDATE -> error("Theoretically impossible case: ${current.id.value}, ${target.id.value}")
      Origin.PURPOSE to Origin.PRE_DEFINED -> target
      Origin.PURPOSE to Origin.SANDBOX -> target
      Origin.PURPOSE to Origin.USER -> target

      Origin.CANDIDATE to Origin.PURPOSE -> error("Theoretically impossible case: ${current.id.value}, ${target.id.value}")
      Origin.CANDIDATE to Origin.CANDIDATE -> target
      Origin.CANDIDATE to Origin.PRE_DEFINED -> target
      Origin.CANDIDATE to Origin.SANDBOX -> target
      Origin.CANDIDATE to Origin.USER -> target

      Origin.PRE_DEFINED to Origin.PURPOSE -> current
      Origin.PRE_DEFINED to Origin.CANDIDATE -> current
      Origin.PRE_DEFINED to Origin.PRE_DEFINED -> target
      Origin.PRE_DEFINED to Origin.SANDBOX -> target
      Origin.PRE_DEFINED to Origin.USER -> target

      Origin.SANDBOX to Origin.PURPOSE -> current
      Origin.SANDBOX to Origin.CANDIDATE -> current
      Origin.SANDBOX to Origin.PRE_DEFINED -> current
      Origin.SANDBOX to Origin.SANDBOX -> target
      Origin.SANDBOX to Origin.USER -> target

      Origin.USER to Origin.PURPOSE -> current
      Origin.USER to Origin.CANDIDATE -> current
      Origin.USER to Origin.PRE_DEFINED -> current
      Origin.USER to Origin.SANDBOX -> current
      Origin.USER to Origin.USER -> target

      else -> error("Unreachable case: ${current.id.value}, ${target.id.value}")
    }
  }

  enum class ExceptionMessage(override val value: String, override val type: ExceptionType) : ExceptionMessageSupport {
    MAPPING_FUNCTION_ID_MUST_BE_EQUAL("Mapping function id must be equal: '{}' != '{}'", ExceptionType.BAD_REQUEST)
  }
}
