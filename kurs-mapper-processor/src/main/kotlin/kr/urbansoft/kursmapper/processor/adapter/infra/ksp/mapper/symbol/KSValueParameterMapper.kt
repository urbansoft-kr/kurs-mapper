package kr.urbansoft.kursmapper.processor.adapter.infra.ksp.mapper.symbol

import com.google.devtools.ksp.symbol.KSValueParameter
import kr.urbansoft.kursmapper.processor.adapter.infra.ksp.model.RawArgument

fun KSValueParameter.kspMapper() = KSValueParameterMapper(this)

@JvmInline
value class KSValueParameterMapper(private val source: KSValueParameter) {
  fun asNullableRawArgument(): RawArgument? {
    val name = source.name?.asString() ?: return null
    val ksType = source.type.resolve()
    return RawArgument(name = name, type = ksType)
  }
}
