package kr.urbansoft.kursmapper.processor.domain.service.guide

import kr.urbansoft.kursmapper.annotation.GuideLanguage

interface LanguageProvider {
  val addFunction: String
  val addFunctionAndImplementationIt: String
  val addFunctionToMapper: String
  val promotingSandboxReferToBelow: String
  val argumentLackedReferToBelow: String
  val changeArgumentsToFinal: String
  val configInterfaceFQCN: String
  val createMapper: String
  val createMapperFile: String
  val deleteSandbox: String
  val explanationOfArgumentLacked: String
  val explanationOfPurposeFunctionName: String
  val explanationOfSandboxRemoval: String
  val explanationOfSandboxSignature: String
  val fileName: String
  val finalArgument: String
  val finalArgumentFQCN: String
  val functionArgumentFQCN: String
  val implementationRequiredReferToBelow: String
  val implementHere: String
  val important: String
  val kursMapperGuideEnd: String
  val kursMapperGuideStart: String
  val mapperFQCN: String
  val mapperPackage: String
  val openConfigInterfaceFile: String
  val pasteHere: String
  val purposeFunction: String
  val rebuildAfterApprovingSandbox: String
  val rebuildAfterChangingArguments: String
  val rebuildAfterImplementation: String
  val rebuildAfterRemoval: String
  val reference: String
  val replaceFunctionBodyWithIDEFeature: String
  val sandboxOverwrittenReferToBelow: String
  val sourceFQCN: String
  val targetFQCN: String
  val whyDeleteSandbox1: String
  val whyDeleteSandbox2: String

  companion object {
    fun create(guideLanguage: GuideLanguage): LanguageProvider =
      when (guideLanguage) {
        GuideLanguage.EN_US -> EnUSProvider()
        GuideLanguage.KO_KR -> KoKRProvider()
      }
  }
}
