package kr.urbansoft.kursmapper.processor.adapter.infra.registry

import kr.urbansoft.kursmapper.processor.domain.model.config.ContextConfig

class ContextConfigRegistry private constructor() {
  private var value: ContextConfig? = null

  companion object {
    fun create(): ContextConfigRegistry = ContextConfigRegistry()
  }

  fun load(): ContextConfig = loadOrNull() ?: error("ContextConfig is not registered.")

  fun loadOrNull(): ContextConfig? = value

  fun register(value: ContextConfig): ContextConfigRegistry = apply { this.value = value }
}
