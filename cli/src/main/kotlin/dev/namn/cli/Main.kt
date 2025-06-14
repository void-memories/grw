package dev.namn.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import dev.namn.cli.commands.AndroidTest
import dev.namn.cli.commands.Build
import dev.namn.cli.commands.Clean
import dev.namn.cli.commands.CleanBuild
import dev.namn.cli.commands.Config
import dev.namn.cli.commands.Gen
import dev.namn.cli.commands.Run
import dev.namn.cli.commands.Task
import dev.namn.cli.commands.Test
import dev.namn.cli.commands.Variant
import dev.namn.cli.utils.UI

class GrwCliImpl : CliktCommand(
    name = "grw",
    help = """
    ${UI.BRIGHT_CYAN}GRW${UI.RESET} ${UI.GRAY}•${UI.RESET} ${UI.DIM}Gradle Android CLI Tool${UI.RESET}
    
    ${UI.CYAN}Powerful CLI for Android Gradle projects using Gradle Tooling API.${UI.RESET}
    ${UI.GRAY}Direct access to project models - no plugin communication needed!${UI.RESET}
    """.trimIndent()
) {
    override fun run() {
        println()
        GrwConfig.init()
        println()
    }
}

fun main(args: Array<String>) = GrwCliImpl()
    .subcommands(
        Variant(),
        Config(),
        Clean(),
        Task(),
        Gen(),
        CleanBuild(),
        Build(),
        Run(),
        Test(),
        AndroidTest()
    )
    .main(args) 
