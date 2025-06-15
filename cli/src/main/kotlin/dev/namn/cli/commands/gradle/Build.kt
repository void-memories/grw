package dev.namn.cli.commands.gradle

import com.github.ajalt.clikt.core.CliktCommand
import dev.namn.cli.utils.Loader
import dev.namn.cli.utils.UI
import dev.namn.cli.utils.UI.showSuccess
import dev.namn.cli.utils.runShell

class Build : CliktCommand(
    name = "build",
    help = "🚀 Build the entire project"
) {
    override fun run() {
        val cmd = "./gradlew build"

        Loader.start("Building project…")
        runShell(cmd)
        Loader.stop()

        showSuccess("Project build successful!")
    }
}
