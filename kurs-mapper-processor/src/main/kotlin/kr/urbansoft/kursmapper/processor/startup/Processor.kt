package kr.urbansoft.kursmapper.processor.startup

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSVisitorVoid
import kr.urbansoft.kursmapper.processor.adapter.inbound.CollectMappingFunctionInboundAdapter
import kr.urbansoft.kursmapper.processor.adapter.inbound.GenerateCodeInboundAdapter
import kr.urbansoft.kursmapper.processor.adapter.inbound.ReportUnresolvedMappingFunctionInboundAdapter
import kr.urbansoft.kursmapper.processor.adapter.inbound.ResolveMappingFunctionInboundAdapter
import kr.urbansoft.kursmapper.processor.adapter.infra.config.reader.RawConfigReader
import kr.urbansoft.kursmapper.processor.adapter.infra.ksp.extension.getAnnotationArgumentValueAsTrimmedStringOrNull
import kr.urbansoft.kursmapper.processor.adapter.infra.ksp.extension.getValidAnnotatedKSClassDeclarationList
import kr.urbansoft.kursmapper.processor.adapter.infra.ksp.extension.validateAllInterface
import kr.urbansoft.kursmapper.processor.adapter.infra.ksp.extension.validateUnique
import kr.urbansoft.kursmapper.processor.adapter.infra.ksp.provider.CheckKSTypeTraitProvider
import kr.urbansoft.kursmapper.processor.adapter.infra.ksp.provider.HandleMappedKursTypeIdProvider
import kr.urbansoft.kursmapper.processor.adapter.infra.ksp.provider.LoadMappedKursTypeIdProvider
import kr.urbansoft.kursmapper.processor.adapter.infra.ksp.reader.RawUserDefinedMappingFunctionReader
import kr.urbansoft.kursmapper.processor.adapter.infra.ksp.resolver.KursTypeNameResolver
import kr.urbansoft.kursmapper.processor.adapter.infra.registry.CollectedMappingFunctionRegistry
import kr.urbansoft.kursmapper.processor.adapter.infra.registry.ContextConfigRegistry
import kr.urbansoft.kursmapper.processor.adapter.infra.registry.KSTypeRegistry
import kr.urbansoft.kursmapper.processor.adapter.infra.registry.KursTypeIdRegistry
import kr.urbansoft.kursmapper.processor.adapter.infra.registry.KursTypeRegistry
import kr.urbansoft.kursmapper.processor.adapter.infra.registry.KursTypeRuleConfigRegistry
import kr.urbansoft.kursmapper.processor.adapter.infra.registry.PackageRuleConfigRegistry
import kr.urbansoft.kursmapper.processor.adapter.infra.registry.TypeNameRegistry
import kr.urbansoft.kursmapper.processor.adapter.outbound.LoadCollectedMappingFunctionAdapter
import kr.urbansoft.kursmapper.processor.adapter.outbound.LoadConfigAdapter
import kr.urbansoft.kursmapper.processor.adapter.outbound.LoadKursTypeAdapter
import kr.urbansoft.kursmapper.processor.adapter.outbound.LoadKursTypeIdAdapter
import kr.urbansoft.kursmapper.processor.adapter.outbound.LoadUserDefinedMappingFunctionAdapter
import kr.urbansoft.kursmapper.processor.adapter.outbound.RaiseCompileErrorAdapter
import kr.urbansoft.kursmapper.processor.adapter.outbound.SaveCollectedMappingFunctionAdapter
import kr.urbansoft.kursmapper.processor.adapter.outbound.WriteGuideToFileAdapter
import kr.urbansoft.kursmapper.processor.adapter.outbound.WriteMappingFunctionAdapter
import kr.urbansoft.kursmapper.processor.application.port.inbound.CollectMappingFunctionUseCase
import kr.urbansoft.kursmapper.processor.application.port.inbound.GenerateCodeUseCase
import kr.urbansoft.kursmapper.processor.application.port.inbound.ReportUnresolvedMappingFunctionUseCase
import kr.urbansoft.kursmapper.processor.application.port.inbound.ResolveMappingFunctionUseCase
import kr.urbansoft.kursmapper.processor.application.port.outbound.LoadCollectedMappingFunctionPort
import kr.urbansoft.kursmapper.processor.application.port.outbound.LoadConfigPort
import kr.urbansoft.kursmapper.processor.application.port.outbound.LoadKursTypeIdPort
import kr.urbansoft.kursmapper.processor.application.port.outbound.LoadKursTypePort
import kr.urbansoft.kursmapper.processor.application.port.outbound.LoadUserDefinedMappingFunctionPort
import kr.urbansoft.kursmapper.processor.application.port.outbound.RaiseCompileErrorPort
import kr.urbansoft.kursmapper.processor.application.port.outbound.SaveCollectedMappingFunctionPort
import kr.urbansoft.kursmapper.processor.application.port.outbound.WriteGuideToFilePort
import kr.urbansoft.kursmapper.processor.application.port.outbound.WriteMappingFunctionPort
import kr.urbansoft.kursmapper.processor.application.service.CollectMappingFunctionService
import kr.urbansoft.kursmapper.processor.application.service.GenerateCodeService
import kr.urbansoft.kursmapper.processor.application.service.ReportUnresolvedMappingFunctionService
import kr.urbansoft.kursmapper.processor.application.service.ResolveMappingFunctionService
import kr.urbansoft.kursmapper.processor.domain.model.config.definition.KursContextDefinition
import kr.urbansoft.kursmapper.processor.domain.service.CollectCandidateMappingFunctionDomainService
import kr.urbansoft.kursmapper.processor.domain.service.CollectPreDefinedMappingFunctionDomainService
import kr.urbansoft.kursmapper.processor.domain.service.GenerateGuideDomainService
import kr.urbansoft.kursmapper.processor.domain.service.MergeMappingFunctionDomainService
import kr.urbansoft.kursmapper.processor.domain.service.ResolveMappingFunctionDomainService
import kr.urbansoft.shared.exception.ApplicationException
import kr.urbansoft.shared.exception.ExceptionType
import kr.urbansoft.tools.KursDI

