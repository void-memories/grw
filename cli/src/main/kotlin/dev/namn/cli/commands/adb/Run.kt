package dev.namn.cli.commands.adb

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.CliktError
import dev.namn.cli.GrwConfig
import dev.namn.cli.utils.Input
import dev.namn.cli.utils.Loader
import dev.namn.cli.utils.UI
import dev.namn.cli.utils.runShell
import dev.namn.cli.utils.runShellWithOutput
import org.w3c.dom.Element
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

class Run : CliktCommand(
    name = "run",
    help = "Build, install on a device/emulator, and launch the app"
) {
    override fun run() {
        UI.showCommandDescription("Building, installing, and launching app")

        val selectedVariant = GrwConfig.selectedVariant
            ?: throw CliktError("No build variant set. Run `grw variant` first.")

        UI.showKeyValueList(
            listOf("Build Variant" to "${UI.BRIGHT_PURPLE}$selectedVariant${UI.RESET}"),
            "Build Configuration"
        )

        // Device selection logic
        Loader.start("Inspecting connected devices...")
        val devicesOutput = runShellWithOutput("adb devices").trim()
        val deviceLines = devicesOutput.split("\n").drop(1) // Skip "List of devices attached" line
        val devices = deviceLines
            .filter { it.isNotBlank() && it.contains("\tdevice") }
            .map { it.split("\t")[0] }
        Loader.stop()
        
        if (devices.isEmpty()) {
            UI.showCommandError("grw run", "No ADB devices found. Please connect a device and enable USB debugging.")
            return
        }
        
        val selectedDevice = if (devices.size == 1) {
            echo("📱 Using device: ${devices[0]}")
            devices[0]
        } else {
            Input.promptList(
                choices = devices,
                message = "Select Android device to install and run app",
                hint = "Choose the device you want to install and run the app on"
            )
        }

        val installTask = "install${selectedVariant.replaceFirstChar { it.uppercase() }}"

        try {
            Loader.start("Installing $selectedVariant")
            runShell("./gradlew $installTask")
            Loader.stop()

            Loader.start("Finding launcher activity")
            val projectRoot = File(System.getProperty("user.dir"))
            val manifest = projectRoot.walkTopDown()
                .find { it.name == "AndroidManifest.xml" }
                ?: throw CliktError("Could not find AndroidManifest.xml to read package name")

            val doc = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder()
                .parse(manifest)
            val pkg = doc.documentElement.getAttribute("package")
            if (pkg.isNullOrBlank()) {
                throw CliktError("No package attribute in AndroidManifest.xml")
            }

            val activities = doc.getElementsByTagName("activity")
            var launchActivity: String? = null
            for (i in 0 until activities.length) {
                val act = activities.item(i) as Element
                val filters = act.getElementsByTagName("intent-filter")
                for (j in 0 until filters.length) {
                    val ifElem = filters.item(j) as Element
                    val actions = ifElem.getElementsByTagName("action")
                    val categories = ifElem.getElementsByTagName("category")
                    var hasMain = false
                    var hasLauncher = false
                    for (k in 0 until actions.length) {
                        val name = (actions.item(k) as Element).getAttribute("android:name")
                        if (name == "android.intent.action.MAIN") hasMain = true
                    }
                    for (k in 0 until categories.length) {
                        val name = (categories.item(k) as Element).getAttribute("android:name")
                        if (name == "android.intent.category.LAUNCHER") hasLauncher = true
                    }
                    if (hasMain && hasLauncher) {
                        launchActivity = act.getAttribute("android:name")
                        break
                    }
                }
                if (launchActivity != null) break
            }

            if (launchActivity == null) {
                throw CliktError("Could not find a launcher activity in AndroidManifest.xml")
            }

            if (launchActivity.startsWith(".")) {
                launchActivity = pkg + launchActivity
            }

            // Launch the app with selected device
            Loader.start("Launching app")
            runShell("adb -s $selectedDevice shell am start -n $pkg/$launchActivity")
            Loader.stop()

            UI.showCommandSuccess("App installed and launched: $pkg/$launchActivity")

        } catch (e: Exception) {
            Loader.stop()
            UI.showCommandError("grw run", e.message ?: "Unknown error occurred")
        }
    }
}
