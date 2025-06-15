package dev.namn.cli.commands.adb

import com.github.ajalt.clikt.core.CliktCommand
import dev.namn.cli.GrwConfig
import dev.namn.cli.managers.AdbManager
import dev.namn.cli.managers.GradleManager
import dev.namn.cli.utils.Loader
import dev.namn.cli.utils.UI
import dev.namn.cli.utils.UI.showException
import dev.namn.cli.utils.UI.showInfo
import dev.namn.cli.utils.abort
import dev.namn.cli.utils.runShell

class ClearData : CliktCommand(
    name = "clearData",
    help = "🚀 Clear application data on the selected Android device"
) {
    override fun run() {
        val deviceId = AdbManager.getConnectedDeviceId()
        showInfo("📱 Using device: $deviceId")

        val variant = GrwConfig.selectedVariant
            ?: abort("No build variant set. Run `grw variant` first.")

        showInfo("🔧 Build variant: $variant")

        val appModule = GradleManager.findAppModuleName()
            ?: run {
                UI.showError("Could not find Android application module. Make sure you're in an Android project.")
                return
            }

        showInfo("📂 Application module: $appModule")

        val packageName = GradleManager.findApplicationId()
        showInfo("📦 Package: $packageName")

        try {
            Loader.start("Clearing data for $packageName on device $deviceId")
            runShell("adb -s $deviceId shell pm clear $packageName")
            Loader.stop()
            showInfo("✅ Cleared data for '$packageName' on device '$deviceId'.")
        } catch (e: Exception) {
            Loader.stop()
            showException(e)
        }
    }
}
