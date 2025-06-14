package dev.namn.cli.utils

import com.github.kinquirer.KInquirer
import com.github.kinquirer.components.ListViewOptions
import com.github.kinquirer.components.promptList

object Input {
    private fun promptList(
        choices: List<String>,
        message: String = "Select an option",
        hint: String = "Use ↑/↓ arrows and Enter to select"
    ): String {
        println("${UI.BOLD}$message${UI.RESET}")
        if (hint.isNotEmpty()) {
            println("${UI.DIM}$hint${UI.RESET}")
        }
        println()

        return KInquirer.promptList(
            message = "",
            choices = choices,
            hint = hint,
            pageSize = minOf(choices.size, 10),
            viewOptions = ListViewOptions(
                questionMarkPrefix = "▶",
                cursor = "● ",
                nonCursor = "  "
            )
        )
    }

    fun promptBuildVariant(flavors: List<String>, buildTypes: List<String>): String {
        val variants = mutableListOf<String>()

        for (flavor in flavors) {
            for (buildType in buildTypes) {
                variants.add("$flavor${buildType.replaceFirstChar { it.uppercase() }}")
            }
        }

        return promptList(
            choices = variants,
            message = "Select build variant",
            hint = "Choose your flavor + build type combination"
        )
    }

    fun promptBuildType(buildTypes: List<String>): String {
        return promptList(
            choices = buildTypes,
            message = "Select build type",
            hint = "Choose your build type (debug/release)"
        )
    }
}
