package kr.urbansoft.kursmapper.processor.application.port.outbound

interface RaiseCompileErrorPort {
  fun raise(guide: String)
}
