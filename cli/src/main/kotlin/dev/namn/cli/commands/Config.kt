package dev.namn.cli.commands

import com.github.ajalt.clikt.core.CliktCommand
import dev.namn.cli.GrwConfig

class Config : CliktCommand(
    name = "config",
    help = "🚀 display config"
) {
    override fun run() {
        echo("🚀 Config: $GrwConfig")
    }
}
