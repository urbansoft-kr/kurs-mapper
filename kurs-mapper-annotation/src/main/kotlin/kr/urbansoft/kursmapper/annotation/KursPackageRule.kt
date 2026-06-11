package kr.urbansoft.kursmapper.annotation

@Retention(AnnotationRetention.SOURCE) annotation class KursPackageRule(val packageName: String, val rule: KursRule)