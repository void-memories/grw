package dev.namn.cli.commands.gradle

import com.github.ajalt.clikt.core.CliktCommand
import dev.namn.cli.GrwConfig
import dev.namn.cli.managers.GradleManager
import dev.namn.cli.utils.Input
import dev.namn.cli.utils.Loader
import dev.namn.cli.utils.UI
import dev.namn.cli.utils.UI.showCommandDescription
import dev.namn.cli.utils.UI.showError
import dev.namn.cli.utils.UI.showException
import dev.namn.cli.utils.UI.showInfo
import dev.namn.cli.utils.UI.showKeyValueList
import dev.namn.cli.utils.UI.showSuccess
import dev.namn.cli.utils.abort

class Variant : CliktCommand(
    name = "variant",
    help = "Select and configure build variant"
) {

    override fun run() {
        showCommandDescription("Configuring build variant")

        try {
            val flavors = GradleManager.listFlavors().map { it.name }
            val buildTypes = GradleManager.listBuildTypes()

            if (buildTypes.isEmpty()) {
                showError("No build types found in project")
                return
            }

            GrwConfig.selectedVariant?.let { current ->
                showKeyValueList(
                    listOf("Current Variant" to "${UI.BRIGHT_PURPLE}$current${UI.RESET}"),
                    "Current Configuration"
                )
            }

            val selectedVariant = if (flavors.isEmpty()) {
                showInfo("No custom flavors found - selecting from available build types")
                Input.promptList(buildTypes)
            } else {
                Input.promptList(flavors.mapIndexed { idx, flavor ->
                    val buildType = buildTypes[idx % buildTypes.size]
                    "$flavor${buildType.replaceFirstChar { it.uppercase() }}"
                })
            }

            Loader.start("Saving configuration")
            GrwConfig.setVariant(selectedVariant)
            Loader.stop()

            UI.showConfigUpdate("Build Variant", selectedVariant, "Configuration saved to .grw")
            println()

        } catch (e: Exception) {
            showException(e)
        }
    }
}
