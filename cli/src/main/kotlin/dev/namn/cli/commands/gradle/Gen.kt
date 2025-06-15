package dev.namn.cli.commands.gradle

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import dev.namn.cli.GrwConfig
import dev.namn.cli.utils.Loader
import dev.namn.cli.utils.UI
import dev.namn.cli.utils.UI.showSuccess
import dev.namn.cli.utils.abort
import dev.namn.cli.utils.runShell
import java.io.File

class Gen : CliktCommand(
    name = "gen",
    help = "Generate APK or AAB"
) {
    private val assetType by argument(
        name = "ASSET_TYPE",
        help = "apk or aab"
    )

    override fun run() {
        UI.showCommandDescription("Generating ${assetType.uppercase()}")

        val selectedVariant = GrwConfig.selectedVariant
            ?: abort("No build variant set. Run `grw variant` first.")

        UI.showKeyValueList(
            listOf("Build Variant" to "${UI.BRIGHT_PURPLE}$selectedVariant${UI.RESET}"),
            "Build Configuration"
        )

        val prefix = when (assetType.lowercase()) {
            "apk" -> "assemble"
            "aab" -> "bundle"
            else -> abort("Unknown ASSET_TYPE '$assetType'. Use 'apk' or 'aab'.")
        }

        val taskName = "$prefix${selectedVariant.replaceFirstChar { it.uppercase() }}"

        try {
            Loader.start("Running Gradle task: $taskName")
            val cmd = "./gradlew $taskName"
            runShell(cmd)

            val ext = assetType.lowercase()
            val expectedPattern = Regex(".*\\.$ext$", RegexOption.IGNORE_CASE)
            val projectRoot = File(System.getProperty("user.dir"))
            val matches = projectRoot.walkTopDown()
                .filter { it.isFile && expectedPattern.matches(it.name) }
                .toList()

            Loader.stop()
            if (matches.isEmpty()) {
                abort("Could not locate the generated $assetType file")
            } else {
                showSuccess("Artifact generated: ${matches.first().absolutePath}")
            }

        } catch (e: Exception) {
            Loader.stop()
            abort(e)
        }
    }
}
