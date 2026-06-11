package kr.urbansoft.kursmapper.processor.adapter.outbound

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets
import kr.urbansoft.kursmapper.processor.application.port.outbound.WriteGuideToFilePort

class WriteGuideToFileAdapter(private val codeGenerator: CodeGenerator) : WriteGuideToFilePort {
  override fun writeToResolveMappingFunctionGuide(guide: String) {
    write(guide = guide, fileName = "KursMapperGuide")
  }

  override fun writeToPromoteSandbox(guide: String) {
    write(guide = guide, fileName = "promoteSandboxGuide")
  }

  private fun write(guide: String, fileName: String) {
    val file = codeGenerator.createNewFile(dependencies = Dependencies.ALL_FILES, packageName = "", fileName = fileName, extensionName = "txt")
    OutputStreamWriter(file, StandardCharsets.UTF_8).use { writer -> writer.write(guide) }
  }
}
