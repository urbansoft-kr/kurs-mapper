package kr.urbansoft.kursmapper.processor.domain.model.function.predefined.util

import kr.urbansoft.kursmapper.processor.domain.model.function.FunctionBody
import kr.urbansoft.kursmapper.processor.domain.model.function.MappingFunction
import kr.urbansoft.kursmapper.processor.domain.model.function.predefined.PreDefinedMappingFunctions
import kr.urbansoft.kursmapper.processor.domain.model.function.predefined.PreDefinedMappingFunctions.Source.Companion
import kr.urbansoft.kursmapper.processor.domain.model.kurstype.KursTypeName
import kr.urbansoft.kursmapper.processor.domain.service.CollectPreDefinedMappingFunctionDomainService.Context

val Companion.JavaUUID: PreDefinedMappingFunctions
  get() = concrete

private val concrete =
  object : PreDefinedMappingFunctions {
    override fun Context.collect(): List<MappingFunction> = listOf(asString())

    private fun Context.asString(): MappingFunction =
      MappingFunction.createPreDefined(
        sourceId = KursTypeName.from("java.util.UUID").asKursTypeId(),
        targetId = KursTypeName.from("kotlin.String").asKursTypeId(),
        body =
          FunctionBody.build(functionBodyBuilderContext()) {
            returnKeyword()
            source()
            dot().text("toString").invoke()
          },
        importList = FunctionBody.Import.buildList(functionBodyBuilderContext()) { nothing() },
      )
  }
