package dev.namn.cli.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.CliktError
import dev.namn.cli.GrwConfig
import dev.namn.cli.utils.Loader
import dev.namn.cli.utils.runShell
import org.w3c.dom.Element
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

class Run : CliktCommand(
    name = "run",
    help = "🚀 Build, install on a device/emulator, and launch the app"
) {
    override fun run() {
        val flavor = GrwConfig.flavor
            ?: throw CliktError("No flavor set. Run `grw flw <flavor>` first.")
        val variant = GrwConfig.variant
            ?: throw CliktError("No variant set. Run `grw vnt <variant>` first.")

        val capFlavor = flavor.replaceFirstChar { it.uppercase() }
        val capVariant = variant.replaceFirstChar { it.uppercase() }

        val installTask = "install${capFlavor}${capVariant}"
        Loader.start("Installing $flavor-$variant…")
        runShell("./gradlew $installTask")
        Loader.stop()

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

        Loader.start("Launching app…")
        val launchExit = runShell("adb shell am start -n $pkg/$launchActivity")
        Loader.stop()

        echo("✅ App installed and launched: $pkg/$launchActivity")
    }
}
