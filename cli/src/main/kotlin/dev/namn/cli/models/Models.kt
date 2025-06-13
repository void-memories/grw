package dev.namn.cli.models

import kotlinx.serialization.Serializable

@Serializable
data class FlavorInfo(
    val name: String,
    val dimension: String? = null,
    val applicationId: String? = null,
    val versionCode: Int? = null,
    val versionName: String? = null
)
