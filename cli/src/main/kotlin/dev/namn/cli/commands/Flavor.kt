package dev.namn.cli.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.kinquirer.KInquirer
import com.github.kinquirer.components.ListViewOptions
import com.github.kinquirer.components.promptList
import dev.namn.cli.GrwConfig
import dev.namn.cli.utils.AndroidProjectAnalyzer

class Flavor : CliktCommand(
    name = "flavor",
    help = "🚀 list and set product flavors"
) {
    private val analyzer = AndroidProjectAnalyzer()

    override fun run() {
        try {
            val flavors = analyzer.getFlavors().map { it.name }
            if (flavors.isEmpty()) {
                echo("⚠️ No flavors found.")
                return
            }

            val selected = KInquirer.promptList(
                message = "Select a product flavor",
                choices = flavors,
                hint = "Use ↑/↓ & Enter",
                pageSize = flavors.size.coerceAtMost(5),
                viewOptions = ListViewOptions(
                    questionMarkPrefix = "❓",
                    cursor = "-> ",
                    nonCursor = "   "
                )
            )

            GrwConfig.setFlavor(selected)

            echo("\n✅ You selected: $selected")
        } catch (e: Exception) {
            echo("❌ Error: ${e.message}")
        }
    }
}
