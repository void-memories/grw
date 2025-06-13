package dev.namn.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands

class ListFlavors : CliktCommand(name = "flavor-list", help = "List Android flavors") {
    override fun run() {
        val output = runBashCommand("./gradlew grwListFlavors")
        println(output)
    }
}

private fun runBashCommand(command: String): String {
    val process = ProcessBuilder("/bin/bash", "-c", command)
        .redirectErrorStream(true)
        .start()

    val output = process.inputStream.bufferedReader().readText().trim()
    val exitCode = process.waitFor()

    if (exitCode != 0) {
        throw RuntimeException("Command failed with exit code $exitCode:\n$output")
    }

    return output
}

class GrwCliImpl : CliktCommand() {
    override fun run() {
    }
}

fun main(args: Array<String>) = GrwCliImpl()
    .subcommands(ListFlavors())
    .main(args)
