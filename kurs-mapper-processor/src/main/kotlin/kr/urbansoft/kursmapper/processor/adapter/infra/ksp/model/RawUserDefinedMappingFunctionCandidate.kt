package kr.urbansoft.kursmapper.processor.adapter.infra.ksp.model

import com.google.devtools.ksp.symbol.KSType
import kr.urbansoft.kursmapper.processor.domain.model.function.FunctionName
import kr.urbansoft.kursmapper.processor.domain.model.mapper.MapperName
import kr.urbansoft.kursmapper.processor.domain.model.packages.PackageName

data class RawUserDefinedMappingFunctionCandidate(
  val mapperPackageName: PackageName,
  val mapperName: MapperName,
  val mappingFunctionName: FunctionName,
  val sourceKsType: KSType,
  val targetKsType: KSType,
  val argumentList: List<RawArgument>,
)
