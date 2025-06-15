package dev.namn.cli.utils

fun runShell(cmd: String) {
    Logger.info("Executing shell command → $cmd")
    try {
        val process = ProcessBuilder("bash", "-c", cmd)
            .redirectErrorStream(true)
            .start()

        val output = process.inputStream.bufferedReader().use { it.readText() }

        output.lineSequence().forEach { Logger.info(it) }

        val exitCode = process.waitFor()
        if (exitCode != 0) {
            Logger.error("Command '$cmd' exited with code $exitCode")
        } else {
            Logger.info("Command completed successfully")
        }
    } catch (e: Exception) {
        Logger.error("Failed to execute '$cmd': ${e.message}")
    }
}

fun runShellWithOutput(cmd: String): String {
    Logger.info("Executing shell command → $cmd")
    return try {
        val process = ProcessBuilder("bash", "-c", cmd)
            .redirectErrorStream(true)
            .start()

        val output = process.inputStream.bufferedReader().use { it.readText() }

        val exitCode = process.waitFor()
        if (exitCode != 0) {
            Logger.error("Command '$cmd' exited with code $exitCode")
            ""
        } else {
            Logger.info("Command completed successfully")
            output
        }
    } catch (e: Exception) {
        Logger.error("Failed to execute '$cmd': ${e.message}")
        ""
    }
}
