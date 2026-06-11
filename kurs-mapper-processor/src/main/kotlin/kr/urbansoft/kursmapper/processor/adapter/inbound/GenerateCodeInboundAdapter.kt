package kr.urbansoft.kursmapper.processor.adapter.inbound

import kr.urbansoft.kursmapper.processor.application.port.inbound.GenerateCodeUseCase

class GenerateCodeInboundAdapter(private val generateCodeUseCase: GenerateCodeUseCase) {
  fun generate() = generateCodeUseCase.generate()
}