class Processor(private val codeGenerator: CodeGenerator, private val kspLogger: KSPLogger) : SymbolProcessor {
  override fun process(resolver: Resolver): List<KSAnnotated> {
    resolver
      .getValidAnnotatedKSClassDeclarationList(KursContextDefinition.QUALIFIED_NAME)
      .validateUnique(
        keySelector = {
          getAnnotationArgumentValueAsTrimmedStringOrNull(KursContextDefinition.QUALIFIED_NAME, KursContextDefinition.Property.CONTEXT_NAME)
            ?: run {
              kspLogger.error(
                "'${KursContextDefinition.Property.CONTEXT_NAME}' in @${KursContextDefinition.SIMPLE_NAME} is required.",
                this,
              )
              return emptyList()
            }
        },
        whenDuplicated = { contextName ->
          kspLogger.error(
            "'${KursContextDefinition.Property.CONTEXT_NAME}' in @${KursContextDefinition.SIMPLE_NAME} is duplicated: $contextName",
            this,
          )
          return emptyList()
        },
      )
      .validateAllInterface {
        kspLogger.error("Only interface can be annotated with @${KursContextDefinition.SIMPLE_NAME}.", this)
        return emptyList()
      }
      .forEach { configInterfaceDeclaration -> configInterfaceDeclaration.accept(Visitor(resolver), Unit) }
    return emptyList()
  }

