package kr.urbansoft.kursmapper.processor.domain.service.guide

class EnUSProvider : KoKRProvider() {
  override val addFunction = "Add the following function."
  override val addFunctionAndImplementationIt = "Add the following function to the mapper and implement the mapping logic."
  override val addFunctionToMapper = "Add the following function to the mapper."
  override val argumentLackedReferToBelow =
    "The mapping intent is missing required arguments. Please refer to the guide below and update the arguments."
  override val changeArgumentsToFinal =
    "Update the mapping intent to use the final argument list, or replace it with the function signature shown below."
  override val configInterfaceFQCN = "Configuration Interface FQCN"
  override val createMapper = "Create a mapper."
  override val createMapperFile = "Create a mapper file with the following content."
  override val deleteSandbox = "Remove the following sandbox function. Removing the sandbox mapper class is not required."
  override val explanationOfArgumentLacked =
    "This issue occurs when a mapping intent does not declare all arguments required by the mapping functions it uses."
  override val explanationOfPurposeFunctionName =
    "KursMapper identifies mapping intents by their source and target types, not by their function names. Therefore, the actual function name declared in the configuration interface may differ from the one shown above."
  override val explanationOfSandboxRemoval =
    "Sandbox definitions must be removed when a production mapping function already exists to prevent future confusion."
  override val explanationOfSandboxSignature =
    "KursMapper does not use the class name, variable name, or function name defined in the sandbox declaration. Therefore, the actual declaration in the configuration interface may differ from the one shown above."
  override val fileName = "File Name"
  override val finalArgument = "Final Arguments"
  override val finalArgumentFQCN = "Final Argument FQCNs"
  override val functionArgumentFQCN = "Function Argument FQCNs"
  override val implementationRequiredReferToBelow = "Mapping implementation is required. Please refer to the guide below."
  override val implementHere = "Implement mapping logic here."
  override val important = "Important)"
  override val kursMapperGuideEnd = "===================================================================="
  override val kursMapperGuideStart = "======================== [KursMapper Guide] ========================"
  override val mapperFQCN = "Mapper FQCN"
  override val mapperPackage = "Mapper Package"
  override val openConfigInterfaceFile = "Open the configuration interface file."
  override val pasteHere = "Paste the implementation here instead of '{code: bridge}'."
  override val promotingSandboxReferToBelow =
    "To promote the sandbox function to a production mapping function, please follow the instructions below."
  override val purposeFunction = "Mapping Intent"
  override val rebuildAfterApprovingSandbox = "Rebuild the project after promoting the sandbox function."
  override val rebuildAfterChangingArguments = "After updating the mapping intent, rebuild the project."
  override val rebuildAfterImplementation = "Rebuild the project after completing the implementation."
  override val rebuildAfterRemoval = "Rebuild the project after removing it."
  override val reference = "Note)"
  override val replaceFunctionBodyWithIDEFeature =
    "Use the IDE's Go to Declaration feature to navigate to the sandbox implementation, then copy its implementation and replace the body of the production mapping function."
  override val sandboxOverwrittenReferToBelow = "A sandbox function must be removed. Please refer to the guide below."
  override val sourceFQCN = "Source FQCN"
  override val targetFQCN = "Target FQCN"
  override val whyDeleteSandbox1 =
    "If the original sandbox function is not removed after promotion, a compilation error will be raised along with a removal guide."
  override val whyDeleteSandbox2 = "This is intended to prevent future confusion about which mapping function is currently being applied."
}
