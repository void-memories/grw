package dev.namn.cli.commands

import com.github.ajalt.clikt.core.CliktCommand
import dev.namn.cli.utils.Loader
import dev.namn.cli.utils.runShell

class Sync : CliktCommand(
    name = "sync",
    help = "🚀 perform gradle sync"
) {
    override fun run() {
        Loader.start()
        runShell("./gradlew --refresh-dependencies")
        Loader.stop()
    }
}
