plugins {
  kotlin("jvm") version "1.9.0"
  application
}

repositories {
  mavenCentral()
}

dependencies {
  implementation("com.github.ajalt.clikt:clikt:4.0.0")
}

application {
  mainClass.set("dev.namn.cli.MainKt")
}
