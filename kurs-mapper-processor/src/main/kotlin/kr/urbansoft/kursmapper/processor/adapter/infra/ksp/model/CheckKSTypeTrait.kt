package kr.urbansoft.kursmapper.processor.adapter.infra.ksp.model

import com.google.devtools.ksp.symbol.KSType
import kr.urbansoft.kursmapper.processor.domain.model.kurstype.KursType

@JvmInline
value class CheckKSTypeTrait(private val value: KSType.() -> Set<KursType.Trait>) {
  operator fun invoke(type: KSType): Set<KursType.Trait> = type.value()
}
