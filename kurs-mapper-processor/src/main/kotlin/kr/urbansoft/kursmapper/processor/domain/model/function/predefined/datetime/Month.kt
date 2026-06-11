package kr.urbansoft.kursmapper.processor.domain.model.function.predefined.datetime

import kr.urbansoft.kursmapper.processor.domain.model.function.FunctionBody
import kr.urbansoft.kursmapper.processor.domain.model.function.MappingFunction
import kr.urbansoft.kursmapper.processor.domain.model.function.predefined.PreDefinedMappingFunctions
import kr.urbansoft.kursmapper.processor.domain.model.function.predefined.PreDefinedMappingFunctions.Source.Companion
import kr.urbansoft.kursmapper.processor.domain.model.kurstype.KursTypeName
import kr.urbansoft.kursmapper.processor.domain.service.CollectPreDefinedMappingFunctionDomainService.Context

val Companion.Month: PreDefinedMappingFunctions
  get() = concrete

private val concrete =
  object : PreDefinedMappingFunctions {
    override fun Context.collect(): List<MappingFunction> = listOf(asJavaMonth())

    private fun Context.asJavaMonth(): MappingFunction =
      MappingFunction.createPreDefined(
        sourceId = KursTypeName.from("kotlinx.datetime.Month").asKursTypeId(),
        targetId = KursTypeName.from("java.time.Month").asKursTypeId(),
        body =
          FunctionBody.build(functionBodyBuilderContext()) {
            returnKeyword()
            source()
            dot().text("toJavaMonth").invoke()
          },
        importList =
          FunctionBody.Import.buildList(functionBodyBuilderContext()) {
            add("kotlinx.datetime", "toJavaMonth")
          },
      )
  }
