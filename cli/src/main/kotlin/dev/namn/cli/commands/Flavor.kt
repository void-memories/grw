package dev.namn.cli.commands

import com.github.ajalt.clikt.core.CliktCommand
import dev.namn.cli.GrwConfig
import dev.namn.cli.utils.AndroidProjectAnalyzer
import dev.namn.cli.utils.Input
import dev.namn.cli.utils.UI

class Flavor : CliktCommand(
    name = "flavor",
    help = "${UI.BRIGHT_CYAN}🎯 Manage and configure Android product flavors${UI.RESET}"
) {
    private val analyzer = AndroidProjectAnalyzer()

    override fun run() {
        UI.showInfo("Analyzing Android project flavors...")
        
        try {
            val flavors = analyzer.getFlavors().map { it.name }
            
            if (flavors.isEmpty()) {
                UI.showWarning("No product flavors found in this project")
                println("${UI.DIM}${UI.GRAY}💡 Add flavors to your build.gradle.kts file to get started${UI.RESET}")
                return
            }

            println()
            UI.showTable(
                headers = listOf("Available Flavors", "Status"),
                rows = flavors.map { flavor ->
                    val isSelected = flavor == GrwConfig.flavor
                    val status = if (isSelected) "${UI.BRIGHT_GREEN}✓ Selected${UI.RESET}" else "${UI.GRAY}Available${UI.RESET}"
                    listOf(flavor, status)
                }
            )
            
            println()
            val selected = Input.promptList(
                choices = flavors,
                message = "Choose a product flavor",
                hint = "This will be used for builds and runs"
            )
            
            UI.showLoadingAnimation("Configuring flavor", 800)
            GrwConfig.setFlavor(selected)
            
            Input.showSelectionResult(selected, "product flavor")
            
            println("${UI.createBox(
                title = "Flavor Configuration Updated",
                content = listOf(
                    "Selected flavor: ${UI.BRIGHT_GREEN}$selected${UI.RESET}",
                    "Config saved to: ${UI.CYAN}.grw${UI.RESET}",
                    "Ready for builds and runs!"
                ),
                color = UI.GREEN
            )}")
            
        } catch (e: Exception) {
            UI.showError("Failed to analyze flavors: ${e.message}")
            println("${UI.DIM}${UI.GRAY}💡 Make sure you're in an Android project directory${UI.RESET}")
        }
    }
}
