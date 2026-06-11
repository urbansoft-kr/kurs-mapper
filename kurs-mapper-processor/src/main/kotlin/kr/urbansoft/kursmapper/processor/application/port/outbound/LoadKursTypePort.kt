package kr.urbansoft.kursmapper.processor.application.port.outbound

import kr.urbansoft.kursmapper.processor.domain.model.config.ContextConfig
import kr.urbansoft.kursmapper.processor.domain.model.config.KursTypeRuleConfig
import kr.urbansoft.kursmapper.processor.domain.model.config.PackageRuleConfig
import kr.urbansoft.kursmapper.processor.domain.model.kurstype.KursType
import kr.urbansoft.kursmapper.processor.domain.model.kurstype.KursTypeId

interface LoadKursTypePort {
  fun loadById(
    id: KursTypeId,
    contextConfig: ContextConfig,
    packageRuleConfigList: List<PackageRuleConfig>,
    kursTypeRuleConfigList: List<KursTypeRuleConfig>,
  ): KursType?
}
