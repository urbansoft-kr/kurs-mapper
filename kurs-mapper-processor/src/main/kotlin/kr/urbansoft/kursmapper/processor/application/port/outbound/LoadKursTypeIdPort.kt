package kr.urbansoft.kursmapper.processor.application.port.outbound

import kr.urbansoft.kursmapper.processor.domain.model.kurstype.KursTypeId
import kr.urbansoft.kursmapper.processor.domain.model.kurstype.KursTypeName

interface LoadKursTypeIdPort {
  fun asNotNull(kursTypeId: KursTypeId): KursTypeId

  fun asNullable(kursTypeId: KursTypeId): KursTypeId

  fun loadByName(name: KursTypeName): KursTypeId
}
