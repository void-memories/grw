package dev.namn.cli.utils

object Logger {
    fun info(msg: String) = println("$msg")
    fun error(msg: String) = println("❌ $msg")
}
