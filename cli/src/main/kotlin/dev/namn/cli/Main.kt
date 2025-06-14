package dev.namn.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import dev.namn.cli.commands.Build
import dev.namn.cli.commands.BuildVariant
import dev.namn.cli.commands.Clean
import dev.namn.cli.commands.CleanBuild
import dev.namn.cli.commands.Config
import dev.namn.cli.commands.Flavor
import dev.namn.cli.commands.Gen
import dev.namn.cli.commands.Run
import dev.namn.cli.commands.Task
import dev.namn.cli.utils.UI

class GrwCliImpl : CliktCommand(
    name = "grw",
    help = """
    ${UI.GRADIENT_BLUE}🚀 GRW - Gradle Android CLI Tool${UI.RESET}
    
    ${UI.CYAN}Powerful CLI for Android Gradle projects using Gradle Tooling API.${UI.RESET}
    ${UI.GRAY}Direct access to project models - no plugin communication needed!${UI.RESET}
    """.trimIndent()
) {
    override fun run() {
        UI.clearScreen()
        UI.showWelcomeBanner()
        
        println()
        UI.showLoadingAnimation("Initializing GRW CLI", 1000)
        
        GrwConfig.init()
        
        UI.showCommandsOverview()
        println()
    }
}

fun main(args: Array<String>) = GrwCliImpl()
    .subcommands(
        Flavor(),
        BuildVariant(),
        Config(),
        Clean(),
        Task(),
        Gen(),
        CleanBuild(),
        Build(),
        Run()
    )
    .main(args) 
