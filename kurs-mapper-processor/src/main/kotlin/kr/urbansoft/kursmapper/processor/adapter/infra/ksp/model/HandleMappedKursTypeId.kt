package kr.urbansoft.kursmapper.processor.adapter.infra.ksp.model

import com.google.devtools.ksp.symbol.KSType
import kr.urbansoft.kursmapper.processor.domain.model.kurstype.KursTypeId

@JvmInline
value class HandleMappedKursTypeId(private val value: (KSType, KursTypeId) -> Unit) {
  operator fun invoke(type: KSType, id: KursTypeId) = value(type, id)
}
