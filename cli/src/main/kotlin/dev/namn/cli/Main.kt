package dev.namn.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import dev.namn.cli.commands.ListFlavors
import dev.namn.cli.commands.ShowProjectInfo

/**
 * Main CLI command for the GRW tool
 */
class GrwCliImpl : CliktCommand(
    name = "grw",
    help = """
    🚀 GRW - Gradle Android CLI Tool
    
    Powerful CLI for Android Gradle projects using Gradle Tooling API.
    Direct access to project models - no plugin communication needed!
    """.trimIndent()
) {
    override fun run() {
        println()
        println("🚀 Welcome to GRW CLI!")
        println("💡 Use --help to see available commands")
        println()
    }
}

/**
 * Main entry point for the CLI application
 */
fun main(args: Array<String>) = GrwCliImpl()
    .subcommands(
        ListFlavors(),
        ShowProjectInfo()
    )
    .main(args) 
