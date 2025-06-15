package dev.namn.cli.commands.gradle

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import dev.namn.cli.utils.Loader
import dev.namn.cli.utils.runShell

class Task : CliktCommand(
    name = "task",
    help = "🚀 execute gradle task"
) {
    private val gradleTask by argument(
        name = "TASK",
        help = "Name of the Gradle task to run (e.g. assembleDebug)"
    )

    override fun run() {
        Loader.start()
        runShell("./gradlew $gradleTask")
        Loader.stop()
    }
}
