package dev.namn.cli.utils

import com.github.kinquirer.KInquirer
import com.github.kinquirer.components.ListViewOptions
import com.github.kinquirer.components.promptList
import com.github.kinquirer.components.promptInput
import com.github.kinquirer.components.promptConfirm

object Input {
    
    fun promptList(
        choices: List<String>, 
        message: String = "Select an option",
        hint: String = "Use ↑/↓ arrows and Enter to select"
    ): String {
        println("${UI.CYAN}┌─────────────────────────────────────────────────────────────┐${UI.RESET}")
        println("${UI.CYAN}│${UI.RESET} ${UI.BOLD}${UI.BRIGHT_WHITE}🎯 $message${UI.RESET}${" ".repeat(55 - message.length)}${UI.CYAN}│${UI.RESET}")
        println("${UI.CYAN}└─────────────────────────────────────────────────────────────┘${UI.RESET}")
        println()
        
        return KInquirer.promptList(
            message = "❓ $message",
            choices = choices,
            hint = hint,
            pageSize = minOf(choices.size, 10),
            viewOptions = ListViewOptions(
                questionMarkPrefix = "❓",
                cursor = "▶ ",
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
    
    fun promptText(
        message: String,
        default: String? = null,
        hint: String? = null
    ): String {
        println("${UI.PURPLE}┌─────────────────────────────────────────────────────────────┐${UI.RESET}")
        println("${UI.PURPLE}│${UI.RESET} ${UI.BOLD}${UI.BRIGHT_WHITE}✏️  $message${UI.RESET}${" ".repeat(54 - message.length)}${UI.PURPLE}│${UI.RESET}")
        if (hint != null) {
            println("${UI.PURPLE}│${UI.RESET} ${UI.DIM}${UI.GRAY}$hint${UI.RESET}${" ".repeat(59 - hint.length)}${UI.PURPLE}│${UI.RESET}")
        }
        println("${UI.PURPLE}└─────────────────────────────────────────────────────────────┘${UI.RESET}")
        println()
        
        return KInquirer.promptInput(
            message = "✏️  $message",
            default = default ?: "",
            hint = hint ?: ""
        )
    }
    
    fun promptConfirm(
        message: String,
        default: Boolean = false
    ): Boolean {
        println("${UI.YELLOW}┌─────────────────────────────────────────────────────────────┐${UI.RESET}")
        println("${UI.YELLOW}│${UI.RESET} ${UI.BOLD}${UI.BRIGHT_WHITE}❓ $message${UI.RESET}${" ".repeat(55 - message.length)}${UI.YELLOW}│${UI.RESET}")
        println("${UI.YELLOW}└─────────────────────────────────────────────────────────────┘${UI.RESET}")
        println()
        
        return KInquirer.promptConfirm(
            message = "❓ $message",
            default = default
        )
    }
    
    fun showSelectionResult(selected: String, type: String = "option") {
        println()
        println("${UI.BG_GREEN}${UI.BLACK} ✅ SELECTED ${UI.RESET} ${UI.BRIGHT_GREEN}$selected${UI.RESET}")
        println("${UI.DIM}${UI.GRAY}You chose: $selected as your $type${UI.RESET}")
        println()
    }
    
    fun showMultiChoice(
        title: String,
        options: Map<String, String>,
        selectedKey: String? = null
    ) {
        println("${UI.BRIGHT_WHITE}${UI.BOLD}$title${UI.RESET}")
        println("${UI.GRAY}┌─────────────────────────────────────────────────────────────┐${UI.RESET}")
        
        options.forEach { (key, description) ->
            val isSelected = key == selectedKey
            val marker = if (isSelected) "${UI.BRIGHT_GREEN}●${UI.RESET}" else "${UI.GRAY}○${UI.RESET}"
            val keyColor = if (isSelected) UI.BRIGHT_GREEN else UI.BRIGHT_YELLOW
            val descColor = if (isSelected) UI.GREEN else UI.GRAY
            
            println("${UI.GRAY}│${UI.RESET} $marker ${keyColor}$key${UI.RESET} - ${descColor}$description${UI.RESET}")
        }
        
        println("${UI.GRAY}└─────────────────────────────────────────────────────────────┘${UI.RESET}")
    }
}
