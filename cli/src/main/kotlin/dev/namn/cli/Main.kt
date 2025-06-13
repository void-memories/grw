package dev.namn.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import dev.namn.cli.commands.Flavor

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

fun main(args: Array<String>) = GrwCliImpl()
    .subcommands(
        Flavor(),
    )
    .main(args) 
