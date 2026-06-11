package kr.urbansoft.kursmapper.processor.domain.model.function.predefined.time

import kr.urbansoft.kursmapper.processor.domain.model.function.FunctionBody
import kr.urbansoft.kursmapper.processor.domain.model.function.MappingFunction
import kr.urbansoft.kursmapper.processor.domain.model.function.predefined.PreDefinedMappingFunctions
import kr.urbansoft.kursmapper.processor.domain.model.function.predefined.PreDefinedMappingFunctions.Source.Companion
import kr.urbansoft.kursmapper.processor.domain.model.kurstype.KursTypeName
import kr.urbansoft.kursmapper.processor.domain.service.CollectPreDefinedMappingFunctionDomainService.Context

val Companion.JavaLocalDateTime: PreDefinedMappingFunctions
  get() = concrete

private val concrete =
  object : PreDefinedMappingFunctions {
    override fun Context.collect(): List<MappingFunction> = listOf(asLocalDateTime())

    private fun Context.asLocalDateTime(): MappingFunction =
      MappingFunction.createPreDefined(
        sourceId = KursTypeName.from("java.time.LocalDateTime").asKursTypeId(),
        targetId = KursTypeName.from("kotlinx.datetime.LocalDateTime").asKursTypeId(),
        body =
          FunctionBody.build(functionBodyBuilderContext()) {
            returnKeyword()
            source()
            dot().text("toKotlinLocalDateTime").invoke()
          },
        importList =
          FunctionBody.Import.buildList(functionBodyBuilderContext()) {
            add("kotlinx.datetime", "toKotlinLocalDateTime")
          },
      )
  }
