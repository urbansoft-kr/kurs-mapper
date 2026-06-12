package kr.urbansoft.kursmapper.annotation

@Retention(AnnotationRetention.SOURCE)
annotation class KursRule(
  val mapperSubPackageCreationMode: MapperSubPackageCreationMode = MapperSubPackageCreationMode.AUTO,
  val mapperSubPackageName: String = "",
  val mapperNamePrefix: String = "",
  val mapperName: String = "",
  val mapperNameSuffix: String = "",
  val mappingFunctionNamePrefix: String = "",
  val mappingFunctionName: String = "",
  val mappingFunctionNameSuffix: String = "",
)
