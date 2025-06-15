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
            val includeRegex =
                """include\s*\(\s*["']([^"']+)["']\s*\)|include\s+["']([^"']+)["']""".toRegex()

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

    fun getApplicationModuleName(): String? {
        val rootBuildFiles = listOf(
            File(projectDir, "build.gradle.kts"),
            File(projectDir, "build.gradle")
        )

        for (buildFile in rootBuildFiles) {
            if (buildFile.exists() && hasAndroidApplicationPlugin(buildFile)) {
                return "." // Root module
            }
        }

        projectDir.listFiles()?.forEach { dir ->
            if (dir.isDirectory && !dir.name.startsWith(".")) {
                val buildFiles = listOf(
                    File(dir, "build.gradle.kts"),
                    File(dir, "build.gradle")
                )

                for (buildFile in buildFiles) {
                    if (buildFile.exists() && hasAndroidApplicationPlugin(buildFile)) {
                        return dir.name
                    }
                }
            }
        }

        return null
    }


    fun getApplicationId(moduleName: String): String? {
        val moduleDir = if (moduleName == ".") projectDir else File(projectDir, moduleName)
        val buildFiles = listOf(
            File(moduleDir, "build.gradle.kts"),
            File(moduleDir, "build.gradle")
        )

        for (buildFile in buildFiles) {
            if (buildFile.exists()) {
                try {
                    val content = buildFile.readText().removeComments()

                    val applicationIdRegex = """applicationId\s*(=|\s)\s*["']([^"']+)["']"""
                        .toRegex(setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL))

                    val matchResult = applicationIdRegex.find(content)
                    if (matchResult != null) {
                        return matchResult.groupValues[2]
                    }
                } catch (_: Exception) {
                    continue
                }
            }
        }

        return null
    }


    private fun hasAndroidApplicationPlugin(buildFile: File): Boolean {
        return try {
            val content = buildFile.readText().removeComments()

            // Pattern 1: plugins { id("com.android.application") } - applied by default
            val pluginIdRegex = """id\s*\(\s*["']com\.android\.application["']\s*\)"""
                .toRegex(setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL))

            // Pattern 2: plugins { alias(libs.plugins.android.application) } - applied by default
            // But NOT if it has "apply false"
            val aliasRegex = """alias\s*\(\s*[^)]*\.android\.application[^)]*\)"""
                .toRegex(setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL))
            
            // Pattern 3: apply plugin: 'com.android.application'
            val applyPluginRegex = """apply\s+plugin\s*:\s*['"]com\.android\.application['"]"""
                .toRegex(setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL))

            // Check if plugin is declared but explicitly not applied
            val applyFalseRegex = """alias\s*\(\s*[^)]*\.android\.application[^)]*\)\s+apply\s+false"""
                .toRegex(setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL))

            val hasPluginId = pluginIdRegex.containsMatchIn(content)
            val hasAlias = aliasRegex.containsMatchIn(content)
            val hasApplyPlugin = applyPluginRegex.containsMatchIn(content)
            val hasApplyFalse = applyFalseRegex.containsMatchIn(content)

            // Plugin is applied if:
            // - id("com.android.application") is present (applied by default)
            // - alias(...android.application...) is present AND NOT followed by "apply false"
            // - apply plugin: 'com.android.application' is present
            (hasPluginId || hasApplyPlugin || (hasAlias && !hasApplyFalse))
        } catch (_: Exception) {
            false
        }
    }

    private fun String.removeComments(): String {
        return this
            .replace(Regex("""//.*"""), "")                            // remove line comments
            .replace(Regex("""(?s)/\*.*?\*/"""), "")                   // remove block comments
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
            val buildTypesBlockRegex =
                """buildTypes\s*\{([^{}]*(?:\{[^{}]*\}[^{}]*)*)\}""".toRegex()
            val buildTypesBlock = buildTypesBlockRegex.find(content)?.groupValues?.get(1)

            if (buildTypesBlock != null) {
                // Find individual build type names within the block - look for name followed by {
                // But avoid matching nested blocks like applicationId or other properties
                val buildTypeNameRegex = """^\s*(\w+)\s*\{""".toRegex(RegexOption.MULTILINE)
                buildTypeNameRegex.findAll(buildTypesBlock).forEach { match ->
                    val buildTypeName = match.groupValues[1]
                    // Skip common property names that might be mistaken for build types
                    if (buildTypeName !in listOf(
                            "applicationId",
                            "versionCode",
                            "versionName",
                            "testInstrumentationRunner",
                            "proguardFiles",
                            "buildFeatures",
                            "compileOptions",
                            "kotlinOptions",
                            "signingConfig"
                        )
                    ) {
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
            throw IllegalStateException(
                "Failed to parse build types from ${buildFile.name}: ${e.message}",
                e
            )
        }
    }
}
