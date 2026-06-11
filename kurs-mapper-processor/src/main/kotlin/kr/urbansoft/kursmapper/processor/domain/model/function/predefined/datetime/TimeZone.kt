package kr.urbansoft.kursmapper.processor.domain.model.function.predefined.datetime

import kr.urbansoft.kursmapper.processor.domain.model.function.FunctionBody
import kr.urbansoft.kursmapper.processor.domain.model.function.MappingFunction
import kr.urbansoft.kursmapper.processor.domain.model.function.predefined.PreDefinedMappingFunctions
import kr.urbansoft.kursmapper.processor.domain.model.function.predefined.PreDefinedMappingFunctions.Source.Companion
import kr.urbansoft.kursmapper.processor.domain.model.kurstype.KursTypeName
import kr.urbansoft.kursmapper.processor.domain.service.CollectPreDefinedMappingFunctionDomainService.Context

val Companion.TimeZone: PreDefinedMappingFunctions
  get() = concrete

private val concrete =
  object : PreDefinedMappingFunctions {
    override fun Context.collect(): List<MappingFunction> = listOf(asJavaZoneId())

    private fun Context.asJavaZoneId(): MappingFunction =
      MappingFunction.createPreDefined(
        sourceId = KursTypeName.from("kotlinx.datetime.TimeZone").asKursTypeId(),
        targetId = KursTypeName.from("java.time.ZoneId").asKursTypeId(),
        body =
          FunctionBody.build(functionBodyBuilderContext()) {
            returnKeyword()
            source()
            dot().text("toJavaZoneId").invoke()
          },
        importList =
          FunctionBody.Import.buildList(functionBodyBuilderContext()) {
            add("kotlinx.datetime", "toJavaZoneId")
          },
      )
  }
