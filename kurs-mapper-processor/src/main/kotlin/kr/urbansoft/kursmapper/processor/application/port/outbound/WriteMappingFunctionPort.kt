package kr.urbansoft.kursmapper.processor.application.port.outbound

import kr.urbansoft.kursmapper.processor.domain.model.config.ContextConfig
import kr.urbansoft.kursmapper.processor.domain.model.function.MappingFunction
import kr.urbansoft.kursmapper.processor.domain.model.kurstype.KursType
import kr.urbansoft.kursmapper.processor.domain.model.kurstype.KursTypeId

interface WriteMappingFunctionPort {
  interface Context {
    val contextConfig: ContextConfig
    val mappingFunctionList: List<MappingFunction>

    fun KursTypeId.asKursType(): KursType?
  }

  fun Context.write()
}
