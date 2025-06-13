package dev.namn.cli.utils

object Loader {
    private val frames = arrayOf(
        "⠋", "⠙", "⠹", "⠸", "⠼", "⠴", "⠦", "⠧", "⠇", "⠏"
    )

    private const val ANSI_CYAN = "\u001B[36m"
    private const val ANSI_RESET = "\u001B[0m"

    @Volatile
    private var running = false
    private var thread: Thread? = null

    fun start(message: String = "") {
        if (running) return
        running = true

        thread = Thread {
            var i = 0
            while (running) {
                val frame = frames[i % frames.size]
                print("\r$ANSI_CYAN$frame$ANSI_RESET $message")
                System.out.flush()
                i++
                try {
                    Thread.sleep(80) // a bit faster
                } catch (e: InterruptedException) {
                    Thread.currentThread().interrupt()
                    break
                }
            }
        }.apply {
            isDaemon = true
            start()
        }
    }

    fun stop() {
        if (!running) return
        running = false
        try {
            thread?.join()
        } catch (ignored: InterruptedException) {
            Thread.currentThread().interrupt()
        }

        print("\r\u001B[2K")
        System.out.flush()
    }
}
