package dev.namn.cli.utils

object Logger {
    fun info(msg: String) = println("ℹ️ [GrwConfig] $msg")
    fun error(msg: String) = println("❌ [GrwConfig] $msg")
}
