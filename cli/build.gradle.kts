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
  maven(url = "https://jitpack.io")
}

dependencies {
  implementation("com.github.ajalt.clikt:clikt:4.0.0")
  implementation("org.gradle:gradle-tooling-api:8.1.1")
  implementation("com.android.tools.build:gradle:8.1.0")
  runtimeOnly("org.slf4j:slf4j-simple:2.0.9")
  implementation("com.github.kotlin-inquirer:kotlin-inquirer:0.1.0")
  implementation("org.json:json:20230227")
  
  // Enhanced UI dependencies
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
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
  
  // Handle JANSI native library issues gracefully
  applicationDefaultJvmArgs = listOf(
    "-Djansi.passthrough=true",
    "-Djansi.strip=false"
  )
}
