package dev.namn.cli.commands

import com.github.ajalt.clikt.core.CliktCommand
import dev.namn.cli.AndroidProjectAnalyzer
import dev.namn.cli.OutputFormatter

class ListFlavors : CliktCommand(
    name = "flavor-list", 
    help = "🚀 List Android flavors with beautiful formatting"
) {
    private val analyzer = AndroidProjectAnalyzer()
    private val formatter = OutputFormatter()

    override fun run() {
        try {
            val projectInfo = analyzer.extractProjectInfo()
            formatter.displayProjectInfo(projectInfo)
        } catch (e: Exception) {
            println("❌ Error accessing project: ${e.message}")
            println("💡 Make sure you're in an Android project directory")
        }
    }
} 
