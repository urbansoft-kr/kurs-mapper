package kr.urbansoft.kursmapper.processor.adapter.infra.ksp.resolver

import com.google.devtools.ksp.getClassDeclarationByName
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.Variance
import kr.urbansoft.kursmapper.processor.domain.model.kurstype.KursTypeName

class KursTypeNameResolver(private val kspResolver: Resolver) {
  fun resolve(name: KursTypeName): KSType {
    val bareKSType =
      name.removeGeneric().asNotNull().let { kspResolver.getClassDeclarationByName(it.value) } ?: error("KSType is not found: ${name.value}")

    if (name.containsNoGeneric()) return bareKSType.asStarProjectedType().run { if (name.isNullable()) makeNullable() else makeNotNullable() }

    val typeArgumentList =
      name.genericList().map {
        if (it.isStar()) error("Star variance is not supported: ${it.value}")
        if (it.containsCovariant()) error("Covariant variance is not supported: ${it.value}")
        if (it.containsContravariant()) error("Contravariant variance is not supported: ${it.value}")

        val ksType = resolve(it)
        val ksTypeReference = kspResolver.createKSTypeReferenceFromKSType(ksType)
        kspResolver.getTypeArgument(ksTypeReference, Variance.INVARIANT)
      }
    return bareKSType.asType(typeArgumentList).run { if (name.isNullable()) makeNullable() else makeNotNullable() }
  }
}
