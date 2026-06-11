package kr.urbansoft.kursmapper.processor.domain.service

import kr.urbansoft.kursmapper.processor.domain.model.function.FunctionBody
import kr.urbansoft.kursmapper.processor.domain.model.function.MappingFunction
import kr.urbansoft.kursmapper.processor.domain.model.function.predefined.PreDefinedMappingFunctions
import kr.urbansoft.kursmapper.processor.domain.model.function.predefined.PreDefinedMappingFunctions.Source
import kr.urbansoft.kursmapper.processor.domain.model.function.predefined.datetime.DayOfWeek
import kr.urbansoft.kursmapper.processor.domain.model.function.predefined.datetime.LocalDate
import kr.urbansoft.kursmapper.processor.domain.model.function.predefined.datetime.LocalDateTime
import kr.urbansoft.kursmapper.processor.domain.model.function.predefined.datetime.LocalTime
import kr.urbansoft.kursmapper.processor.domain.model.function.predefined.datetime.Month
import kr.urbansoft.kursmapper.processor.domain.model.function.predefined.datetime.TimeZone
import kr.urbansoft.kursmapper.processor.domain.model.function.predefined.kotlin.String
import kr.urbansoft.kursmapper.processor.domain.model.function.predefined.time.Instant
import kr.urbansoft.kursmapper.processor.domain.model.function.predefined.time.JavaDayOfWeek
import kr.urbansoft.kursmapper.processor.domain.model.function.predefined.time.JavaInstant
import kr.urbansoft.kursmapper.processor.domain.model.function.predefined.time.JavaLocalDate
import kr.urbansoft.kursmapper.processor.domain.model.function.predefined.time.JavaLocalDateTime
import kr.urbansoft.kursmapper.processor.domain.model.function.predefined.time.JavaLocalTime
import kr.urbansoft.kursmapper.processor.domain.model.function.predefined.time.JavaMonth
import kr.urbansoft.kursmapper.processor.domain.model.function.predefined.time.JavaZoneId
import kr.urbansoft.kursmapper.processor.domain.model.function.predefined.util.JavaUUID
import kr.urbansoft.kursmapper.processor.domain.model.kurstype.KursTypeId
import kr.urbansoft.kursmapper.processor.domain.model.kurstype.KursTypeName

class CollectPreDefinedMappingFunctionDomainService {
  interface Context {
    fun KursTypeName.asKursTypeId(): KursTypeId

    fun functionBodyBuilderContext(): FunctionBody.BuilderContext
  }

  fun Context.collect(): List<MappingFunction> =
    Collector(this)
      .collect(
        Source.DayOfWeek,
        Source.Instant,
        Source.JavaDayOfWeek,
        Source.JavaInstant,
        Source.JavaLocalDate,
        Source.JavaLocalDateTime,
        Source.JavaLocalTime,
        Source.JavaMonth,
        Source.JavaUUID,
        Source.JavaZoneId,
        Source.LocalDate,
        Source.LocalDateTime,
        Source.LocalTime,
        Source.Month,
        Source.String,
        Source.TimeZone,
      )
      .build()

  private class Collector(private val context: Context, private val list: MutableList<MappingFunction> = mutableListOf()) {
    fun collect(vararg preDefinedMappingFunctionsList: PreDefinedMappingFunctions): Collector = apply {
      for (preDefinedMappingFunctions in preDefinedMappingFunctionsList) {
        with(preDefinedMappingFunctions) { list += context.collect() }
      }
    }

    fun build(): List<MappingFunction> = list.distinct().toList()
  }
}
