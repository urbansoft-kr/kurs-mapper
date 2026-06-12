package kr.urbansoft.kursmapper.processor.domain.model.kurstype

import kr.urbansoft.kursmapper.processor.domain.model.packages.PackageName
import kr.urbansoft.shared.exception.ExceptionMessageSupport
import kr.urbansoft.shared.exception.ExceptionType
import kr.urbansoft.shared.validation.validate

@ConsistentCopyVisibility
data class KursTypeId private constructor(val name: KursTypeName, val bareId: BareKursTypeId, val genericIdList: List<KursTypeId?>) {
  private val notNullName: KursTypeName = name.asNotNull()

  init {
    if (name.containsGeneric()) validate(genericIdList.isNotEmpty(), { ExceptionMessage.GENERIC_ID_LIST_IS_EMPTY })
    else validate(genericIdList.isEmpty(), { ExceptionMessage.GENERIC_ID_LIST_IS_NOT_EMPTY })
  }

  companion object {
    fun from(name: KursTypeName, bareId: BareKursTypeId, genericKursTypeIdList: List<KursTypeId?> = emptyList()): KursTypeId {
      validate(
        name.removeGeneric() == bareId.name,
        { ExceptionMessage.NAME_MUST_STARTS_WITH_RAW_NAME },
        name.value,
        bareId.name.value,
      )
      return KursTypeId(name = name, bareId = bareId, genericIdList = genericKursTypeIdList.toList())
    }

    @Deprecated("Use from(name: KursTypeName, bareId: BareKursTypeId, genericKursTypeIdList: List<KursTypeId?> = emptyList()) instead")
    fun from(
      name: KursTypeName,
      qualifiedName: KursTypeQualifiedName,
      packageName: PackageName,
      simpleName: KursTypeSimpleName,
      nullability: KursType.Nullability,
      traitSet: Set<KursType.Trait>,
      genericKursTypeIdList: List<KursTypeId?> = emptyList(),
    ): KursTypeId {
      return KursTypeId(
        name = name,
        bareId =
          BareKursTypeId.from(
            name = name.removeGeneric(),
            qualifiedName = qualifiedName,
            packageName = packageName,
            simpleName = simpleName,
            nullability = nullability,
            traitSet = traitSet.toSet(),
          ),
        genericIdList = genericKursTypeIdList.toList(),
      )
    }
  }

  fun symbolName(): SymbolName =
    SymbolName.from(
      genericIdList.filterNotNull().map { it.symbolName().value }.fold("") { acc, symbolNameValue -> acc + symbolNameValue } +
        bareId.symbolName().value
    )

  fun qualifiedName(): KursTypeQualifiedName = bareId.qualifiedName

  fun packageName(): PackageName = bareId.packageName

  fun simpleName(): KursTypeSimpleName {
    return KursTypeSimpleName.from(
      buildString {
        append(bareId.simpleName.value)
        if (genericIdList.isNotEmpty()) {
          append("<")
          append(genericIdList.joinToString(", ") { it?.simpleName()?.value ?: "*" })
          append(">")
        }
      }
    )
  }

  fun bareSimpleName(): KursTypeSimpleName = bareId.simpleName

  fun nullability(): KursType.Nullability = bareId.nullability

  fun hasAllTraits(traits: Collection<KursType.Trait>): Boolean = bareId.hasAllTraits(traits)

  fun hasAllTraits(vararg traits: KursType.Trait): Boolean = bareId.hasAllTraits(*traits)

  fun hasAnyTraits(traits: Collection<KursType.Trait>): Boolean = bareId.hasAnyTraits(traits)

  fun hasAnyTraits(vararg traits: KursType.Trait): Boolean = bareId.hasAnyTraits(*traits)

  fun hasTrait(trait: KursType.Trait): Boolean = bareId.hasTrait(trait)

  fun isSameExceptNullability(other: KursTypeId): Boolean = notNullName == other.notNullName

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other == null || this::class != other::class) return false
    other as KursTypeId
    return name == other.name
  }

  override fun hashCode(): Int {
    return name.hashCode()
  }

  enum class ExceptionMessage(override val value: String, override val type: ExceptionType) : ExceptionMessageSupport {
    GENERIC_ID_LIST_IS_EMPTY("genericIdList is empty", ExceptionType.BAD_REQUEST),
    GENERIC_ID_LIST_IS_NOT_EMPTY("genericIdList is not empty", ExceptionType.BAD_REQUEST),
    NAME_MUST_STARTS_WITH_RAW_NAME("name must starts with raw name: name: {}, rawName: {}", ExceptionType.BAD_REQUEST),
  }
}
