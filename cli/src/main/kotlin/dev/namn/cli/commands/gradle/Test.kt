package dev.namn.cli.commands.gradle

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.CliktError
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.optional
import dev.namn.cli.GrwConfig
import dev.namn.cli.managers.GradleManager
import dev.namn.cli.utils.Input
import dev.namn.cli.utils.Loader
import dev.namn.cli.utils.abort
import dev.namn.cli.utils.runShell

class Test : CliktCommand(
    name = "test",
    help = "🚀 Run unit tests"
) {
    private val moduleArg by argument(help = "Module name (optional)").optional()

    override fun run() {
        val variant = GrwConfig.selectedVariant
            ?: abort("No build variant set. Run `grw variant` first.")

        val modules = GradleManager.listModules()

        val selectedModule = moduleArg ?: Input.promptList(modules, "Select module to test")

        val cmd = when (moduleArg) {
            "all" -> "./gradlew test"
            else -> "./gradlew :${selectedModule}:test${variant}UnitTest"
        }

        Loader.start("Running tests for module:$selectedModule variant:$variant")
        runShell(cmd)
        Loader.stop()
    }
}
