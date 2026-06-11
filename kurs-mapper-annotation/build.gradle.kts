plugins {
  kotlin("jvm")
  id("com.vanniktech.maven.publish")
  id("org.jetbrains.dokka")
}

dependencies { testImplementation(kotlin("test")) }

kotlin { jvmToolchain(25) }

tasks.test { useJUnitPlatform() }

mavenPublishing {
  publishToMavenCentral()

  signAllPublications()

  coordinates("kr.urbansoft.kursmapper", "kurs-mapper-annotation", version.toString())

  pom {
    name.set("KursMapper")
    description.set("Annotations for KursMapper. Don't make your code fit the mapper. The mapper should fit your code.")
    inceptionYear.set("2026")
    url.set("https://github.com/urbansoft-kr/kurs-mapper")
    licenses {
      license {
        name.set("The MIT License")
        url.set("https://opensource.org/licenses/MIT")
      }
    }
    developers {
      developer {
        id.set("urbansoft")
        name.set("urbansoft")
        url.set("https://github.com/urbansoft-kr")
      }
    }
    scm {
      url.set("https://github.com/urbansoft-kr/kurs-mapper")
      connection.set("scm:git:git://github.com/urbansoft-kr/kurs-mapper.git")
      developerConnection.set("scm:git:ssh://git@github.com/urbansoft-kr/kurs-mapper.git")
    }
  }
}
