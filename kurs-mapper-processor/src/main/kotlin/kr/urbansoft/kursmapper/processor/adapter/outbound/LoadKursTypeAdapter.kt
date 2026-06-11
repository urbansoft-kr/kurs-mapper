package kr.urbansoft.kursmapper.processor.adapter.outbound

import com.google.devtools.ksp.isConstructor
import com.google.devtools.ksp.processing.Resolver
import kr.urbansoft.kursmapper.processor.adapter.infra.ksp.extension.getInstantiatorOrNull
import kr.urbansoft.kursmapper.processor.adapter.infra.ksp.extension.getName
import kr.urbansoft.kursmapper.processor.adapter.infra.ksp.extension.getNameOrNull
import kr.urbansoft.kursmapper.processor.adapter.infra.ksp.mapper.symbol.kspMapper
import kr.urbansoft.kursmapper.processor.adapter.infra.ksp.model.CheckKSTypeTrait
import kr.urbansoft.kursmapper.processor.adapter.infra.ksp.model.HandleMappedKursTypeId
import kr.urbansoft.kursmapper.processor.adapter.infra.ksp.model.LoadMappedKursTypeId
import kr.urbansoft.kursmapper.processor.adapter.infra.registry.KSTypeRegistry
import kr.urbansoft.kursmapper.processor.adapter.infra.registry.KursTypeRegistry
import kr.urbansoft.kursmapper.processor.application.port.outbound.LoadKursTypePort
import kr.urbansoft.kursmapper.processor.domain.model.config.ContextConfig
import kr.urbansoft.kursmapper.processor.domain.model.config.KursTypeRuleConfig
import kr.urbansoft.kursmapper.processor.domain.model.config.PackageRuleConfig
import kr.urbansoft.kursmapper.processor.domain.model.function.Argument
import kr.urbansoft.kursmapper.processor.domain.model.function.ArgumentName
import kr.urbansoft.kursmapper.processor.domain.model.function.FunctionName
import kr.urbansoft.kursmapper.processor.domain.model.function.PrimaryConstructor
import kr.urbansoft.kursmapper.processor.domain.model.function.StaticFunction
import kr.urbansoft.kursmapper.processor.domain.model.kurstype.KursType
import kr.urbansoft.kursmapper.processor.domain.model.kurstype.KursTypeId

class LoadKursTypeAdapter(
  private val checkKsTypeTrait: CheckKSTypeTrait,
  private val handleMappedKursTypeId: HandleMappedKursTypeId,
  private val kspResolver: Resolver,
  private val ksTypeRegistry: KSTypeRegistry,
  private val kursTypeRegistry: KursTypeRegistry,
  private val loadMappedKursTypeId: LoadMappedKursTypeId,
) : LoadKursTypePort {
  override fun loadById(
    id: KursTypeId,
    contextConfig: ContextConfig,
    packageRuleConfigList: List<PackageRuleConfig>,
    kursTypeRuleConfigList: List<KursTypeRuleConfig>,
  ): KursType? {
    return kursTypeRegistry.getOrNull(id)
      ?: let {
        val ksType = ksTypeRegistry.getOrNull(id) ?: return null
        val instantiator =
          ksType.getInstantiatorOrNull(rootPackageName = contextConfig.rootPackageName, kspResolver = kspResolver)?.run {
            val argumentList = parameters.map { parameter ->
              val parameterName = parameter.getNameOrNull()?.let { ArgumentName.from(it) } ?: return@run null
              val parameterKsType = parameter.type.resolve()
              val parameterKursTypeId =
                parameterKsType
                  .kspMapper()
                  .asKursTypeId(
                    loadMappedKursTypeId = loadMappedKursTypeId,
                    checkKsTypeTrait = checkKsTypeTrait,
                    handleMappedKursTypeId = handleMappedKursTypeId,
                  )
              Argument.create(name = parameterName, typeId = parameterKursTypeId)
            }
            if (isConstructor()) PrimaryConstructor.from(kursTypeId = id, argumentList = argumentList)
            else StaticFunction.from(kursTypeId = id, name = FunctionName.from(getName()), argumentList = argumentList)
          }
        return KursType.from(
            id = id,
            instantiator = instantiator,
            contextConfig = contextConfig,
            packageRuleConfigList = packageRuleConfigList,
            kursTypeRuleConfigList = kursTypeRuleConfigList,
          )
          .also { kursTypeRegistry.put(it) }
      }
  }
}
