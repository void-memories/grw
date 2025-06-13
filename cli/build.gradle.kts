plugins {
  kotlin("jvm") version "1.9.24"
  kotlin("plugin.serialization") version "1.9.24"
  application
}

repositories {
  google()
  mavenCentral()
  gradlePluginPortal()
  maven {
    url = uri("https://repo.gradle.org/gradle/libs-releases")
  }
}

dependencies {
  implementation("com.github.ajalt.clikt:clikt:4.0.0")
  
  // Gradle Tooling API for direct project access (using available version)
  implementation("org.gradle:gradle-tooling-api:8.1.1")
  
  // Android Gradle Plugin API for accessing Android project models
  implementation("com.android.tools.build:gradle:8.1.0")
  
  // JSON serialization for structured output
  implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
  
  // Logging
  runtimeOnly("org.slf4j:slf4j-simple:2.0.9")
}

kotlin {
  jvmToolchain(18)
}

java {
  sourceCompatibility = JavaVersion.VERSION_18
  targetCompatibility = JavaVersion.VERSION_18
}

application {
  mainClass.set("dev.namn.cli.MainKt")
}
