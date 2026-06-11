package kr.urbansoft.kursmapper.processor.domain.model.kurstype

import kr.urbansoft.kursmapper.processor.domain.model.packages.PackageName
import kr.urbansoft.kursmapper.processor.shared.exception.ExceptionMessageSupport
import kr.urbansoft.kursmapper.processor.shared.exception.ExceptionType
import kr.urbansoft.kursmapper.processor.shared.validation.validate

@ConsistentCopyVisibility
data class BareKursTypeId
private constructor(
  val name: KursTypeName,
  val qualifiedName: KursTypeQualifiedName,
  val packageName: PackageName,
  val simpleName: KursTypeSimpleName,
  val nullability: KursType.Nullability,
  val traitSet: Set<KursType.Trait>,
) {
  init {
    validate(name.containsNoGeneric(), { ExceptionMessage.NAME_MUST_NOT_CONTAIN_GENERIC }, name.value)
    validate(name.isNotKotlinUnit(), { ExceptionMessage.NAME_MUST_NOT_BE_KOTLIN_UNIT })
    validate(name.isNotKotlinNothing(), { ExceptionMessage.NAME_MUST_NOT_BE_KOTLIN_NOTHING })
  }

  companion object {
    fun from(
      name: KursTypeName,
      qualifiedName: KursTypeQualifiedName,
      packageName: PackageName,
      simpleName: KursTypeSimpleName,
      nullability: KursType.Nullability,
      traitSet: Set<KursType.Trait>,
    ): BareKursTypeId =
      BareKursTypeId(
        name = name,
        qualifiedName = qualifiedName,
        packageName = packageName,
        simpleName = simpleName,
        nullability = nullability,
        traitSet = traitSet.toSet(),
      )
  }

  fun symbolName(): SymbolName =
    SymbolName.from(
      when (nullability) {
        KursType.Nullability.NULLABLE -> "Nullable"
        KursType.Nullability.NOT_NULL -> ""
      } +
        simpleName.value
          .replace(".", "") // remove invalid character
          .replace("<", "") // remove invalid character
          .replace(">", "") // remove invalid character
          .replace(",", "") // remove invalid character
          .replace(" ", "") // remove invalid character
          .replace("?", "") // remove invalid character
          .replace("!", "") // remove invalid character
    )

  fun hasTrait(trait: KursType.Trait): Boolean = trait in traitSet

  fun hasAllTraits(vararg traits: KursType.Trait): Boolean = traits.all { hasTrait(it) }

  fun hasAllTraits(traits: Collection<KursType.Trait>): Boolean = traits.all { hasTrait(it) }

  fun hasAnyTraits(vararg traits: KursType.Trait): Boolean = traits.any { hasTrait(it) }

  fun hasAnyTraits(traits: Collection<KursType.Trait>): Boolean = traits.any { hasTrait(it) }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other == null || this::class != other::class) return false
    other as BareKursTypeId
    return name == other.name
  }

  override fun hashCode(): Int {
    return name.hashCode()
  }

  enum class ExceptionMessage(override val value: String, override val type: ExceptionType) : ExceptionMessageSupport {
    NAME_MUST_NOT_CONTAIN_GENERIC("name must not contain generic: name: {}", ExceptionType.BAD_REQUEST),
    NAME_MUST_NOT_BE_KOTLIN_UNIT("name must not be kotlin.Unit", ExceptionType.BAD_REQUEST),
    NAME_MUST_NOT_BE_KOTLIN_NOTHING("name must not be kotlin.Nothing", ExceptionType.BAD_REQUEST),
  }
}
