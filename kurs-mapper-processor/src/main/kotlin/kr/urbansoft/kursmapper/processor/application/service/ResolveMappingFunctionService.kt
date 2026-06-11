package kr.urbansoft.kursmapper.processor.application.service

import kr.urbansoft.kursmapper.processor.application.port.inbound.ResolveMappingFunctionUseCase
import kr.urbansoft.kursmapper.processor.application.port.outbound.LoadCollectedMappingFunctionPort
import kr.urbansoft.kursmapper.processor.application.port.outbound.LoadConfigPort
import kr.urbansoft.kursmapper.processor.application.port.outbound.LoadKursTypeIdPort
import kr.urbansoft.kursmapper.processor.application.port.outbound.LoadKursTypePort
import kr.urbansoft.kursmapper.processor.application.port.outbound.SaveCollectedMappingFunctionPort
import kr.urbansoft.kursmapper.processor.domain.model.config.ContextConfig
import kr.urbansoft.kursmapper.processor.domain.model.function.MappingFunction
import kr.urbansoft.kursmapper.processor.domain.model.function.MappingFunctionId
import kr.urbansoft.kursmapper.processor.domain.model.kurstype.KursType
import kr.urbansoft.kursmapper.processor.domain.model.kurstype.KursTypeId
import kr.urbansoft.kursmapper.processor.domain.service.ResolveMappingFunctionDomainService

class ResolveMappingFunctionService(
  loadConfigPort: LoadConfigPort,
  private val loadCollectedMappingFunctionPort: LoadCollectedMappingFunctionPort,
  private val loadKursTypeIdPort: LoadKursTypeIdPort,
  private val loadKursTypePort: LoadKursTypePort,
  private val resolveMappingFunctionDomainService: ResolveMappingFunctionDomainService,
  private val saveCollectedMappingFunctionPort: SaveCollectedMappingFunctionPort,
) : ResolveMappingFunctionUseCase {
  private val contextConfig = loadConfigPort.loadContextConfig()
  private val packageRuleConfigList = loadConfigPort.loadPackageRuleConfigList()
  private val kursTypeRuleConfigList = loadConfigPort.loadKursTypeRuleConfigList()
  private val purposeMappingFunctionIdSet = loadConfigPort.loadPurposeMappingFunctionList().map { it.id }.toSet()

  override fun resolve() {
    with(resolveMappingFunctionDomainService) {
      val context =
        object : ResolveMappingFunctionDomainService.Context {
          override val contextConfig: ContextConfig = this@ResolveMappingFunctionService.contextConfig

          override val purposeMappingFunctionIdSet: Set<MappingFunctionId> = this@ResolveMappingFunctionService.purposeMappingFunctionIdSet

          override fun KursTypeId.asKursTypeOrNull(): KursType? =
            loadKursTypePort.loadById(
              id = this,
              contextConfig = contextConfig,
              packageRuleConfigList = packageRuleConfigList,
              kursTypeRuleConfigList = kursTypeRuleConfigList,
            )

          override fun KursTypeId.asNotNull(): KursTypeId = loadKursTypeIdPort.asNotNull(this)

          override fun KursTypeId.asNullable(): KursTypeId = loadKursTypeIdPort.asNullable(this)

          override fun loadAllMappingFunctionList(): List<MappingFunction> = loadCollectedMappingFunctionPort.loadAll()

          override fun saveMappingFunction(mappingFunction: MappingFunction): MappingFunction = saveCollectedMappingFunctionPort.save(mappingFunction)
        }
      context.resolve()
    }
  }
}