  inner class Visitor(private val kspResolver: Resolver) : KSVisitorVoid() {
    override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
      val dependencies =
        KursDI()
          .inboundAdapter {
            add { CollectMappingFunctionInboundAdapter(it()) }
            add { GenerateCodeInboundAdapter(it()) }
            add { ReportUnresolvedMappingFunctionInboundAdapter(it()) }
            add { ResolveMappingFunctionInboundAdapter(it()) }
          }
          .useCase {
            add<CollectMappingFunctionUseCase> { CollectMappingFunctionService(it(), it(), it(), it(), it(), it(), it(), it()) }
            add<GenerateCodeUseCase> { GenerateCodeService(it(), it(), it(), it()) }
            add<ReportUnresolvedMappingFunctionUseCase> { ReportUnresolvedMappingFunctionService(it(), it(), it(), it(), it(), it()) }
            add<ResolveMappingFunctionUseCase> { ResolveMappingFunctionService(it(), it(), it(), it(), it(), it()) }
          }
          .domainService {
            add { CollectCandidateMappingFunctionDomainService() }
            add { CollectPreDefinedMappingFunctionDomainService() }
            add { GenerateGuideDomainService() }
            add { MergeMappingFunctionDomainService() }
            add { ResolveMappingFunctionDomainService() }
          }
          .outPort {
            add<LoadCollectedMappingFunctionPort> { LoadCollectedMappingFunctionAdapter(it()) }
            add<LoadConfigPort> { LoadConfigAdapter(it(), it(), it(), it(), it(), it(), it()) }
            add<LoadKursTypeIdPort> { LoadKursTypeIdAdapter(it(), it(), it(), it(), it(), it()) }
            add<LoadKursTypePort> { LoadKursTypeAdapter(it(), it(), it(), it(), it(), it()) }
            add<LoadUserDefinedMappingFunctionPort> { LoadUserDefinedMappingFunctionAdapter(it(), it(), it(), it()) }
            add<RaiseCompileErrorPort> { RaiseCompileErrorAdapter() }
            add<SaveCollectedMappingFunctionPort> { SaveCollectedMappingFunctionAdapter(it()) }
            add<WriteGuideToFilePort> { WriteGuideToFileAdapter(it()) }
            add<WriteMappingFunctionPort> { WriteMappingFunctionAdapter(it(), it(), it(), it()) }
          }
          .infra {
            add { CheckKSTypeTraitProvider(it(), it()).get() }
            add { CollectedMappingFunctionRegistry.create() }
            add { ContextConfigRegistry.create() }
            add { HandleMappedKursTypeIdProvider(it(), it(), it()).get() }
            add { KSTypeRegistry.create() }
            add { KursTypeIdRegistry.create() }
            add { KursTypeNameResolver(it()) }
            add { KursTypeRegistry.create() }
            add { KursTypeRuleConfigRegistry.create() }
            add { LoadMappedKursTypeIdProvider(it()).get() }
            add { PackageRuleConfigRegistry.create() }
            add { RawConfigReader(classDeclaration, it()) }
            add { RawUserDefinedMappingFunctionReader(it()) }
            add { TypeNameRegistry.create() }
          }
          .externalInfra {
            add<CodeGenerator> { codeGenerator }
            add<KSPLogger> { kspLogger }
            add<Resolver> { kspResolver }
          }
          .build()

      try {
        // collect mapping functions
        dependencies<CollectMappingFunctionInboundAdapter>().collect()

        // resolve mapping functions
        dependencies<ResolveMappingFunctionInboundAdapter>().resolve()

        // generate code
        dependencies<GenerateCodeInboundAdapter>().generate()

        // report unresolved mapping functions
        dependencies<ReportUnresolvedMappingFunctionInboundAdapter>().report()
      } catch (e: ApplicationException) {
        if (e.type == ExceptionType.GUIDE) kspLogger.error(e.message() ?: e.message, classDeclaration)
        else kspLogger.error(e.message, classDeclaration)
        throw e
      } catch (e: Exception) {
        kspLogger.error("Unexpected Exception: ${e.message ?: "Unknown message"}", classDeclaration)
        throw e
      }
    }
  }
}
