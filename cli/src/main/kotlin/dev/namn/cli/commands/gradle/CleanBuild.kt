package dev.namn.cli.commands.gradle

import com.github.ajalt.clikt.core.CliktCommand
import dev.namn.cli.utils.Loader
import dev.namn.cli.utils.runShell

class CleanBuild : CliktCommand(
    name = "cleanBuild",
    help = "🚀 Cleans & Build the entire project"
) {
    override fun run() {
        val cmd = "./gradlew clean && ./gradlew build"

        Loader.start("Building project…")
        runShell(cmd)
        Loader.stop()

        echo("✅ Project clean & build successful!")
    }
}
