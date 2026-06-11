package kr.urbansoft.kursmapper.processor.domain.model.function.predefined.kotlin

import kr.urbansoft.kursmapper.processor.domain.model.function.FunctionBody
import kr.urbansoft.kursmapper.processor.domain.model.function.MappingFunction
import kr.urbansoft.kursmapper.processor.domain.model.function.predefined.PreDefinedMappingFunctions
import kr.urbansoft.kursmapper.processor.domain.model.function.predefined.PreDefinedMappingFunctions.Source.Companion
import kr.urbansoft.kursmapper.processor.domain.model.kurstype.KursTypeName
import kr.urbansoft.kursmapper.processor.domain.service.CollectPreDefinedMappingFunctionDomainService.Context

val Companion.String: PreDefinedMappingFunctions
  get() = concrete

private val concrete =
  object : PreDefinedMappingFunctions {
    override fun Context.collect(): List<MappingFunction> = listOf(asJavaUUID())

    private fun Context.asJavaUUID(): MappingFunction =
      MappingFunction.createPreDefined(
        sourceId = KursTypeName.from("kotlin.String").asKursTypeId(),
        targetId = KursTypeName.from("java.util.UUID").asKursTypeId(),
        body =
          FunctionBody.build(functionBodyBuilderContext()) {
            returnKeyword()
            text("UUID")
            dot().text("fromString").invoke { source() }
          },
        importList = FunctionBody.Import.buildList(functionBodyBuilderContext()) { nothing() },
      )
  }
