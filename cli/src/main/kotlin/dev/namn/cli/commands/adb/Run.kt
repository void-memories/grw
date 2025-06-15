package dev.namn.cli.commands.adb

import com.github.ajalt.clikt.core.CliktCommand
import dev.namn.cli.GrwConfig
import dev.namn.cli.managers.AdbManager
import dev.namn.cli.managers.AndroidManifestManager
import dev.namn.cli.managers.GradleManager
import dev.namn.cli.utils.Loader
import dev.namn.cli.utils.UI
import dev.namn.cli.utils.UI.showException
import dev.namn.cli.utils.UI.showSuccess
import dev.namn.cli.utils.abort
import dev.namn.cli.utils.runShell

class Run : CliktCommand(
    name = "run",
    help = "Build, install on a device/emulator, and launch the app"
) {
    override fun run() {
        UI.showCommandDescription("Building, installing, and launching app")

        val variant = GrwConfig.selectedVariant
            ?: abort("No build variant set. Run `grw variant` first.")

        UI.showKeyValueList(
            listOf("Build Variant" to "${UI.BRIGHT_PURPLE}$variant${UI.RESET}"),
            "Build Configuration"
        )

        val deviceId = AdbManager.getConnectedDeviceId()

        val installTask = "install${variant.replaceFirstChar { it.uppercase() }}"
        try {
            Loader.start("Installing variant '$variant'")
            runShell("./gradlew $installTask")
            Loader.stop()

            val packageName = GradleManager.findApplicationId()
            val launchActivity = AndroidManifestManager.findLauncherActivity(packageName)

            Loader.start("Launching app on $deviceId")
            runShell("adb -s $deviceId shell am start -n $packageName/$launchActivity")
            Loader.stop()

            showSuccess("App installed and launched: $packageName/$launchActivity")
        } catch (e: Exception) {
            Loader.stop()
            showException(e)
        }
    }

}
