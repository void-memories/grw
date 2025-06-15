package dev.namn.cli.commands.adb

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.CliktError
import dev.namn.cli.GrwConfig
import dev.namn.cli.utils.AndroidProjectAnalyzer
import dev.namn.cli.utils.Input
import dev.namn.cli.utils.Loader
import dev.namn.cli.utils.runShell
import dev.namn.cli.utils.runShellWithOutput
import org.w3c.dom.Element
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

class ClearData : CliktCommand(
    name = "clearData",
    help = "🚀 Install and run the selected android application"
) {
    override fun run() {
        Loader.start("Inspecting connected devices...")
        val devicesOutput = runShellWithOutput("adb devices").trim()
        val deviceLines = devicesOutput.split("\n").drop(1) // Skip "List of devices attached" line
        val devices = deviceLines
            .filter { it.isNotBlank() && it.contains("\tdevice") }
            .map { it.split("\t")[0] }
        
        if (devices.isEmpty()) {
            echo("❌ No ADB devices found. Please connect a device and enable USB debugging.")
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
        
        // Get the selected build variant
        val selectedVariant = GrwConfig.selectedVariant
            ?: throw CliktError("No build variant set. Run `grw variant` first.")
        
        // Dynamically find the application module and package name
        val analyzer = AndroidProjectAnalyzer()
        val appModuleName = analyzer.getApplicationModuleName()
        
        if (appModuleName == null) {
            echo("❌ Could not find Android application module. Make sure you're in an Android project.")
            return
        }
        
        println("Found application module: $appModuleName")
        
        val packageName = analyzer.getApplicationId(appModuleName)
        
        if (packageName == null) {
            echo("❌ Could not find applicationId in the ${appModuleName} module's build.gradle file.")
            return
        }
        
        echo("📦 Found application module: $appModuleName")
        echo("📦 Found package: $packageName")
        echo("🔧 Using build variant: $selectedVariant")
        
        try {
            // Step 1: Install the app
            val installTask = "install${selectedVariant.replaceFirstChar { it.uppercase() }}"
            Loader.start("Installing $selectedVariant...")
            runShell("./gradlew $installTask")
            Loader.stop()
            echo("✅ App installed successfully!")
            
            // Step 2: Find and launch the app
            Loader.start("Finding launcher activity...")
            val projectRoot = File(System.getProperty("user.dir"))
            val manifestPath = if (appModuleName == ".") "src/main/AndroidManifest.xml" else "$appModuleName/src/main/AndroidManifest.xml"
            val manifest = File(projectRoot, manifestPath)
            
            if (!manifest.exists()) {
                echo("⚠️ Could not find AndroidManifest.xml at $manifestPath. App installed but not launched.")
                return
            }
            
            val doc = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder()
                .parse(manifest)
            
            // Find launcher activity
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
                echo("⚠️ Could not find launcher activity. App installed but not launched.")
                return
            }
            
            if (launchActivity.startsWith(".")) {
                launchActivity = packageName + launchActivity
            }
            
            // Launch the app
            Loader.start("Launching app...")
            val launchCmd = "adb -s $selectedDevice shell am start -n $packageName/$launchActivity"
            runShell(launchCmd)
            Loader.stop()
            
            echo("🚀 Complete! App installed and launched: $packageName/$launchActivity")
            
        } catch (e: Exception) {
            Loader.stop()
            echo("❌ Error: ${e.message}")
        }
    }
}
