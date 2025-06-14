package dev.namn.cli.commands

import com.github.ajalt.clikt.core.CliktCommand
import dev.namn.cli.GrwConfig
import dev.namn.cli.utils.UI

class Config : CliktCommand(
    name = "config",
    help = "🚀 display config"
) {
    override fun run() {
        UI.showJsonObject(GrwConfig.toJsonObject(), )
    }
}
