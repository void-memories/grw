package dev.namn.cli.managers

import com.github.ajalt.clikt.core.CliktError
import dev.namn.cli.utils.Input
import dev.namn.cli.utils.Loader
import dev.namn.cli.utils.abort
import dev.namn.cli.utils.runShellWithOutput

object AdbManager {
    fun getConnectedDeviceId(): String {
        Loader.start("Detecting ADB devices...")
        val lines = runShellWithOutput("adb devices").lines().drop(1)
        Loader.stop()

        val ids =
            lines.mapNotNull { it.takeIf { l -> l.contains("\tdevice") }?.substringBefore("\t") }
        return when {
            ids.isEmpty() -> abort("No devices connected")
            ids.size == 1 -> ids.first()
            else -> Input.promptList(ids, "Select Android device", "Your device")
        }
    }
}
