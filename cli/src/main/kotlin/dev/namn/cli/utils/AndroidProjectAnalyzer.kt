package dev.namn.cli.utils

import dev.namn.cli.models.FlavorInfo
import org.gradle.tooling.GradleConnector
import org.gradle.tooling.model.GradleProject
import java.io.File

class AndroidProjectAnalyzer {
    private val projectDir = File(".")

    fun getFlavors(): List<FlavorInfo> {
        return GradleConnector.newConnector()
            .forProjectDirectory(projectDir)
            .connect().use { connection ->
                val gradleProject = connection.getModel(GradleProject::class.java)
                val project = findAppProject(gradleProject) ?: gradleProject

                val buildFile = File(project.projectDirectory, "build.gradle.kts")
                if (!buildFile.exists()) {
                    val buildFileGroovy = File(project.projectDirectory, "build.gradle")
                    if (!buildFileGroovy.exists()) return emptyList()
                }

                parseFlavorsFromBuildFile(buildFile)
            }
    }

    fun getBuildTypes(): List<String> {
        return listOf("debug", "release")
    }

    private fun findAppProject(project: GradleProject): GradleProject? {
        //TODO: take in onboarding step
        project.children.find { it.name == "app" }?.let { return it }

        return null
    }

    private fun parseFlavorsFromBuildFile(buildFile: File): List<FlavorInfo> {
        try {
            val content = buildFile.readText()
            val flavors = mutableListOf<FlavorInfo>()

            // Simple regex to find create("flavorName") patterns
            val flavorRegex = """create\("([^"]+)"\)\s*\{[^}]*dimension\s*=\s*"([^"]+)"""".toRegex()
            flavorRegex.findAll(content).forEach { match ->
                val name = match.groupValues[1]
                val dimension = match.groupValues[2].takeIf { it.isNotEmpty() }
                flavors.add(FlavorInfo(name = name, dimension = dimension))
            }

            return flavors
        } catch (e: Exception) {
            return emptyList()
        }
    }
}
