package dev.namn.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project

class GrwPlugin : Plugin<Project> {
  override fun apply(project: Project) {
    project.tasks.register("grwListFlavors") { it ->
      it.group = "grw"
      it.description = "Print all product-flavor names"
      it.doLast {
        project.extensions.findByName("android")?.let { androidExt ->
          val flavors = androidExt::class.java
            .getMethod("getProductFlavors")
            .invoke(androidExt) as Iterable<*>
          flavors.forEach { println(it.toString()) }
        }
      }
    }
  }
}
