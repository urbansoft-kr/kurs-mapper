package kr.urbansoft.kursmapper.processor.adapter.infra.ksp.mapper.symbol

import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSTypeArgument
import kr.urbansoft.kursmapper.processor.adapter.infra.ksp.model.CheckKSTypeTrait
import kr.urbansoft.kursmapper.processor.adapter.infra.ksp.model.HandleMappedKursTypeId
import kr.urbansoft.kursmapper.processor.adapter.infra.ksp.model.LoadMappedKursTypeId
import kr.urbansoft.kursmapper.processor.domain.model.kurstype.KursTypeId

fun KSTypeArgument.kspMapper() = KSTypeArgumentMapper(this)

@JvmInline
value class KSTypeArgumentMapper(private val source: KSTypeArgument) {
  fun asNullableKSType(): KSType? = source.type?.resolve()

  fun asNullableKursTypeId(
    loadMappedKursTypeId: LoadMappedKursTypeId,
    checkKsTypeTrait: CheckKSTypeTrait,
    handleMappedKursTypeId: HandleMappedKursTypeId,
  ): KursTypeId? =
    source
      .kspMapper()
      .asNullableKSType()
      ?.kspMapper()
      ?.asKursTypeId(
        loadMappedKursTypeId = loadMappedKursTypeId,
        checkKsTypeTrait = checkKsTypeTrait,
        handleMappedKursTypeId = handleMappedKursTypeId,
      )
}
