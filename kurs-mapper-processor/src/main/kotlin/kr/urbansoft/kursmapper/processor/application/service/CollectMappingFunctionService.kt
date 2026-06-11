package kr.urbansoft.kursmapper.processor.application.service

import kr.urbansoft.kursmapper.processor.application.port.inbound.CollectMappingFunctionUseCase
import kr.urbansoft.kursmapper.processor.application.port.outbound.LoadConfigPort
import kr.urbansoft.kursmapper.processor.application.port.outbound.LoadKursTypeIdPort
import kr.urbansoft.kursmapper.processor.application.port.outbound.LoadKursTypePort
import kr.urbansoft.kursmapper.processor.application.port.outbound.LoadUserDefinedMappingFunctionPort
import kr.urbansoft.kursmapper.processor.application.port.outbound.SaveCollectedMappingFunctionPort
import kr.urbansoft.kursmapper.processor.domain.model.config.ContextConfig
import kr.urbansoft.kursmapper.processor.domain.model.function.FunctionBody
import kr.urbansoft.kursmapper.processor.domain.model.function.MappingFunction
import kr.urbansoft.kursmapper.processor.domain.model.kurstype.KursType
import kr.urbansoft.kursmapper.processor.domain.model.kurstype.KursTypeId
import kr.urbansoft.kursmapper.processor.domain.model.kurstype.KursTypeName
import kr.urbansoft.kursmapper.processor.domain.service.CollectCandidateMappingFunctionDomainService
import kr.urbansoft.kursmapper.processor.domain.service.CollectPreDefinedMappingFunctionDomainService
import kr.urbansoft.kursmapper.processor.domain.service.MergeMappingFunctionDomainService

class CollectMappingFunctionService(
  private val collectCandidateMappingFunctionDomainService: CollectCandidateMappingFunctionDomainService,
  private val collectPreDefinedMappingFunctionDomainService: CollectPreDefinedMappingFunctionDomainService,
  private val loadConfigPort: LoadConfigPort,
  private val loadKursTypeIdPort: LoadKursTypeIdPort,
  private val loadKursTypePort: LoadKursTypePort,
  private val loadUserDefinedMappingFunctionPort: LoadUserDefinedMappingFunctionPort,
  private val mergeMappingFunctionDomainService: MergeMappingFunctionDomainService,
  private val saveCollectedMappingFunctionPort: SaveCollectedMappingFunctionPort,
) : CollectMappingFunctionUseCase {
  private val contextConfig = loadConfigPort.loadContextConfig()
  private val packageRuleConfigList = loadConfigPort.loadPackageRuleConfigList()
  private val kursTypeRuleConfigList = loadConfigPort.loadKursTypeRuleConfigList()

  override fun collect() {
    mergeMappingFunctionDomainService
      .merge(
        collectCandidateList(),
        collectPreDefinedList(),
        collectSandboxList(),
        collectUserList(),
      )
      .forEach { saveCollectedMappingFunctionPort.save(it) }
  }

  private fun collectCandidateList(): List<MappingFunction> =
    with(collectCandidateMappingFunctionDomainService) {
      val context =
        object : CollectCandidateMappingFunctionDomainService.Context {
          override fun loadKursType(kursTypeId: KursTypeId): KursType? =
            loadKursTypePort.loadById(kursTypeId, contextConfig, packageRuleConfigList, kursTypeRuleConfigList)

          override fun asNullableKursTypeId(kursTypeId: KursTypeId): KursTypeId = loadKursTypeIdPort.asNullable(kursTypeId)

          override fun asNotNullKursTypeId(kursTypeId: KursTypeId): KursTypeId = loadKursTypeIdPort.asNotNull(kursTypeId)
        }
      context.collect(loadConfigPort.loadPurposeMappingFunctionList())
    }

  private fun collectPreDefinedList(): List<MappingFunction> =
    with(collectPreDefinedMappingFunctionDomainService) {
      val functionBodyBuilderContext =
        object : FunctionBody.BuilderContext {
          override val contextConfig: ContextConfig = this@CollectMappingFunctionService.contextConfig
        }
      val context =
        object : CollectPreDefinedMappingFunctionDomainService.Context {
          override fun KursTypeName.asKursTypeId(): KursTypeId = loadKursTypeIdPort.loadByName(this)

          override fun functionBodyBuilderContext(): FunctionBody.BuilderContext = functionBodyBuilderContext
        }
      context.collect()
    }

  private fun collectSandboxList(): List<MappingFunction> = loadConfigPort.loadSandboxMappingFunctionList()

  private fun collectUserList(): List<MappingFunction> =
    with(loadUserDefinedMappingFunctionPort) {
      val context =
        object : LoadUserDefinedMappingFunctionPort.Context {
          override val contextConfig: ContextConfig = this@CollectMappingFunctionService.contextConfig

          override fun KursTypeId.asKursType(): KursType? =
            loadKursTypePort.loadById(this, contextConfig, packageRuleConfigList, kursTypeRuleConfigList)
        }
      context.load()
    }
}
