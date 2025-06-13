package dev.namn.cli.utils

import com.github.kinquirer.KInquirer
import com.github.kinquirer.components.ListViewOptions
import com.github.kinquirer.components.promptList

object Input {
    fun promptList(choices: List<String>): String {
        return KInquirer.promptList(
            message = "Select a build variant",
            choices = choices,
            hint = "Use ↑/↓ & Enter",
            pageSize = choices.size,
            viewOptions = ListViewOptions(
                questionMarkPrefix = "❓",
                cursor = "-> ",
                nonCursor = "   "
            )
        )
    }
}
