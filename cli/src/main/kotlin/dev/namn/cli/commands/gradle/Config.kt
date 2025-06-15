package dev.namn.cli.commands.gradle

import com.github.ajalt.clikt.core.CliktCommand
import dev.namn.cli.GrwConfig
import dev.namn.cli.utils.UI
import dev.namn.cli.utils.UI.showJsonObject

class Config : CliktCommand(
    name = "config",
    help = "🚀 display config"
) {
    override fun run() {
        showJsonObject(GrwConfig.toJsonObject(), )
    }
}
