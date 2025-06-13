package dev.namn.cli

import dev.namn.cli.models.FlavorInfo
import dev.namn.cli.models.ProjectInfo
import org.gradle.tooling.GradleConnector
import org.gradle.tooling.model.GradleProject
import java.io.File

/**
 * Utility class for analyzing Android Gradle projects using the Gradle Tooling API
 */
class AndroidProjectAnalyzer {

    /**
     * Extracts comprehensive project information from the current directory
     */
    fun extractProjectInfo(): ProjectInfo {
        val projectDir = File(".")
        
        return GradleConnector.newConnector()
            .forProjectDirectory(projectDir)
            .connect().use { connection ->
                
                // Get the root gradle project
                val project = connection.getModel(GradleProject::class.java)
                
                // Find the app subproject (or use root if it's an app project)
                val appProject = findAppProject(project) ?: project
                
                // Extract Android project information
                val flavors = extractFlavors(appProject)
                val buildTypes = extractBuildTypes(appProject)
                
                ProjectInfo(
                    name = appProject.name,
                    flavors = flavors,
                    buildTypes = buildTypes
                )
            }
    }

    /**
     * Finds the Android app subproject in a multi-module setup
     */
    private fun findAppProject(project: GradleProject): GradleProject? {
        // Look for 'app' subproject first
        project.children.find { it.name == "app" }?.let { return it }
        
        // If no 'app' subproject, check if root project has Android plugin
        return if (hasAndroidPlugin(project)) project else null
    }

    /**
     * Simple heuristic to detect Android projects
     * In a real implementation, you'd check applied plugins
     */
    private fun hasAndroidPlugin(project: GradleProject): Boolean {
        return true // For now, assume it's an Android project
    }

    /**
     * Extracts flavor information from the Android project
     */
    private fun extractFlavors(project: GradleProject): List<FlavorInfo> {
        // In a real implementation, you would use the Android model
        // For now, we'll simulate by checking if build files contain flavor definitions
        val buildFile = File(project.projectDirectory, "build.gradle.kts")
        if (!buildFile.exists()) {
            val buildFileGroovy = File(project.projectDirectory, "build.gradle")
            if (!buildFileGroovy.exists()) return emptyList()
        }
        
        // Parse build file for flavor information
        return parseFlavorsFromBuildFile(buildFile)
    }

    /**
     * Parses build.gradle.kts file to extract flavor information using regex
     */
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

    /**
     * Extracts build types from the Android project
     */
    private fun extractBuildTypes(project: GradleProject): List<String> {
        // Extract build types (debug, release, etc.)
        return listOf("debug", "release") // Simplified for demo
    }
} 
