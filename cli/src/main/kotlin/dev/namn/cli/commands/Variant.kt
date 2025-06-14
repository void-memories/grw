package dev.namn.cli.commands

import com.github.ajalt.clikt.core.CliktCommand
import dev.namn.cli.GrwConfig
import dev.namn.cli.utils.AndroidProjectAnalyzer
import dev.namn.cli.utils.Input
import dev.namn.cli.utils.Loader
import dev.namn.cli.utils.UI

class Variant : CliktCommand(
    name = "variant",
    help = "Select and configure build variant"
) {
    private val analyzer = AndroidProjectAnalyzer()

    override fun run() {
        UI.showCommandDescription("Configuring build variant")
        
        try {
            val flavors = analyzer.getFlavors().map { it.name }
            val buildTypes = analyzer.getBuildTypes()
            
            if (buildTypes.isEmpty()) {
                UI.showError("No build types found in project")
                return
            }

            GrwConfig.selectedVariant?.let { current ->
                UI.showKeyValueList(
                    listOf("Current Variant" to "${UI.BRIGHT_PURPLE}$current${UI.RESET}"),
                    "Current Configuration"
                )
            }
            
            val selectedVariant = if (flavors.isEmpty()) {
                UI.showInfo("No custom flavors found - selecting from available build types")
                Input.promptBuildType(buildTypes)
            } else {
                Input.promptBuildVariant(flavors, buildTypes)
            }
            
            Loader.start("Saving configuration")
            GrwConfig.setVariant(selectedVariant)
            Loader.stop()
            
            UI.showConfigUpdate("Build Variant", selectedVariant, "Configuration saved to .grw")
            println()
            
        } catch (e: Exception) {
            UI.showCommandError("grw variant", e.message ?: "Unknown error occurred")
        }
    }
}
