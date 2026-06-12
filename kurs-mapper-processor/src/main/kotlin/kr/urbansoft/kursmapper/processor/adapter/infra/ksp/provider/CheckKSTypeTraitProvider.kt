package kr.urbansoft.kursmapper.processor.adapter.infra.ksp.provider

import com.google.devtools.ksp.getClassDeclarationByName
import com.google.devtools.ksp.processing.Resolver
import kr.urbansoft.kursmapper.processor.adapter.infra.ksp.extension.getPackageName
import kr.urbansoft.kursmapper.processor.adapter.infra.ksp.model.CheckKSTypeTrait
import kr.urbansoft.kursmapper.processor.adapter.infra.registry.ContextConfigRegistry
import kr.urbansoft.kursmapper.processor.domain.model.kurstype.KursType

class CheckKSTypeTraitProvider(
  kspResolver: Resolver,
  private val contextConfigRegistry: ContextConfigRegistry,
) {
  private val listKsType =
    kspResolver.getClassDeclarationByName("kotlin.collections.List")?.asStarProjectedType() ?: error("unreachable error")
  private val mapKsType =
    kspResolver.getClassDeclarationByName("kotlin.collections.Map")?.asStarProjectedType() ?: error("unreachable error")
  private val setKsType =
    kspResolver.getClassDeclarationByName("kotlin.collections.Set")?.asStarProjectedType() ?: error("unreachable error")

  fun get(): CheckKSTypeTrait {
    return CheckKSTypeTrait {
      val contextConfig = contextConfigRegistry.load()
      val rootPackageName = contextConfig.rootPackageName
      val notNullKsType = makeNotNullable()
      setOfNotNull(
        if (declaration.qualifiedName?.asString() == "kotlin.Any") KursType.Trait.ANY else null,
        if (notNullKsType.getPackageName() in rootPackageName) KursType.Trait.INTERNAL else KursType.Trait.EXTERNAL,
        if (listKsType.isAssignableFrom(notNullKsType)) KursType.Trait.LIST else null,
        if (mapKsType.isAssignableFrom(notNullKsType)) KursType.Trait.MAP else null,
        if (setKsType.isAssignableFrom(notNullKsType)) KursType.Trait.SET else null,
        if (declaration.qualifiedName?.asString() == "kotlin.String") KursType.Trait.STRING else null,
      )
    }
  }
}
