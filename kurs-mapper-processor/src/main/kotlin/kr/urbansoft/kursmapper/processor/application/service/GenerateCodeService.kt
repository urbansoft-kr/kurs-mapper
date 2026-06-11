package kr.urbansoft.kursmapper.processor.application.service

import kr.urbansoft.kursmapper.processor.application.port.inbound.GenerateCodeUseCase
import kr.urbansoft.kursmapper.processor.application.port.outbound.LoadCollectedMappingFunctionPort
import kr.urbansoft.kursmapper.processor.application.port.outbound.LoadConfigPort
import kr.urbansoft.kursmapper.processor.application.port.outbound.LoadKursTypePort
import kr.urbansoft.kursmapper.processor.application.port.outbound.WriteMappingFunctionPort
import kr.urbansoft.kursmapper.processor.domain.model.config.ContextConfig
import kr.urbansoft.kursmapper.processor.domain.model.function.MappingFunction
import kr.urbansoft.kursmapper.processor.domain.model.kurstype.KursType
import kr.urbansoft.kursmapper.processor.domain.model.kurstype.KursTypeId

class GenerateCodeService(
  loadConfigPort: LoadConfigPort,
  private val loadCollectedMappingFunctionPort: LoadCollectedMappingFunctionPort,
  private val loadKursTypePort: LoadKursTypePort,
  private val writeMappingFunctionPort: WriteMappingFunctionPort,
) : GenerateCodeUseCase {
  private val contextConfig = loadConfigPort.loadContextConfig()
  private val packageRuleConfigList = loadConfigPort.loadPackageRuleConfigList()
  private val kursTypeRuleConfigList = loadConfigPort.loadKursTypeRuleConfigList()

  override fun generate() {
    loadCollectedMappingFunctionPort
      .loadAll()
      .filter { mappingFunction -> mappingFunction.isReadyToGenerate() }
      .groupBy { it.sourceId }
      .forEach { (_, mappingFunctionList) ->
        with(writeMappingFunctionPort) {
          val context =
            object : WriteMappingFunctionPort.Context {
              override val contextConfig: ContextConfig = this@GenerateCodeService.contextConfig
              override val mappingFunctionList: List<MappingFunction> = mappingFunctionList

              override fun KursTypeId.asKursType(): KursType? =
                loadKursTypePort.loadById(this, contextConfig, packageRuleConfigList, kursTypeRuleConfigList)
            }
          context.write()
        }
      }
  }
}
