package dev.namn.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import dev.namn.cli.commands.adb.ClearData
import dev.namn.cli.commands.gradle.AndroidTest
import dev.namn.cli.commands.gradle.Build
import dev.namn.cli.commands.gradle.Clean
import dev.namn.cli.commands.gradle.CleanBuild
import dev.namn.cli.commands.gradle.Config
import dev.namn.cli.commands.gradle.Gen
import dev.namn.cli.commands.adb.Run
import dev.namn.cli.commands.gradle.Task
import dev.namn.cli.commands.gradle.Test
import dev.namn.cli.commands.gradle.Variant
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
        AndroidTest(),
        ClearData()
    )
    .main(args) 
