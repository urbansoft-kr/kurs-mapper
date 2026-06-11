package kr.urbansoft.kursmapper.processor.adapter.infra.ksp.model

import kr.urbansoft.kursmapper.processor.domain.model.kurstype.KursTypeId
import kr.urbansoft.kursmapper.processor.domain.model.kurstype.KursTypeName

@JvmInline
value class LoadMappedKursTypeId(private val value: KursTypeName.() -> KursTypeId?) {
  operator fun invoke(typeName: KursTypeName): KursTypeId? = typeName.value()
}
