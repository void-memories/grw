package dev.namn.cli.utils

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlin.random.Random

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
    
    const val BOLD = "\u001B[1m"
    const val DIM = "\u001B[2m"
    const val ITALIC = "\u001B[3m"
    const val UNDERLINE = "\u001B[4m"
    const val BLINK = "\u001B[5m"
    const val REVERSE = "\u001B[7m"
    const val STRIKETHROUGH = "\u001B[9m"
    
    const val GRADIENT_BLUE = "\u001B[38;5;39m"
    const val GRADIENT_PURPLE = "\u001B[38;5;129m"
    const val GRADIENT_PINK = "\u001B[38;5;205m"
    
    fun clearScreen() {
        print("\u001B[2J\u001B[H")
    }
    
    fun showWelcomeBanner() {
        val banner = """
${GRADIENT_BLUE}
  ██████╗ ██████╗ ██╗    ██╗
 ██╔════╝ ██╔══██╗██║    ██║
 ██║  ███╗██████╔╝██║ █╗ ██║
 ██║   ██║██╔══██╗██║███╗██║
 ╚██████╔╝██║  ██║╚███╔███╔╝
  ╚═════╝ ╚═╝  ╚═╝ ╚══╝╚══╝ 
${RESET}
${GRADIENT_PURPLE}┌─────────────────────────────────────────────────────────────┐${RESET}
${GRADIENT_PURPLE}│${RESET} ${BOLD}${BRIGHT_CYAN}🚀 Welcome to GRW - Gradle Android CLI Tool${RESET}           ${GRADIENT_PURPLE}│${RESET}
${GRADIENT_PURPLE}│${RESET} ${CYAN}✨ Powerful • Fast • Beautiful • Developer-Friendly${RESET}      ${GRADIENT_PURPLE}│${RESET}
${GRADIENT_PURPLE}└─────────────────────────────────────────────────────────────┘${RESET}
        """.trimIndent()
        
        println(banner)
    }
    
    fun showLoadingAnimation(message: String, durationMs: Long) {
        val frames = listOf("⠋", "⠙", "⠹", "⠸", "⠼", "⠴", "⠦", "⠧", "⠇", "⠏")
        val colors = listOf(BRIGHT_BLUE, BRIGHT_CYAN, BRIGHT_GREEN, BRIGHT_YELLOW, BRIGHT_PURPLE)
        
        runBlocking {
            val startTime = System.currentTimeMillis()
            var frameIndex = 0
            
            while (System.currentTimeMillis() - startTime < durationMs) {
                val color = colors[Random.nextInt(colors.size)]
                print("\r${color}${frames[frameIndex]}${RESET} ${CYAN}$message...${RESET}")
                frameIndex = (frameIndex + 1) % frames.size
                delay(100)
            }
            
            println("\r${BRIGHT_GREEN}✅${RESET} ${GREEN}$message completed!${RESET}")
        }
    }
    
    fun showCommandsOverview() {
        val commands = listOf(
            "flavor" to "🎯 Manage product flavors",
            "variant" to "🔧 Handle build variants", 
            "config" to "⚙️  Configure GRW settings",
            "sync" to "🔄 Sync Gradle project",
            "clean" to "🧹 Clean build artifacts",
            "task" to "📋 Execute Gradle tasks",
            "gen" to "🏗️  Generate code/resources",
            "build" to "🚀 Clean and build project",
            "run" to "▶️  Run the application"
        )
        
        println("${BOLD}${BRIGHT_WHITE}📚 Available Commands:${RESET}")
        println("${GRAY}┌─────────────────────────────────────────────────────────────┐${RESET}")
        
        commands.forEach { (cmd, desc) ->
            val cmdText = "grw $cmd"
            val padding = maxOf(0, 20 - cmdText.length)
            println("${GRAY}│${RESET} ${BRIGHT_YELLOW}$cmdText${RESET}${" ".repeat(padding)}${desc} ${GRAY}│${RESET}")
        }
        
        println("${GRAY}└─────────────────────────────────────────────────────────────┘${RESET}")
        println("${DIM}${GRAY}💡 Use ${BRIGHT_WHITE}grw <command> --help${GRAY} for detailed information${RESET}")
    }
    
    fun showSuccess(message: String) {
        println("${BG_GREEN}${BLACK} ✅ SUCCESS ${RESET} ${BRIGHT_GREEN}$message${RESET}")
    }
    
    fun showError(message: String) {
        println("${BG_RED}${WHITE} ❌ ERROR ${RESET} ${BRIGHT_RED}$message${RESET}")
    }
    
    fun showWarning(message: String) {
        println("${BG_YELLOW}${BLACK} ⚠️  WARNING ${RESET} ${BRIGHT_YELLOW}$message${RESET}")
    }
    
    fun showInfo(message: String) {
        println("${BG_BLUE}${WHITE} ℹ️  INFO ${RESET} ${BRIGHT_BLUE}$message${RESET}")
    }
    
    fun showProgressBar(current: Int, total: Int, message: String = "") {
        val percentage = (current.toDouble() / total * 100).toInt()
        val filled = (current.toDouble() / total * 30).toInt()
        val empty = 30 - filled
        
        val bar = "${BRIGHT_GREEN}${"█".repeat(filled)}${GRAY}${"░".repeat(empty)}${RESET}"
        print("\r$bar ${BRIGHT_WHITE}$percentage%${RESET} $message")
        
        if (current == total) {
            println()
        }
    }
    
    fun createBox(title: String, content: List<String>, color: String = CYAN): String {
        val maxWidth = maxOf(title.length, content.maxOfOrNull { it.length } ?: 0) + 4
        val topBorder = "┌${"─".repeat(maxWidth - 2)}┐"
        val bottomBorder = "└${"─".repeat(maxWidth - 2)}┘"
        val titleLine = "│ ${BOLD}$title${RESET}${" ".repeat(maxWidth - title.length - 3)}│"
        
        val result = StringBuilder()
        result.appendLine("$color$topBorder$RESET")
        result.appendLine("$color$titleLine$RESET")
        
        if (content.isNotEmpty()) {
            result.appendLine("$color├${"─".repeat(maxWidth - 2)}┤$RESET")
            content.forEach { line ->
                val paddedLine = "│ $line${" ".repeat(maxWidth - line.length - 3)}│"
                result.appendLine("$color$paddedLine$RESET")
            }
        }
        
        result.appendLine("$color$bottomBorder$RESET")
        return result.toString()
    }
    
    fun showTable(headers: List<String>, rows: List<List<String>>) {
        if (headers.isEmpty() || rows.isEmpty()) return
        
        val columnWidths = headers.indices.map { col ->
            maxOf(
                headers[col].length,
                rows.maxOfOrNull { it.getOrNull(col)?.length ?: 0 } ?: 0
            ) + 2
        }
        
        val totalWidth = columnWidths.sum() + headers.size + 1
        
        println("${GRAY}┌${"─".repeat(totalWidth - 2)}┐${RESET}")
        
        val headerRow = headers.mapIndexed { index, header ->
            "${BOLD}${BRIGHT_WHITE}${header.padEnd(columnWidths[index])}${RESET}"
        }.joinToString("${GRAY}│${RESET}")
        println("${GRAY}│${RESET}$headerRow${GRAY}│${RESET}")
        
        println("${GRAY}├${"─".repeat(totalWidth - 2)}┤${RESET}")
        
        rows.forEach { row ->
            val rowString = row.mapIndexed { index, cell ->
                "${cell.padEnd(columnWidths[index])}"
            }.joinToString("${GRAY}│${RESET}")
            println("${GRAY}│${RESET}$rowString${GRAY}│${RESET}")
        }
        
        println("${GRAY}└${"─".repeat(totalWidth - 2)}┘${RESET}")
    }
    
    fun rainbow(text: String): String {
        val colors = listOf(RED, YELLOW, GREEN, CYAN, BLUE, PURPLE)
        return text.mapIndexed { index, char ->
            "${colors[index % colors.size]}$char"
        }.joinToString("") + RESET
    }
} 