package ramapo.rfeit.yahtzee.data

import android.content.Context
import java.io.File
import java.io.PrintWriter

/**
 * A utility class for logging game events to a file or the console.
 *
 * @param context the Android context used for file operations. If null, logs will only be printed to the console.
 */
class Logger(private val context: Context?) {

    companion object {
        /**
         * The name of the log file where events are recorded.
         */
        const val LOG_FILE = "log.txt"
    }

    init {
        // Initialize the log file when the Logger instance is created.
        initializeLog()
    }

    /**
     * Initializes the log by deleting any existing log file and starting a new log with an initial entry.
     *
     * @reference Used ChatGPT to ask about accessing file directory for Android.
     */
    private fun initializeLog() {
        if (context == null) {
            println("Context is null; cannot initialize log file.")
            return
        }

        try {
            val logFile = File(context.filesDir, LOG_FILE)

            // Delete the log file if it exists.
            if (logFile.exists()) {
                logFile.delete()
            }

            // Write the initial line to a new log file.
            logLine("Game Started")

        } catch (e: Exception) {
            // Log any exceptions that occur during initialization.
            println("Error initializing log file: ${e.message}")
            e.printStackTrace()
        }
    }

    /**
     * Writes a single line to the log file or prints it to the console if the context is null.
     *
     * @param line the line to be written to the log file.
     * @return true if the line was successfully written to the log file; false otherwise.
     *
     * @reference Used ChatGPT to ask about accessing file directory for Android.
     */
    fun logLine(line: String): Boolean {
        // If context is null, print the log line to the console instead.
        if (context == null) {
            println("Context is null; cannot save to log file.")
            println(line)
            return false
        }

        try {
            // Open the log file in append mode and write the line.
            val fileOutputStream = context.openFileOutput(LOG_FILE, Context.MODE_APPEND)
            val writer = PrintWriter(fileOutputStream)

            // Print the line to the file and close the writer.
            writer.println(line)
            writer.close()
            return true

        } catch (e: Exception) {
            // Handle any errors during the write operation, such as file permissions or IO issues.
            println("Error saving the game: ${e.message}")
            e.printStackTrace()
            return false
        }
    }

    /**
     * Reads the contents of the log file and returns them as a string.
     *
     * @return the contents of the log file, or a default message if the log file does not exist or context is null.
     *
     * @reference None.
     */
    fun readLog(): String {
        if (context == null) return "No log entries found."
        val logFile = File(context.filesDir, LOG_FILE)

        // Check if the log file exists and read its contents, or return a default message if it does not.
        return if (logFile.exists()) logFile.readText() else "No log entries found."
    }
}