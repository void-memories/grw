package dev.namn.cli.utils

object UI {
    const val RESET = "\u001B[0m"
    const val BLACK = "\u001B[30m"
    const val RED = "\u001B[31m"
    const val GREEN = "\u001B[32m"
    const val YELLOW = "\u001B[33m"
    const val BLUE = "\u001B[34m"
    const val PURPLE = "\u001B[35m"
    const val CYAN = "\u001B[36m"
    const val WHITE = "\u001B[37m"
    const val GRAY = "\u001B[90m"

    const val BRIGHT_RED = "\u001B[91m"
    const val BRIGHT_GREEN = "\u001B[92m"
    const val BRIGHT_YELLOW = "\u001B[93m"
    const val BRIGHT_BLUE = "\u001B[94m"
    const val BRIGHT_PURPLE = "\u001B[95m"
    const val BRIGHT_CYAN = "\u001B[96m"
    const val BRIGHT_WHITE = "\u001B[97m"

    const val BG_BLACK = "\u001B[40m"
    const val BG_RED = "\u001B[41m"
    const val BG_GREEN = "\u001B[42m"
    const val BG_YELLOW = "\u001B[43m"
    const val BG_BLUE = "\u001B[44m"
    const val BG_PURPLE = "\u001B[45m"
    const val BG_CYAN = "\u001B[46m"
    const val BG_WHITE = "\u001B[47m"
    const val BG_GRAY = "\u001B[100m"

    const val BOLD = "\u001B[1m"
    const val DIM = "\u001B[2m"
    const val ITALIC = "\u001B[3m"
    const val UNDERLINE = "\u001B[4m"

    fun showSuccess(message: String) {
        println("${BRIGHT_GREEN}✓${RESET} $message")
    }

    fun showError(message: String) {
        println("${BRIGHT_RED}✗${RESET} $message")
    }

    fun showWarning(message: String) {
        println("${BRIGHT_YELLOW}!${RESET} $message")
    }

    fun showInfo(message: String) {
        println("${BRIGHT_BLUE}i${RESET} $message")
    }

    fun showCommandDescription(description: String = "") {
        println("${BRIGHT_BLUE}▶${RESET} ${BOLD}$description${RESET}")
    }

    fun showCommandSuccess(message: String) {
        println("${BRIGHT_GREEN}✅${RESET} $message")
    }

    fun showCommandError(command: String, error: String) {
        println("${BRIGHT_RED}❌${RESET} ${BOLD}$command${RESET} failed")
        println("  ${DIM}$error${RESET}")
    }

    fun showConfigUpdate(key: String, value: String, description: String = "") {
        println("${BRIGHT_GREEN}✓${RESET} ${BOLD}$key${RESET} set to ${BRIGHT_CYAN}$value${RESET}")
        if (description.isNotEmpty()) {
            println("  ${DIM}$description${RESET}")
        }
    }

    fun showKeyValueList(items: List<Pair<String, String>>, title: String = "") {
        if (title.isNotEmpty()) {
            println("${BOLD}$title${RESET}")
            println()
        }

        val maxKeyLength = items.maxOfOrNull { it.first.length } ?: 0

        items.forEach { (key, value) ->
            val padding = " ".repeat(maxOf(0, maxKeyLength - key.length + 2))
            println("  ${GRAY}$key${RESET}$padding$value")
        }

        if (title.isNotEmpty()) println()
    }

    fun showSelectionResult(selected: String, type: String = "option") {
        println("${BRIGHT_GREEN}✓${RESET} Selected ${BOLD}$selected${RESET} as $type")
    }
}
