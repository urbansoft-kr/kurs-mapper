plugins {
  kotlin("jvm") version "2.3.21" apply false
  id("com.vanniktech.maven.publish") version "0.36.0" apply false
  id("org.jetbrains.dokka") version "2.2.0" apply false
}

allprojects {
  group = "kr.urbansoft.kursmapper"
  version = "0.1.2-alpha"

  repositories { mavenCentral() }
}
