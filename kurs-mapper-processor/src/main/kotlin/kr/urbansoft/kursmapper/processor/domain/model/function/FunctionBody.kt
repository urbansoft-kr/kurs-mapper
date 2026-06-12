package kr.urbansoft.kursmapper.processor.domain.model.function

import kr.urbansoft.kursmapper.processor.domain.model.config.ContextConfig
import kr.urbansoft.kursmapper.processor.domain.model.kurstype.KursType
import kr.urbansoft.kursmapper.processor.domain.model.packages.PackageName
import kr.urbansoft.shared.exception.ExceptionMessageSupport
import kr.urbansoft.shared.exception.ExceptionType

@JvmInline
value class FunctionBody private constructor(val value: String) {
  companion object {
    fun from(value: String): FunctionBody = FunctionBody(value = value.trim())

    fun build(context: BuilderContext, block: Builder.() -> Builder): FunctionBody = Builder(context).block().build()

    fun blank(): FunctionBody = FunctionBody("")
  }

  interface BuilderContext {
    val contextConfig: ContextConfig
  }

  class Builder(private val context: BuilderContext) {
    private val stringBuilder = StringBuilder()

    fun brace(block: Builder.() -> Builder): Builder = apply { stringBuilder.append("{ ${build(block)} }") }

    fun brace(parameters: Builder.() -> Builder, block: Builder.() -> Builder): Builder = apply {
      stringBuilder.append("{ ${build(parameters)} -> ${build(block)} }")
    }

    fun build(): FunctionBody = from(stringBuilder.toString())

    fun colon(): Builder = apply { stringBuilder.append(": ") }

    fun comma(): Builder = apply { stringBuilder.append(", ") }

    fun configInterface(): Builder = apply { stringBuilder.append(context.contextConfig.configInterfaceSimpleName.value) }

    fun contextName(): Builder = apply { stringBuilder.append(context.contextConfig.contextName.value) }

    fun create(kursType: KursType): Builder = apply {
      create(kursType.instantiator ?: throw ExceptionMessage.INSTANTIATOR_IS_NOT_FOUND.create(kursType))
    }

    fun create(kursTypeInstantiator: KursTypeInstantiator): Builder = apply { stringBuilder.append(kursTypeInstantiator.asCode()) }

    fun dot(): Builder = apply { stringBuilder.append(".") }

    fun equal(): Builder = apply { stringBuilder.append(" = ") }

    fun functionName(kursType: KursType): Builder = apply { stringBuilder.append(kursType.mappingFunctionName.value) }

    fun invoke(): Builder = apply { stringBuilder.append("()") }

    fun invoke(block: Builder.() -> Builder): Builder = apply { parenthesis(block = block) }

    fun invoke(argumentList: List<Argument>, caller: MappingFunction? = null): Builder = apply {
      stringBuilder.append("(")
      if (caller == null) stringBuilder.append(argumentList.joinToString(separator = ", ") { "${it.name.value} = ${it.name.value}" })
      else if (caller.isPurpose()) {
        val callerArgumentMap = caller.argumentList.associateBy { it.key }
        stringBuilder.append(
          argumentList.joinToString(separator = ", ") {
            "${it.name.value} = ${callerArgumentMap[it.key]?.name?.value ?: "TODO(\"Not Found Required Argument\")"}"
          }
        )
      } else {
        val callerArgumentMap = caller.argumentList.associateBy { it.id }
        stringBuilder.append(
          argumentList.joinToString(separator = ", ") {
            "${it.name.value} = ${callerArgumentMap[it.id]?.name?.value ?: "TODO(\"Not Found Required Argument\")"}"
          }
        )
      }
      stringBuilder.append(")")
    }

    fun it(): Builder = apply { stringBuilder.append("it") }

    fun let(block: Builder.() -> Builder): Builder = apply {
      stringBuilder.append("let ")
      brace(block)
    }

    fun let(parameters: Builder.() -> Builder, block: Builder.() -> Builder): Builder = apply {
      stringBuilder.append("let ")
      brace(parameters = parameters, block = block)
    }

    fun map(block: Builder.() -> Builder): Builder = apply {
      stringBuilder.append("map ")
      brace(block)
    }

    fun map(parameters: Builder.() -> Builder, block: Builder.() -> Builder): Builder = apply {
      stringBuilder.append("map ")
      brace(parameters = parameters, block = block)
    }

    fun nameOf(argument: Argument): Builder = apply { stringBuilder.append(argument.name.value) }

    fun parenthesis(block: Builder.() -> Builder): Builder = apply {
      stringBuilder.append("(${build(block)})")
    }

    fun question(): Builder = apply { stringBuilder.append("?") }

    fun questionColon(): Builder = apply { stringBuilder.append(" ?: ") }

    fun returnKeyword(): Builder = apply { stringBuilder.append("return ") }

    fun source(): Builder = apply { stringBuilder.append(context.contextConfig.mapperSourceVariableName.value) }

    fun text(text: String): Builder = apply { stringBuilder.append(text) }

    fun toSet(): Builder = apply { stringBuilder.append("toSet()") }

    private fun KursTypeInstantiator.asCode(): String {
      return when (kind) {
        KursTypeInstantiator.Kind.PRIMARY_CONSTRUCTOR -> kursTypeId.bareSimpleName().value
        KursTypeInstantiator.Kind.STATIC_FUNCTION -> "${kursTypeId.bareSimpleName().value}.${name.value}"
      }
    }

    private fun build(block: Builder.() -> Builder): String = Builder(context).block().build().value

    enum class ExceptionMessage(override val value: String, override val type: ExceptionType) : ExceptionMessageSupport {
      INSTANTIATOR_IS_NOT_FOUND("instantiator is not found: {}", ExceptionType.NOT_FOUND)
    }
  }

  @ConsistentCopyVisibility
  data class Import private constructor(val packageName: PackageName, val name: String) {
    companion object {
      fun from(packageName: PackageName, name: String): Import = Import(packageName = packageName, name = name.trim())

      fun buildList(context: BuilderContext, block: Builder.() -> Builder): List<Import> = Builder(context).block().build()
    }

    class Builder(private val context: BuilderContext) {
      private val list = mutableListOf<Import>()

      fun build(): List<Import> = list.toList()

      fun nothing(): Builder = this

      fun add(packageName: PackageName, name: String): Builder = apply { list.add(from(packageName, name)) }

      fun add(packageName: String, name: String): Builder = apply { list.add(from(PackageName.from(packageName), name)) }

      fun callee(callee: MappingFunction, source: KursType, target: KursType): Builder = apply {
        list.add(from(source.mapperPackageName, context.contextConfig.contextName.value))
        if (!callee.isUser()) list.add(from(source.mapperPackageName, target.mappingFunctionName.value))
      }

      fun callee(callee: MappingFunction, pair: Pair<KursType, KursType>): Builder = apply {
        callee(callee = callee, source = pair.first, target = pair.second)
      }

      fun configInterface(): Builder = apply {
        list.add(from(context.contextConfig.configInterfacePackageName, context.contextConfig.configInterfaceSimpleName.value))
      }

      fun create(kursType: KursType): Builder = apply { list.add(from(kursType.packageName(), kursType.bareSimpleName().value)) }
    }
  }
}
