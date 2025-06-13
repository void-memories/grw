package dev.namn.cli.commands

import com.github.ajalt.clikt.core.CliktCommand
import dev.namn.cli.utils.Loader
import dev.namn.cli.utils.runShell

class Clean : CliktCommand(
    name = "clean",
    help = "🚀 delete build folder"
) {
    override fun run() {
        Loader.start()
        runShell("./gradlew clean")
        Loader.stop()
    }
}
