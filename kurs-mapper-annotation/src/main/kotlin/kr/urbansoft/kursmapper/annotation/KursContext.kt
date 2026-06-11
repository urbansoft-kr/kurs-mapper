package kr.urbansoft.kursmapper.annotation

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class KursContext(
  val contextName: String,
  val rootPackageName: String,
  val mapperNameGlobalSuffix: String = "Mapper",
  val mapperSourceVariableName: String = "source",
  val mappingFunctionNameVerb: String = "as",
  val packageRules: Array<KursPackageRule> =
    [KursPackageRule(packageName = "java", rule = KursRule(mapperNamePrefix = "Java", mappingFunctionNamePrefix = "Java"))],
  val guideLanguage: GuideLanguage = GuideLanguage.EN_US,
)
