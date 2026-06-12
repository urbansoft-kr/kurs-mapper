package kr.urbansoft.kursmapper.processor.domain.model.kurstype

import kr.urbansoft.kursmapper.processor.domain.model.config.ContextConfig
import kr.urbansoft.kursmapper.processor.domain.model.config.KursTypeRuleConfig
import kr.urbansoft.kursmapper.processor.domain.model.config.PackageRuleConfig
import kr.urbansoft.kursmapper.processor.domain.model.config.RuleConfig
import kr.urbansoft.kursmapper.processor.domain.model.function.Argument
import kr.urbansoft.kursmapper.processor.domain.model.function.FunctionName
import kr.urbansoft.kursmapper.processor.domain.model.function.KursTypeInstantiator
import kr.urbansoft.kursmapper.processor.domain.model.mapper.MapperName
import kr.urbansoft.kursmapper.processor.domain.model.packages.PackageName

@ConsistentCopyVisibility
data class KursType
private constructor(
  val id: KursTypeId,
  val instantiator: KursTypeInstantiator?,
  val mapperPackageName: PackageName,
  val mapperName: MapperName,
  val mappingFunctionName: FunctionName,
) {
  val kind: Kind =
    instantiator?.let {
      when (it.argumentList.size) {
        0 -> Kind.BLACKBOX
        1 -> Kind.SINGLE
        else -> Kind.MULTIPLE
      }
    } ?: Kind.BLACKBOX

  companion object {
    fun from(
      id: KursTypeId,
      instantiator: KursTypeInstantiator?,
      contextConfig: ContextConfig,
      packageRuleConfigList: List<PackageRuleConfig>,
      kursTypeRuleConfigList: List<KursTypeRuleConfig>,
    ): KursType {
      val kursTypeRuleConfig = kursTypeRuleConfigList.firstOrNull { it.kursTypeId == id }?.rule
      val packageRuleConfig =
        packageRuleConfigList.filter { id.packageName() in it.packageName }.maxByOrNull { it.packageName.length() }?.rule
      val ruleConfig = kursTypeRuleConfig ?: packageRuleConfig ?: RuleConfig.default()
      return KursType(
        id = id,
        instantiator = instantiator,
        mapperPackageName = ruleConfig.mapperPackageName(kursTypeId = id, contextConfig = contextConfig),
        mapperName = ruleConfig.mapperName(kursTypeId = id, contextConfig = contextConfig),
        mappingFunctionName = ruleConfig.mappingFunctionName(kursTypeId = id, contextConfig = contextConfig),
      )
    }
  }

  fun genericIdList(): List<KursTypeId?> = id.genericIdList

  fun isExternal(): Boolean = id.hasTrait(Trait.EXTERNAL)

  fun isInternal(): Boolean = id.hasTrait(Trait.INTERNAL)

  fun isList(): Boolean = id.hasTrait(Trait.LIST)

  fun isListOrSet(): Boolean = id.hasAnyTraits(Trait.LIST, Trait.SET)

  fun isMap(): Boolean = id.hasTrait(Trait.MAP)

  fun isNotNull(): Boolean = nullability() == Nullability.NOT_NULL

  fun isNullable(): Boolean = nullability() == Nullability.NULLABLE

  fun isSameExceptNullability(other: KursType): Boolean = id.isSameExceptNullability(other.id)

  fun isSameExceptNullabilityAndResolvable(target: KursType): Boolean =
    this.isSameExceptNullability(target) && isNotNull() && target.isNullable()

  fun isSet(): Boolean = id.hasTrait(Trait.SET)

  fun isStringAnyMap(): Boolean {
    if (!isMap()) return false
    val genericIdList = genericIdList()
    if (genericIdList.size != 2) return false
    val maybeStringId = genericIdList[0] ?: return false
    val maybeAnyId = genericIdList[1] ?: return false
    return maybeStringId.hasTrait(Trait.STRING) && maybeAnyId.hasTrait(Trait.ANY) && maybeAnyId.nullability() == Nullability.NOT_NULL
  }

  fun isStringNullableAnyMap(): Boolean {
    if (!isMap()) return false
    val genericIdList = genericIdList()
    if (genericIdList.size != 2) return false
    val maybeStringId = genericIdList[0] ?: return false
    val maybeAnyId = genericIdList[1] ?: return false
    return maybeStringId.hasTrait(Trait.STRING) && maybeAnyId.hasTrait(Trait.ANY) && maybeAnyId.nullability() == Nullability.NULLABLE
  }

  fun mapperQualifiedName(): String = "${mapperPackageName.value}.${mapperName.value}"

  fun nullability(): Nullability = id.nullability()

  fun packageName(): PackageName = id.packageName()

  fun parameterList(): List<Argument> = instantiator?.argumentList ?: emptyList()

  fun bareSimpleName(): KursTypeSimpleName = id.bareSimpleName()

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other == null || this::class != other::class) return false
    other as KursType
    return id == other.id
  }

  override fun hashCode(): Int {
    return id.hashCode()
  }

  enum class Kind {
    BLACKBOX,
    SINGLE,
    MULTIPLE,
  }

  enum class Nullability {
    NOT_NULL,
    NULLABLE,
  }

  enum class Trait(private val messageToDev: String) {
    ANY("This type is exactly kotlin.Any."),
    EXTERNAL("This type is outside the project's root package."),
    INTERNAL("This type is inside the project's root package."),
    LIST("This type is assignable to kotlin.collections.List."),
    MAP("This type is assignable to kotlin.collections.Map."),
    SET("This type is assignable to kotlin.collections.Set."),
    STRING("This type is exactly kotlin.String."),
  }
}
