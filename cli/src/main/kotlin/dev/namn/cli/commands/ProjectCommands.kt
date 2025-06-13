package dev.namn.cli.commands

import com.github.ajalt.clikt.core.CliktCommand
import org.gradle.tooling.GradleConnector
import org.gradle.tooling.model.GradleProject
import java.io.File

/**
 * Command to show comprehensive project information
 */
class ShowProjectInfo : CliktCommand(
    name = "project-info", 
    help = "📊 Show comprehensive project information"
) {
    override fun run() {
        println("🔍 Analyzing project structure...")
        
        val projectDir = File(".")
        GradleConnector.newConnector()
            .forProjectDirectory(projectDir)
            .connect().use { connection ->
                
                val project = connection.getModel(GradleProject::class.java)
                
                println()
                println("📋 Project Structure:")
                println("┌─────────────────────────────────────────┐")
                println("│ Name: ${project.name}".padEnd(39) + "│")
                println("│ Path: ${project.path}".padEnd(39) + "│")
                println("│ Directory: ${project.projectDirectory.name}".padEnd(39) + "│")
                println("└─────────────────────────────────────────┘")
                
                if (project.children.isNotEmpty()) {
                    println()
                    println("📁 Subprojects:")
                    println("┌─────────────────────────────────────────┐")
                    project.children.forEach { child ->
                        println("│ • ${child.name}".padEnd(39) + "│")
                    }
                    println("└─────────────────────────────────────────┘")
                }
                
                println()
            }
    }
} 
