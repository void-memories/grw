package dev.namn.cli.commands

import com.github.ajalt.clikt.core.CliktCommand
import dev.namn.cli.GrwConfig
import dev.namn.cli.utils.AndroidProjectAnalyzer
import dev.namn.cli.utils.Input

class BuildVariant : CliktCommand(
    name = "variant",
    help = "🚀 list and set build variants"
) {
    private val analyzer = AndroidProjectAnalyzer()

    override fun run() {
        try {
            val buildVariants = analyzer.getBuildVariants()
            if (buildVariants.isEmpty()) {
                echo("⚠️ No flavors found.")
                return
            }

            val selected = Input.promptList(buildVariants)
            GrwConfig.setBuildVariant(selected)

            echo("\n✅ You selected: $selected")
        } catch (e: Exception) {
            echo("❌ Error: ${e.message}")
        }
    }
}
