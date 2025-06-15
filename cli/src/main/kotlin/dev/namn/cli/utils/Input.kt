package dev.namn.cli.utils

import com.github.kinquirer.KInquirer
import com.github.kinquirer.components.ListViewOptions
import com.github.kinquirer.components.promptList

object Input {
    fun promptList(
        choices: List<String>,
        message: String = "Select an option",
        hint: String = "Use ↑/↓ arrows and Enter to select"
    ): String {
        println("${UI.BOLD}$message${UI.RESET}")
        if (hint.isNotEmpty()) {
            println("${UI.DIM}$hint${UI.RESET}")
        }
        println()

        val res = KInquirer.promptList(
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

        UI.showSelectionResult(res)
        return res
    }
}
