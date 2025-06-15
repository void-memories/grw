package dev.namn.cli.utils

import com.github.ajalt.clikt.core.CliktError

fun runShell(cmd: String) {
    UI.showInfo("Executing shell command → $cmd")
    try {
        val process = ProcessBuilder("bash", "-c", cmd)
            .redirectErrorStream(true)
            .start()

        val output = process.inputStream.bufferedReader().use { it.readText() }

        output.lineSequence().forEach { UI.showInfo(it) }

        val exitCode = process.waitFor()
        if (exitCode != 0) {
            UI.showError("Command '$cmd' exited with code $exitCode")
        } else {
            UI.showInfo("Command completed successfully")
        }
    } catch (e: Exception) {
        UI.showError("Failed to execute '$cmd': ${e.message}")
    }
}

fun runShellWithOutput(cmd: String): String {
    UI.showInfo("Executing shell command → $cmd")
    return try {
        val process = ProcessBuilder("bash", "-c", cmd)
            .redirectErrorStream(true)
            .start()

        val output = process.inputStream.bufferedReader().use { it.readText() }

        val exitCode = process.waitFor()
        if (exitCode != 0) {
            UI.showError("Command '$cmd' exited with code $exitCode")
            ""
        } else {
            UI.showInfo("Command completed successfully")
            output
        }
    } catch (e: Exception) {
        UI.showError("Failed to execute '$cmd': ${e.message}")
        ""
    }
}

fun abort(msg: String): Nothing = throw CliktError(msg)
fun abort(e: Throwable): Nothing = throw CliktError(e.message)
