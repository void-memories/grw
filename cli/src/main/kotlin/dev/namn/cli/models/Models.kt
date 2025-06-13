package dev.namn.cli.models

import kotlinx.serialization.Serializable

/**
 * Represents information about an Android product flavor
 */
@Serializable
data class FlavorInfo(
    val name: String,
    val dimension: String? = null,
    val applicationId: String? = null,
    val versionCode: Int? = null,
    val versionName: String? = null
)

/**
 * Represents comprehensive Android project information
 */
@Serializable
data class ProjectInfo(
    val name: String,
    val flavors: List<FlavorInfo>,
    val buildTypes: List<String> = emptyList()
) 
