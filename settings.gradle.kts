plugins {
  id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "kurs-mapper"

include("kurs-mapper-annotation")
include("kurs-mapper-processor")