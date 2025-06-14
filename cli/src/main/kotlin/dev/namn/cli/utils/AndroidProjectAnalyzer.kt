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
        return GradleConnector.newConnector()
            .forProjectDirectory(projectDir)
            .connect().use { connection ->
                val gradleProject = connection.getModel(GradleProject::class.java)
                val project = findAppProject(gradleProject) ?: gradleProject

                val buildFile = File(project.projectDirectory, "build.gradle.kts")
                if (!buildFile.exists()) {
                    val buildFileGroovy = File(project.projectDirectory, "build.gradle")
                    if (!buildFileGroovy.exists()) {
                        throw IllegalStateException("No build.gradle or build.gradle.kts file found in project directory: ${project.projectDirectory}")
                    }
                    return@use parseBuildTypesFromBuildFile(buildFileGroovy)
                }

                parseBuildTypesFromBuildFile(buildFile)
            }
    }

    fun getProjectModules(): List<String> {
        val modules = mutableListOf<String>()
        
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
                val cleanName = moduleName.removePrefix(":")
                modules.add(cleanName)
            }
            
            return modules.toList()
        } catch (e: Exception) {
            return emptyList()
        }
    }

    private fun findAppProject(project: GradleProject): GradleProject? {
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

    private fun parseBuildTypesFromBuildFile(buildFile: File): List<String> {
        try {
            val content = buildFile.readText()
            val buildTypes = mutableSetOf<String>()

            // Pattern 1: buildTypes { debug { ... }, release { ... } }
            val buildTypesBlockRegex = """buildTypes\s*\{([^{}]*(?:\{[^{}]*\}[^{}]*)*)\}""".toRegex()
            val buildTypesBlock = buildTypesBlockRegex.find(content)?.groupValues?.get(1)
            
            if (buildTypesBlock != null) {
                // Find individual build type names within the block - look for name followed by {
                // But avoid matching nested blocks like applicationId or other properties
                val buildTypeNameRegex = """^\s*(\w+)\s*\{""".toRegex(RegexOption.MULTILINE)
                buildTypeNameRegex.findAll(buildTypesBlock).forEach { match ->
                    val buildTypeName = match.groupValues[1]
                    // Skip common property names that might be mistaken for build types
                    if (buildTypeName !in listOf("applicationId", "versionCode", "versionName", "testInstrumentationRunner", "proguardFiles", "buildFeatures", "compileOptions", "kotlinOptions", "signingConfig")) {
                        buildTypes.add(buildTypeName)
                    }
                }
            }
            
            // Pattern 2: create("buildTypeName") { ... } within buildTypes block
            if (buildTypesBlock != null) {
                val createBuildTypeRegex = """create\s*\(\s*["']([^"']+)["']\s*\)""".toRegex()
                createBuildTypeRegex.findAll(buildTypesBlock).forEach { match ->
                    val buildTypeName = match.groupValues[1]
                    buildTypes.add(buildTypeName)
                }
            }
            
            // Pattern 3: register("buildTypeName") { ... } within buildTypes block  
            if (buildTypesBlock != null) {
                val registerBuildTypeRegex = """register\s*\(\s*["']([^"']+)["']\s*\)""".toRegex()
                registerBuildTypeRegex.findAll(buildTypesBlock).forEach { match ->
                    val buildTypeName = match.groupValues[1]
                    buildTypes.add(buildTypeName)
                }
            }

            // Android projects always have 'debug' build type by default, even if not explicitly declared
            // If we found a buildTypes block but no debug, add it
            if (buildTypesBlock != null && !buildTypes.contains("debug")) {
                buildTypes.add("debug")
            }

            return buildTypes.toList().sorted()
        } catch (e: Exception) {
            throw IllegalStateException("Failed to parse build types from ${buildFile.name}: ${e.message}", e)
        }
    }
}
