package kr.urbansoft.kursmapper.processor.adapter.outbound

import kr.urbansoft.kursmapper.processor.adapter.infra.ksp.mapper.symbol.kspMapper
import kr.urbansoft.kursmapper.processor.adapter.infra.ksp.model.CheckKSTypeTrait
import kr.urbansoft.kursmapper.processor.adapter.infra.ksp.model.HandleMappedKursTypeId
import kr.urbansoft.kursmapper.processor.adapter.infra.ksp.model.LoadMappedKursTypeId
import kr.urbansoft.kursmapper.processor.adapter.infra.ksp.resolver.KursTypeNameResolver
import kr.urbansoft.kursmapper.processor.adapter.infra.registry.KSTypeRegistry
import kr.urbansoft.kursmapper.processor.adapter.infra.registry.KursTypeIdRegistry
import kr.urbansoft.kursmapper.processor.application.port.outbound.LoadKursTypeIdPort
import kr.urbansoft.kursmapper.processor.domain.model.kurstype.KursTypeId
import kr.urbansoft.kursmapper.processor.domain.model.kurstype.KursTypeName
import kr.urbansoft.kursmapper.processor.shared.exception.ExceptionMessageSupport
import kr.urbansoft.kursmapper.processor.shared.exception.ExceptionType

class LoadKursTypeIdAdapter(
  private val checkKsTypeTrait: CheckKSTypeTrait,
  private val handleMappedKursTypeId: HandleMappedKursTypeId,
  private val ksTypeRegistry: KSTypeRegistry,
  private val kursTypeIdRegistry: KursTypeIdRegistry,
  private val kursTypeNameResolver: KursTypeNameResolver,
  private val loadMappedKursTypeId: LoadMappedKursTypeId,
) : LoadKursTypeIdPort {
  override fun asNotNull(kursTypeId: KursTypeId): KursTypeId {
    val ksType = ksTypeRegistry.getOrNull(kursTypeId) ?: throw ExceptionMessage.KS_TYPE_IS_NOT_FOUND.create(kursTypeId.name.value)
    return ksType
      .makeNotNullable()
      .kspMapper()
      .asKursTypeId(loadMappedKursTypeId = loadMappedKursTypeId, checkKsTypeTrait = checkKsTypeTrait, handleMappedKursTypeId = handleMappedKursTypeId)
  }

  override fun asNullable(kursTypeId: KursTypeId): KursTypeId {
    val ksType = ksTypeRegistry.getOrNull(kursTypeId) ?: throw ExceptionMessage.KS_TYPE_IS_NOT_FOUND.create(kursTypeId.name.value)
    return ksType
      .makeNullable()
      .kspMapper()
      .asKursTypeId(loadMappedKursTypeId = loadMappedKursTypeId, checkKsTypeTrait = checkKsTypeTrait, handleMappedKursTypeId = handleMappedKursTypeId)
  }

  override fun loadByName(name: KursTypeName): KursTypeId =
    kursTypeIdRegistry.getOrNull(name)
      ?: run {
        kursTypeNameResolver
          .resolve(name)
          .kspMapper()
          .asKursTypeId(
            loadMappedKursTypeId = loadMappedKursTypeId,
            checkKsTypeTrait = checkKsTypeTrait,
            handleMappedKursTypeId = handleMappedKursTypeId,
          )
      }

  enum class ExceptionMessage(override val value: String, override val type: ExceptionType) : ExceptionMessageSupport {
    KS_TYPE_IS_NOT_FOUND("KSType is not found: {}", ExceptionType.NOT_FOUND)
  }
}
