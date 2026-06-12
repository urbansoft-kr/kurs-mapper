package kr.urbansoft.kursmapper.processor.adapter.infra.ksp.reader

import com.google.devtools.ksp.isPublic
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.Modifier
import kr.urbansoft.kursmapper.processor.adapter.infra.ksp.extension.getAllMemberFunctionDeclarations
import kr.urbansoft.kursmapper.processor.adapter.infra.ksp.extension.getName
import kr.urbansoft.kursmapper.processor.adapter.infra.ksp.extension.getNameOrNull
import kr.urbansoft.kursmapper.processor.adapter.infra.ksp.extension.getPackageName
import kr.urbansoft.kursmapper.processor.adapter.infra.ksp.extension.getPrimaryConstructorDeclaration
import kr.urbansoft.kursmapper.processor.adapter.infra.ksp.extension.getSimpleName
import kr.urbansoft.kursmapper.processor.adapter.infra.ksp.mapper.symbol.kspMapper
import kr.urbansoft.kursmapper.processor.adapter.infra.ksp.model.RawUserDefinedMappingFunctionCandidate
import kr.urbansoft.kursmapper.processor.domain.model.config.ContextConfig
import kr.urbansoft.kursmapper.processor.domain.model.function.FunctionName
import kr.urbansoft.kursmapper.processor.domain.model.mapper.MapperName
import kr.urbansoft.kursmapper.processor.domain.model.packages.PackageName

class RawUserDefinedMappingFunctionReader(private val kspResolver: Resolver) {
  fun readRawUserDefinedMappingFunctionCandidateList(contextConfig: ContextConfig): List<RawUserDefinedMappingFunctionCandidate> =
    kspResolver
      .getAllFiles()
      .filter { file -> file.getPackageName() in contextConfig.configInterfacePackageName }
      .flatMap { file -> file.declarations.filterIsInstance<KSClassDeclaration>() }
      .filter { classDeclaration -> classDeclaration.classKind == ClassKind.CLASS }
      .filter { classDeclaration -> Modifier.VALUE in classDeclaration.modifiers }
      .filter { classDeclaration -> classDeclaration.getSimpleName().endsWith(contextConfig.mapperNameGlobalSuffix.value) }
      .mapNotNull { classDeclaration ->
        val parameter =
          classDeclaration.getPrimaryConstructorDeclaration(contextConfig.rootPackageName)?.parameters?.singleOrNull()
            ?: return@mapNotNull null
        val property = classDeclaration.getAllProperties().singleOrNull() ?: return@mapNotNull null
        if (!property.isPublic()) return@mapNotNull null
        if (parameter.getNameOrNull() == contextConfig.mapperSourceVariableName.value) classDeclaration to parameter.type.resolve()
        else null
      }
      .flatMap { (classDeclaration, sourceKsType) ->
        classDeclaration.getAllMemberFunctionDeclarations(kspResolver).mapNotNull { functionDeclaration ->
          val targetKsType = functionDeclaration.returnType?.resolve() ?: return@mapNotNull null
          val argumentList = functionDeclaration.parameters.map { it.kspMapper().asNullableRawArgument() ?: return@mapNotNull null }
          RawUserDefinedMappingFunctionCandidate(
            mapperPackageName = PackageName.from(classDeclaration.getPackageName()),
            mapperName = MapperName.from(classDeclaration.getSimpleName()),
            mappingFunctionName = FunctionName.from(functionDeclaration.getName()),
            sourceKsType = sourceKsType,
            targetKsType = targetKsType,
            argumentList = argumentList,
          )
        }
      }
      .toList()
}
