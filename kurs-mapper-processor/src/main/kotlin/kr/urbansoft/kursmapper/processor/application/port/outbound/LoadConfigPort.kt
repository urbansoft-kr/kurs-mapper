package kr.urbansoft.kursmapper.processor.application.port.outbound

import kr.urbansoft.kursmapper.processor.domain.model.config.ContextConfig
import kr.urbansoft.kursmapper.processor.domain.model.config.KursTypeRuleConfig
import kr.urbansoft.kursmapper.processor.domain.model.config.PackageRuleConfig
import kr.urbansoft.kursmapper.processor.domain.model.function.MappingFunction

interface LoadConfigPort {
  fun loadContextConfig(): ContextConfig

  fun loadPackageRuleConfigList(): List<PackageRuleConfig>

  fun loadKursTypeRuleConfigList(): List<KursTypeRuleConfig>

  fun loadPurposeMappingFunctionList(): List<MappingFunction>

  fun loadSandboxMappingFunctionList(): List<MappingFunction>
}
