// src/main/kotlin/dev/namn/cli/commands/Gen.kt
package dev.namn.cli.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.CliktError
import com.github.ajalt.clikt.parameters.arguments.argument
import dev.namn.cli.GrwConfig
import dev.namn.cli.utils.Loader
import dev.namn.cli.utils.runShell
import java.io.File

class Gen : CliktCommand(
    name = "gen",
    help = "🚀 Generate APK or AAB"
) {
    private val assetType by argument(
        name = "ASSET_TYPE",
        help = "apk or aab"
    )

    override fun run() {
        val flavor = GrwConfig.flavor
            ?: throw CliktError("No flavor set. Run `grw flw <flavor>` first.")
        val variant = GrwConfig.variant
            ?: throw CliktError("No variant set. Run `grw vnt <variant>` first.")

        val prefix = when (assetType.lowercase()) {
            "apk" -> "assemble"
            "aab" -> "bundle"
            else -> throw CliktError("Unknown ASSET_TYPE '$assetType'. Use 'apk' or 'aab'.")
        }
        val taskName = buildString {
            append(prefix)
            append(flavor.replaceFirstChar { it.uppercase() })
            append(variant.replaceFirstChar { it.uppercase() })
        }

        val cmd = "./gradlew $taskName"
        Loader.start("Generating $assetType…")
        runShell(cmd)
        Loader.stop()

        val ext = assetType.lowercase()
        val expectedPattern = Regex(".*-${flavor.lowercase()}-${variant.lowercase()}\\.$ext$")
        val projectRoot = File(System.getProperty("user.dir"))
        val matches = projectRoot.walkTopDown()
            .filter { it.isFile && expectedPattern.matches(it.name.lowercase()) }
            .toList()

        if (matches.isEmpty()) {
            echo("⚠️ Could not locate the generated $assetType file.")
        } else {
            echo("✅ Artifact located at: ${matches.first().absolutePath}")
        }
    }
}
