package kr.urbansoft.kursmapper.processor.adapter.outbound

import kr.urbansoft.kursmapper.processor.adapter.infra.config.mapper.model.configMapper
import kr.urbansoft.kursmapper.processor.adapter.infra.config.reader.RawConfigReader
import kr.urbansoft.kursmapper.processor.adapter.infra.ksp.model.CheckKSTypeTrait
import kr.urbansoft.kursmapper.processor.adapter.infra.ksp.model.HandleMappedKursTypeId
import kr.urbansoft.kursmapper.processor.adapter.infra.ksp.model.LoadMappedKursTypeId
import kr.urbansoft.kursmapper.processor.adapter.infra.registry.ContextConfigRegistry
import kr.urbansoft.kursmapper.processor.adapter.infra.registry.KursTypeRuleConfigRegistry
import kr.urbansoft.kursmapper.processor.adapter.infra.registry.PackageRuleConfigRegistry
import kr.urbansoft.kursmapper.processor.application.port.outbound.LoadConfigPort
import kr.urbansoft.kursmapper.processor.domain.model.config.ContextConfig
import kr.urbansoft.kursmapper.processor.domain.model.config.KursTypeRuleConfig
import kr.urbansoft.kursmapper.processor.domain.model.config.PackageRuleConfig
import kr.urbansoft.kursmapper.processor.domain.model.function.FunctionBody
import kr.urbansoft.kursmapper.processor.domain.model.function.MappingFunction

class LoadConfigAdapter(
  private val checkKsTypeTrait: CheckKSTypeTrait,
  private val contextConfigRegistry: ContextConfigRegistry,
  private val handleMappedKursTypeId: HandleMappedKursTypeId,
  private val kursTypeRuleConfigRegistry: KursTypeRuleConfigRegistry,
  private val loadMappedKursTypeId: LoadMappedKursTypeId,
  private val packageRuleConfigRegistry: PackageRuleConfigRegistry,
  private val rawConfigReader: RawConfigReader,
) : LoadConfigPort {
  override fun loadContextConfig(): ContextConfig =
    contextConfigRegistry.loadOrNull()
      ?: rawConfigReader.readRawContextConfig().configMapper().asContextConfig().also { contextConfigRegistry.register(it) }

  override fun loadPackageRuleConfigList(): List<PackageRuleConfig> =
    packageRuleConfigRegistry.loadOrNull()
      ?: rawConfigReader
        .readRawPackageRuleConfigList()
        .mapNotNull { it.configMapper().asNullablePackageRuleConfig() }
        .also { packageRuleConfigRegistry.register(it) }

  override fun loadKursTypeRuleConfigList(): List<KursTypeRuleConfig> =
    kursTypeRuleConfigRegistry.loadOrNull()
      ?: rawConfigReader
        .readRawKursTypeRuleConfigList()
        .mapNotNull {
          it
            .configMapper()
            .asNullableKursTypeRuleConfig(
              loadMappedKursTypeId = loadMappedKursTypeId,
              checkKsTypeTrait = checkKsTypeTrait,
              handleMappedKursTypeId = handleMappedKursTypeId,
            )
        }
        .also { kursTypeRuleConfigRegistry.register(it) }

  override fun loadPurposeMappingFunctionList(): List<MappingFunction> =
    rawConfigReader.readRawPurposeMappingFunctionList().map {
      it
        .configMapper()
        .asMappingFunction(
          loadMappedKursTypeId = loadMappedKursTypeId,
          checkKsTypeTrait = checkKsTypeTrait,
          handleMappedKursTypeId = handleMappedKursTypeId,
        )
    }

  override fun loadSandboxMappingFunctionList(): List<MappingFunction> =
    rawConfigReader.readSandboxMappingFunctionList().map {
      it
        .configMapper()
        .asMappingFunction(
          loadMappedKursTypeId = loadMappedKursTypeId,
          checkKsTypeTrait = checkKsTypeTrait,
          handleMappedKursTypeId = handleMappedKursTypeId,
          functionBodyBuilderContext =
            object : FunctionBody.BuilderContext {
              override val contextConfig: ContextConfig = loadContextConfig()
            },
        )
    }
}
