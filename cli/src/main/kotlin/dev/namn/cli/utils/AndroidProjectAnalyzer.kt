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

    fun getProjectModules(): List<String> {
        val modules = mutableListOf<String>()
        
        // Try settings.gradle.kts first, then settings.gradle
        val settingsKts = File(projectDir, "settings.gradle.kts")
        val settingsGroovy = File(projectDir, "settings.gradle")
        
        val settingsFile = when {
            settingsKts.exists() -> settingsKts
            settingsGroovy.exists() -> settingsGroovy
            else -> return emptyList()
        }
        
        try {
            val content = settingsFile.readText()
            
            // Parse include statements like: include(":app"), include(":core"), include ":app"
            val includeRegex = """include\s*\(\s*["']([^"']+)["']\s*\)|include\s+["']([^"']+)["']""".toRegex()
            
            includeRegex.findAll(content).forEach { match ->
                val moduleName = match.groupValues[1].ifEmpty { match.groupValues[2] }
                // Remove the leading colon if present
                val cleanName = moduleName.removePrefix(":")
                modules.add(cleanName)
            }
            
            return modules.toList()
        } catch (e: Exception) {
            return emptyList()
        }
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
