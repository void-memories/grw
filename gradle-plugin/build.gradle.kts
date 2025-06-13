plugins {
  `java-gradle-plugin`
  kotlin("jvm") version "1.9.24"
}

repositories {
  google()
  mavenCentral()
}

dependencies {
  implementation("com.android.tools.build:gradle:8.1.0")
}

kotlin {
  jvmToolchain(18)
}

java {
  sourceCompatibility = JavaVersion.VERSION_18
  targetCompatibility = JavaVersion.VERSION_18
}

gradlePlugin {
  plugins {
    create("grw") {
      id = "dev.namn.grw"
      implementationClass = "dev.namn.plugin.GrwPlugin"
    }
  }
}
