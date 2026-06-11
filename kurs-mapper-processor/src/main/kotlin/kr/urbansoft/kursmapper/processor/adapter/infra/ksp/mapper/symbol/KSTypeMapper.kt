package kr.urbansoft.kursmapper.processor.adapter.infra.ksp.mapper.symbol

import com.google.devtools.ksp.closestClassDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.Nullability
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName
import kr.urbansoft.kursmapper.processor.adapter.infra.ksp.extension.getPackageName
import kr.urbansoft.kursmapper.processor.adapter.infra.ksp.extension.getQualifiedNameOrNull
import kr.urbansoft.kursmapper.processor.adapter.infra.ksp.extension.getSimpleName
import kr.urbansoft.kursmapper.processor.adapter.infra.ksp.model.CheckKSTypeTrait
import kr.urbansoft.kursmapper.processor.adapter.infra.ksp.model.HandleMappedKursTypeId
import kr.urbansoft.kursmapper.processor.adapter.infra.ksp.model.LoadMappedKursTypeId
import kr.urbansoft.kursmapper.processor.domain.model.kurstype.BareKursTypeId
import kr.urbansoft.kursmapper.processor.domain.model.kurstype.KursType
import kr.urbansoft.kursmapper.processor.domain.model.kurstype.KursTypeId
import kr.urbansoft.kursmapper.processor.domain.model.kurstype.KursTypeName
import kr.urbansoft.kursmapper.processor.domain.model.kurstype.KursTypeQualifiedName
import kr.urbansoft.kursmapper.processor.domain.model.kurstype.KursTypeSimpleName
import kr.urbansoft.kursmapper.processor.domain.model.packages.PackageName

fun KSType.kspMapper() = KSTypeMapper(this)

@JvmInline
value class KSTypeMapper(private val source: KSType) {
  fun asKursTypeId(
    loadMappedKursTypeId: LoadMappedKursTypeId,
    checkKsTypeTrait: CheckKSTypeTrait,
    handleMappedKursTypeId: HandleMappedKursTypeId,
  ): KursTypeId {
    val typeName = KursTypeName.from(source.toTypeName().toString())
    return loadMappedKursTypeId(typeName)
      ?: KursTypeId.from(
          name = typeName,
          bareId =
            with(source.declaration.closestClassDeclaration() ?: error("closestClassDeclaration is null")) {
              val rawClassName = toClassName()
              val nullability =
                when (source.nullability) {
                  Nullability.NOT_NULL -> KursType.Nullability.NOT_NULL
                  Nullability.NULLABLE,
                  Nullability.PLATFORM -> KursType.Nullability.NULLABLE
                }
              val rawName = KursTypeName.from(rawClassName.toString(), nullability)
              val qualifiedName = KursTypeQualifiedName.from(getQualifiedNameOrNull() ?: error("qualifiedName is null"))
              val packageName = PackageName.from(getPackageName())
              val simpleName = KursTypeSimpleName.from(getSimpleName())
              BareKursTypeId.from(
                name = rawName,
                qualifiedName = qualifiedName,
                packageName = packageName,
                simpleName = simpleName,
                nullability = nullability,
                traitSet = checkKsTypeTrait(source),
              )
            },
          genericKursTypeIdList =
            source.arguments.map {
              it
                .kspMapper()
                .asNullableKursTypeId(
                  loadMappedKursTypeId = loadMappedKursTypeId,
                  checkKsTypeTrait = checkKsTypeTrait,
                  handleMappedKursTypeId = handleMappedKursTypeId,
                )
            },
        )
        .also { handleMappedKursTypeId(source, it) }
  }
}
