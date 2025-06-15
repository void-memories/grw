package dev.namn.cli.managers

import dev.namn.cli.models.FlavorInfo
import dev.namn.cli.utils.abort
import org.gradle.tooling.GradleConnector
import org.gradle.tooling.model.GradleProject
import java.io.File

object GradleManager {
    private val projectDir = File(System.getProperty("user.dir"))

    private fun connect(): GradleProject =
        GradleConnector.newConnector()
            .forProjectDirectory(projectDir)
            .connect()
            .use { it.getModel(GradleProject::class.java) }

    private fun findAppProject(root: GradleProject): GradleProject =
        root.children.find { it.name == "app" } ?: root

    private fun locateBuildFile(project: GradleProject): File? {
        val dir = project.projectDirectory
        return listOf("build.gradle.kts", "build.gradle")
            .map { File(dir, it) }
            .firstOrNull { it.exists() }
    }

    fun listFlavors(): List<FlavorInfo> {
        val project = findAppProject(connect())
        val buildFile = locateBuildFile(project) ?: return emptyList()
        val content = buildFile.readText()
        val flavorPattern = """create\("([^"]+)"\)\s*\{[^}]*dimension\s*=\s*"([^"]+)"""".toRegex()
        return flavorPattern.findAll(content).map {
            FlavorInfo(
                name = it.groupValues[1],
                dimension = it.groupValues[2].ifBlank { null }
            )
        }.toList()
    }

    fun listBuildTypes(): List<String> {
        val project = findAppProject(connect())
        val buildFile = locateBuildFile(project)
            ?: throw IllegalStateException("No build.gradle(.kts) in ${project.projectDirectory}")
        val content = buildFile.readText()

        val buildTypesBlock = "buildTypes\\s*\\{([^{}]*(?:\\{[^{}]*\\}[^{}]*)*)\\}".toRegex()
            .find(content)?.groupValues?.get(1) ?: return emptyList()

        val typeNames = mutableSetOf<String>()
        "^\\s*(\\w+)\\s*\\{".toRegex(RegexOption.MULTILINE)
            .findAll(buildTypesBlock)
            .map { it.groupValues[1] }
            .filter {
                it !in listOf(
                    "applicationId", "versionCode", "versionName",
                    "testInstrumentationRunner", "proguardFiles",
                    "buildFeatures", "compileOptions", "kotlinOptions", "signingConfig"
                )
            }
            .forEach { typeNames += it }

        "create\\(\\s*['\"]([^'\"]+)['\"]\\s*\\)".toRegex()
            .findAll(buildTypesBlock)
            .map { it.groupValues[1] }
            .forEach { typeNames += it }

        if (!typeNames.contains("debug")) typeNames += "debug"
        return typeNames.sorted()
    }

    fun listModules(): List<String> {
        val settingsFile = listOf(
            File(projectDir, "settings.gradle.kts"),
            File(projectDir, "settings.gradle")
        ).firstOrNull { it.exists() } ?: return emptyList()

        val includeRegex =
            "include\\s*\\(\\s*['\"]([^'\"]+)['\"]\\s*\\)|include\\s+['\"]([^'\"]+)['\"]".toRegex()
        return includeRegex.findAll(settingsFile.readText()).map {
            it.groupValues[1].ifEmpty { it.groupValues[2] }.removePrefix(":")
        }.toList()
    }

    fun findAppModuleName(): String? {
        val gradleFiles = listOf("build.gradle.kts", "build.gradle")

        for (fileName in gradleFiles) {
            val file = File(projectDir, fileName)
            if (file.exists() && hasAndroidApplicationPlugin(file)) {
                return "."
            }
        }

        projectDir.listFiles()
            ?.filter { it.isDirectory && !it.name.startsWith(".") }
            ?.forEach { dir ->
                gradleFiles.forEach { fileName ->
                    val buildFile = File(dir, fileName)
                    if (buildFile.exists() && hasAndroidApplicationPlugin(buildFile)) {
                        return dir.name
                    }
                }
            }

        return null
    }

    private fun hasAndroidApplicationPlugin(buildFile: File): Boolean {
        val content = buildFile.readText()
            .replace(Regex("""//.*"""), "")
            .replace(Regex("""(?s)/\*.*?\*/"""), "")

        val opts = setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL)
        val patterns = listOf(
            """id\s*\(\s*["']com\.android\.application["']\s*\)""",
            """alias\s*\(\s*[^)]*\.android\.application[^)]*\)""",
            """apply\s+plugin\s*:\s*['"]com\.android\.application['"]"""
        ).map { it.toRegex(opts) }

        return patterns.any { it.containsMatchIn(content) }
    }

    fun findApplicationId(): String {
        val moduleName = findAppModuleName() ?: abort("Application Module Not Found")
        val dir = if (moduleName == ".") projectDir else File(projectDir, moduleName)
        return listOf("build.gradle.kts", "build.gradle")
            .map { File(dir, it) }
            .firstOrNull { it.exists() }
            ?.readText()
            ?.removeComments()
            ?.let { content ->
                "applicationId\\s*(?:=|)\\s*['\"]([^'\"]+)['\"]".toRegex(
                    setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL)
                ).find(content)?.groupValues?.get(1)
            }!!
    }

    private fun hasAndroidAppPlugin(file: File): Boolean {
        val content = file.readText().removeComments()
        val idRegex =
            "id\\s*\\(\\s*['\"]com\\.android\\.application['\"]\\s*\\)".toRegex(RegexOption.IGNORE_CASE)
        val applyRegex =
            "apply\\s+plugin\\s*:\\s*['\"]com\\.android\\.application['\"]".toRegex(RegexOption.IGNORE_CASE)
        return idRegex.containsMatchIn(content) || applyRegex.containsMatchIn(content)
    }

    private fun String.removeComments(): String =
        replace(Regex("//.*"), "").replace(Regex("(?s)/\\*.*?\\*/"), "")
}
