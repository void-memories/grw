package dev.namn.cli.commands.gradle

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.CliktError
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.optional
import dev.namn.cli.GrwConfig
import dev.namn.cli.utils.AndroidProjectAnalyzer
import dev.namn.cli.utils.Input
import dev.namn.cli.utils.Loader
import dev.namn.cli.utils.runShell

class AndroidTest : CliktCommand(
    name = "androidTest",
    help = "🤖 Run Android instrumentation tests"
) {
    private val analyzer = AndroidProjectAnalyzer()
    private val moduleArg by argument(help = "Module name (optional)").optional()

    override fun run() {
        val variant = GrwConfig.selectedVariant
            ?: throw CliktError("No build variant set. Run `grw variant` first.")

        val modules = analyzer.getProjectModules()

        val selectedModule = moduleArg ?: Input.promptList(modules, "Select module to test")

        val cmd = when (moduleArg) {
            "all" -> "./gradlew connectedAndroidTest"
            else -> "./gradlew :${selectedModule}:connected${variant}AndroidTest"
        }

        Loader.start("Running Android tests for module:$selectedModule variant:$variant")
        runShell(cmd)
        Loader.stop()
    }
} 
