package kr.urbansoft.kursmapper.processor.application.service

import kr.urbansoft.kursmapper.processor.application.port.inbound.ReportUnresolvedMappingFunctionUseCase
import kr.urbansoft.kursmapper.processor.application.port.outbound.LoadCollectedMappingFunctionPort
import kr.urbansoft.kursmapper.processor.application.port.outbound.LoadConfigPort
import kr.urbansoft.kursmapper.processor.application.port.outbound.LoadKursTypePort
import kr.urbansoft.kursmapper.processor.application.port.outbound.RaiseCompileErrorPort
import kr.urbansoft.kursmapper.processor.application.port.outbound.WriteGuideToFilePort
import kr.urbansoft.kursmapper.processor.domain.model.config.ContextConfig
import kr.urbansoft.kursmapper.processor.domain.model.function.MappingFunction
import kr.urbansoft.kursmapper.processor.domain.model.function.MappingFunctionId
import kr.urbansoft.kursmapper.processor.domain.model.kurstype.KursType
import kr.urbansoft.kursmapper.processor.domain.service.GenerateGuideDomainService
import kr.urbansoft.kursmapper.processor.domain.service.guide.LanguageProvider
import kr.urbansoft.kursmapper.processor.shared.exception.ExceptionMessageSupport
import kr.urbansoft.kursmapper.processor.shared.exception.ExceptionType

class ReportUnresolvedMappingFunctionService(
  loadConfigPort: LoadConfigPort,
  private val generateGuideDomainService: GenerateGuideDomainService,
  private val loadCollectedMappingFunctionPort: LoadCollectedMappingFunctionPort,
  private val loadKursTypePort: LoadKursTypePort,
  private val raiseCompileErrorPort: RaiseCompileErrorPort,
  private val writeGuideToFilePort: WriteGuideToFilePort,
) : ReportUnresolvedMappingFunctionUseCase {
  private val contextConfig = loadConfigPort.loadContextConfig()
  private val packageRuleConfigList = loadConfigPort.loadPackageRuleConfigList()
  private val kursTypeRuleConfigList = loadConfigPort.loadKursTypeRuleConfigList()
  private val sandboxMappingFunctionIdSet = loadConfigPort.loadSandboxMappingFunctionList().map { it.id }.toSet()

  override fun report() {
    with(generateGuideDomainService) {
      val allMappingFunctionList = loadCollectedMappingFunctionPort.loadAll()
      val userDefinedMapperQualifiedNameSetForContext = allMappingFunctionList.findUserDefinedMapperQualifiedNameSet {
        loadKursTypePort.loadById(this, contextConfig, packageRuleConfigList, kursTypeRuleConfigList)
      }
      val overwrittenSandboxIdSetForContext = allMappingFunctionList.findOverwrittenSandboxIdSet(sandboxMappingFunctionIdSet)

      fun createContext(mappingFunction: MappingFunction): GenerateGuideDomainService.Context {
        return object : GenerateGuideDomainService.Context {
          override val contextConfig: ContextConfig = this@ReportUnresolvedMappingFunctionService.contextConfig
          override val languageProvider: LanguageProvider = LanguageProvider.create(guideLanguage = contextConfig.guideLanguage)
          override val mappingFunction: MappingFunction = mappingFunction
          override val source: KursType =
            loadKursTypePort.loadById(mappingFunction.sourceId, contextConfig, packageRuleConfigList, kursTypeRuleConfigList)
              ?: throw ExceptionMessage.SOURCE_IS_NOT_FOUND.create(mappingFunction.sourceId.name.value)
          override val target: KursType =
            loadKursTypePort.loadById(mappingFunction.targetId, contextConfig, packageRuleConfigList, kursTypeRuleConfigList)
              ?: throw ExceptionMessage.TARGET_IS_NOT_FOUND.create(mappingFunction.targetId.name.value)

          override val userDefinedMapperQualifiedNameSet: Set<String> = userDefinedMapperQualifiedNameSetForContext

          override fun MappingFunctionId.loadMappingFunction(): MappingFunction =
            loadCollectedMappingFunctionPort.loadById(this) ?: throw ExceptionMessage.MAPPING_FUNCTION_IS_NOT_FOUND.create(this.value)

          override val overwrittenSandboxIdSet: Set<MappingFunctionId> = overwrittenSandboxIdSetForContext
        }
      }

      val guideToPromoteSandboxList =
        allMappingFunctionList.findAllSandboxToReport().map { createContext(mappingFunction = it) }.map { it.generateToPromoteSandbox() }
      if (guideToPromoteSandboxList.isNotEmpty()) writeGuideToFilePort.writeToPromoteSandbox(guideToPromoteSandboxList.joinToString("\n"))

      val guideToResolveMappingFunctionList =
        allMappingFunctionList
          .findMappingFunctionsToReport(overwrittenSandboxIdSetForContext)
          .map { createContext(mappingFunction = it) }
          .map { it.generateToResolveMappingFunction() }

      if (guideToResolveMappingFunctionList.isEmpty()) return

      val mergedGuideToResolveMappingFunction = guideToResolveMappingFunctionList.joinToString("\n")
      writeGuideToFilePort.writeToResolveMappingFunctionGuide(mergedGuideToResolveMappingFunction)
      raiseCompileErrorPort.raise(mergedGuideToResolveMappingFunction)
    }
  }

  enum class ExceptionMessage(override val value: String, override val type: ExceptionType) : ExceptionMessageSupport {
    SOURCE_IS_NOT_FOUND("source is not found: {}", ExceptionType.NOT_FOUND),
    TARGET_IS_NOT_FOUND("target is not found: {}", ExceptionType.NOT_FOUND),
    MAPPING_FUNCTION_IS_NOT_FOUND("mapping function is not found: {}", ExceptionType.NOT_FOUND),
  }
}
