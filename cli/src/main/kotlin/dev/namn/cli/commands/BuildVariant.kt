package dev.namn.cli.commands

import com.github.ajalt.clikt.core.CliktCommand
import dev.namn.cli.GrwConfig
import dev.namn.cli.utils.AndroidProjectAnalyzer
import dev.namn.cli.utils.Input
import dev.namn.cli.utils.UI

class BuildVariant : CliktCommand(
    name = "variant",
    help = "${UI.BRIGHT_PURPLE}🔧 Configure build variants (flavor + build type)${UI.RESET}"
) {
    private val analyzer = AndroidProjectAnalyzer()

    override fun run() {
        UI.showInfo("Analyzing build variants...")
        
        try {
            val flavors = analyzer.getFlavors().map { it.name }
            val buildTypes = analyzer.getBuildTypes()
            
            if (flavors.isEmpty()) {
                UI.showWarning("No flavors found - using default configuration")
                return
            }
            
            if (buildTypes.isEmpty()) {
                UI.showError("No build types found in project")
                return
            }

            println()
            println("${UI.BOLD}${UI.BRIGHT_WHITE}📊 Build Configuration Overview:${UI.RESET}")
            
            val currentVariant = GrwConfig.variant
            Input.showMultiChoice(
                title = "Current Configuration",
                options = mapOf(
                    "Flavor" to (GrwConfig.flavor ?: "Not set"),
                    "Build Type" to "debug, release",
                    "Current Variant" to (currentVariant ?: "Not set")
                ),
                selectedKey = if (currentVariant != null) "Current Variant" else null
            )
            
            println()
            val selectedVariant = Input.promptBuildVariant(flavors, buildTypes)
            
            UI.showLoadingAnimation("Configuring build variant", 1000)
            GrwConfig.setBuildVariant(selectedVariant)
            
            Input.showSelectionResult(selectedVariant, "build variant")
            
            println("${UI.createBox(
                title = "Build Variant Updated",
                content = listOf(
                    "Selected variant: ${UI.BRIGHT_PURPLE}$selectedVariant${UI.RESET}",
                    "This combines your flavor + build type",
                    "Ready for development!"
                ),
                color = UI.PURPLE
            )}")
            
        } catch (e: Exception) {
            UI.showError("Failed to configure build variant: ${e.message}")
        }
    }
}
