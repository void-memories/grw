package dev.namn.cli.commands.gradle

import com.github.ajalt.clikt.core.CliktCommand
import dev.namn.cli.utils.Loader
import dev.namn.cli.utils.UI
import dev.namn.cli.utils.UI.showSuccess
import dev.namn.cli.utils.abort
import dev.namn.cli.utils.runShell

class Clean : CliktCommand(
    name = "clean",
    help = "Clean build artifacts and temporary files"
) {
    override fun run() {
        UI.showCommandDescription("Cleaning build artifacts")

        try {
            Loader.start("Cleaning project")
            runShell("./gradlew -q clean")
            Loader.stop()

            showSuccess("Build artifacts cleaned successfully")
        } catch (e: Exception) {
            Loader.stop()
            abort(e)
        }
    }
}
