package dev.namn.cli

import dev.namn.cli.utils.Logger
import org.json.JSONObject
import java.io.File
import java.io.IOException

object GrwConfig {
    private val configFile = File(".grw")

    var flavor: String? = null
        private set
    var variant: String? = null
        private set

    fun init() {
        Logger.info("Initializing GrwConfig")
        readConfigFromDisk()
    }

    private fun readConfigFromDisk() {
        try {
            if (configFile.exists()) {
                Logger.info("Config file found at ${configFile.absolutePath}, loading…")
                loadVars(configFile.readText())
                Logger.info("Loaded config → flavor=$flavor, variant=$variant")
            } else {
                Logger.info("Config file not found, creating new one with defaults")
                writeConfigToDisk()
            }
        } catch (e: IOException) {
            Logger.error("Failed to read config from disk: ${e.message}")
        }
    }

    private fun loadVars(jsonString: String) {
        val json = try {
            JSONObject(jsonString)
        } catch (e: Exception) {
            JSONObject()
        }

        flavor = json.optString("flavor").takeIf { it.isNotBlank() }
        variant = json.optString("variant").takeIf { it.isNotBlank() }
    }

    private fun writeConfigToDisk() {
        try {
            if (!configFile.exists()) {
                Logger.info("Creating config file at ${configFile.absolutePath}")
                configFile.createNewFile()
            }
            val json = JSONObject().apply {
                put("flavor", flavor)
                put("variant", variant)
            }
            configFile.writeText(json.toString(2))
            Logger.info("Config written to disk → flavor=$flavor, variant=$variant")
        } catch (e: IOException) {
            Logger.error("Failed to write config to disk: ${e.message}")
        }
    }

    fun setFlavor(flavor: String) = apply {
        Logger.info("Setting flavor → $flavor")
        this.flavor = flavor
        writeConfigToDisk()
    }

    fun setVariant(variant: String) = apply {
        Logger.info("Setting variant → $variant")
        this.variant = variant
        writeConfigToDisk()
    }
}
