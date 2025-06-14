package dev.namn.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import dev.namn.cli.commands.Build
import dev.namn.cli.commands.BuildVariant
import dev.namn.cli.commands.Clean
import dev.namn.cli.commands.CleanBuild
import dev.namn.cli.commands.Config
import dev.namn.cli.commands.Gen
import dev.namn.cli.commands.Run
import dev.namn.cli.commands.Task
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
        
//        UI.showConnectionSteps()
        
//        UI.showGradleProjectInfo(
//            projectName = "android-project",
//            rootDir = System.getProperty("user.dir"),
//            gradleVersion = "8.5"
//        )
        
//        println("${UI.DIM}Opening workspace in preferred editor...${UI.RESET}")
        println()
    }
}

fun main(args: Array<String>) = GrwCliImpl()
    .subcommands(
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
