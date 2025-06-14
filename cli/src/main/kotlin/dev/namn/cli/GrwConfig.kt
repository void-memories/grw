package dev.namn.cli

import dev.namn.cli.utils.Logger
import org.json.JSONObject
import java.io.File
import java.io.IOException

object GrwConfig {
    private val configFile = File(".grw")

    var selectedVariant: String? = null
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
                Logger.info("Loaded config → selectedVariant=$selectedVariant")
            } else {
                Logger.info("Config file not found, will create when variant is selected")
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

        selectedVariant = json.optString("selectedVariant").takeIf { it.isNotBlank() }
    }

    private fun writeConfigToDisk() {
        try {
            val json = JSONObject()
            selectedVariant?.let { json.put("selectedVariant", it) }

            configFile.writeText(json.toString(2))
            Logger.info("Config written to disk → selectedVariant=$selectedVariant")
        } catch (e: IOException) {
            Logger.error("Failed to write config to disk: ${e.message}")
        }
    }

    fun setVariant(variant: String) = apply {
        Logger.info("Setting variant → $variant")
        this.selectedVariant = variant
        writeConfigToDisk()
    }

    fun toJsonObject(): JSONObject = JSONObject().apply {
        put("variant", selectedVariant)
    }

    override fun toString(): String {
        return JSONObject().apply {
            selectedVariant?.let { put("selectedVariant", it) }
        }.toString()
    }
}
