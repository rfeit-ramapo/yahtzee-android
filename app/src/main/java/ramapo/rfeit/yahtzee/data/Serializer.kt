package ramapo.rfeit.yahtzee.data

import android.content.Context
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.flow.MutableStateFlow
import java.io.File
import java.io.PrintWriter

/**
 * The `Serializer` class handles saving and loading game state to/from files.
 * It is responsible for serializing the current round, scorecard, and player data,
 * as well as parsing previously saved data to restore game progress.
 *
 * @param context the Android context used for file operations. If null, saving and loading will always fail.
 */
class Serializer(private val context: Context?) {

    /**
     * Loads game data from a file, updating the round number, scorecard, and player objects.
     *
     * @param roundNum [MutableLiveData] of the round number.
     * @param scorecard [MutableStateFlow] of the game's scorecard.
     * @param humPlayer [MutableLiveData] of the human player object.
     * @param compPlayer [MutableLiveData] of the computer player object.
     * @param filename Name of the file to load data from.
     *
     * @return True if the file is successfully loaded and parsed; false otherwise.
     *
     * @reference Used ChatGPT to ask about accessing file directory for Android and to convert
     * C++ version functions and classes before clean-up.
     *
     * @algorithm
     * 1) Check if the context is null or if the file does not exist, and return false if either is true.
     * 2) Open the file and iterate through its lines, trimming whitespace and skipping blank lines.
     * 3) Parse the "Round:" line to extract the current round number.
     * 4) Detect the "Scorecard:" line and begin parsing scorecard entries.
     * 5) For each scorecard entry:
     *    a) Skip entries with a score of 0.
     *    b) Validate and parse the score, winner, and round number.
     *    c) Add the parsed data to temporary lists for categories, scores, winners, and rounds.
     * 6) Validate that all categories are accounted for (exactly 12 entries).
     * 7) Update the round number, scorecard, and player objects with the parsed data.
     * 8) Return true if all steps complete successfully, or false if an error occurs.
     */
    fun loadGame(
        roundNum: MutableLiveData<Int>,
        scorecard: MutableStateFlow<Scorecard>,
        humPlayer: MutableLiveData<Human>,
        compPlayer: MutableLiveData<Computer>,
        filename: String): Boolean {

        if (context == null) return false
        val file = File(context.filesDir, filename)
        if (!file.exists()) return false

        // Read the file line by line
        val categories = mutableListOf<Int>()
        val scores = mutableListOf<Int>()
        val winners = mutableListOf<String>()
        val rounds = mutableListOf<Int>()

        var currentRound = 0
        var categoryIndex = 0
        var readingScorecard = false

        try {
            // Parse the "Round:" part of the file
            file.forEachLine { line ->
                // Trim whitespace from the line to ignore lines with only spaces or tabs
                val trimmedLine = line.trim()

                // Skip blank or empty lines
                if (trimmedLine.isEmpty()) return@forEachLine

                println("Line: $trimmedLine")  // Log for debugging

                val words = trimmedLine.split("\\s+".toRegex()) // Split by spaces

                when {
                    // Parsing the Round line
                    words.firstOrNull() == "Round:" -> {
                        currentRound = words.getOrNull(1)?.toIntOrNull()
                            ?: throw Exception("Invalid round number")
                    }
                    // Start reading scorecard entries after "Scorecard:" line
                    words.firstOrNull() == "Scorecard:" -> {
                        readingScorecard = true
                        return@forEachLine
                    }
                    // Processing scorecard entries
                    readingScorecard -> {
                        // Ensure we're not exceeding the number of categories
                        if (categoryIndex == Scorecard.NUM_CATEGORIES) throw Exception("Too many categories in file")

                        // Parse score and handle 0 scores (skip them)
                        val score = words.getOrNull(0)?.toIntOrNull() ?: throw Exception("Invalid score")
                        if (score == 0) {
                            // If score is 0, skip this entry
                            categoryIndex++
                            return@forEachLine
                        }

                        // Parse winner (should be either "Human" or "Computer")
                        val winner = words.getOrNull(1) ?: throw Exception("Missing winner")
                        if (winner != "Human" && winner != "Computer") {
                            throw Exception("Invalid winner name")
                        }

                        // Parse round number for this entry
                        val roundNumber = words.getOrNull(2)?.toIntOrNull()
                            ?: throw Exception("Invalid round number")

                        // Update lists with parsed data
                        categories.add(categoryIndex)
                        scores.add(score)
                        winners.add(winner)
                        rounds.add(roundNumber)

                        categoryIndex++
                    }
                    else -> {
                        // Skip any unrecognized lines
                        return@forEachLine
                    }
                }
            }

            // Check that we've parsed exactly 12 entries
            if (categoryIndex != Scorecard.NUM_CATEGORIES) {
                throw Exception("Expected 12 scorecard entries, but found $categoryIndex")
            }

            // Update round number and scorecard with parsed data
            roundNum.value = currentRound
            scorecard.value.fillMultiple(
                categories,
                scores,
                winners,
                rounds,
                humPlayer.value!!,
                compPlayer.value!!
            )

            // Print success message and updated scorecard
            println("Serialization file successfully processed!")
            return true
        } catch (e: Exception) {
            println("Error processing the file: ${e.message}")
            return false
        }
    }

    /**
     * Saves the current game state to a file, including the round number and scorecard data.
     *
     * @param currentRound Current round number to save.
     * @param scorecard [Scorecard] object containing the game's score data.
     * @param fileName Name of the file to save data to.
     *
     * @return True if the game is successfully saved; false otherwise.
     *
     * @reference Used ChatGPT to ask about accessing file directory for Android and to convert
     * C++ version functions and classes before clean-up.
     *
     * @algorithm
     * 1) Check if the context is null, and return false if it is.
     * 2) Open the file for writing using `context.openFileOutput()` in private mode.
     * 3) Write the serialization header, including the current round number.
     * 4) Write the "Scorecard:" line to mark the start of scorecard data.
     * 5) Iterate through each category in the scorecard:
     *    a) Write a 0 for categories that are not filled.
     *    b) Write the score, winner, and round number for filled categories.
     * 6) Close the writer and confirm successful saving with a console message.
     * 7) Catch any exceptions that occur, log the error, and return false.
     */
    fun saveGame(
        currentRound: Int,
        scorecard: Scorecard,
        fileName: String
    ): Boolean {
        if (context == null) {
            println("Context is null, cannot save game.")
            return false
        }

        return try {
            // Open the file for writing
            val fileOutputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE)
            val writer = PrintWriter(fileOutputStream)

            // Output serialization header, including current round
            writer.println("Round: $currentRound\n")
            writer.println("Scorecard:")

            // Loop through each category and save data
            val categories = scorecard.categories
            for (i in 0 until Scorecard.NUM_CATEGORIES) {
                // If this category is not filled, just write a 0
                if (!categories[i].full) {
                    writer.println("0")
                } else {
                    // Otherwise, output all info to the file
                    writer.println(
                        "${categories[i].points} " +
                                "${categories[i].winner} " +
                                "${categories[i].round}"
                    )
                }
            }

            // Close the writer
            writer.close()

            // Confirm success
            println("Game saved successfully!")
            true

        } catch (e: Exception) {
            // Handle any errors, such as permissions or file IO issues
            e.printStackTrace() // This will log the full exception stack trace to Logcat
            println("Error saving the game: ${e.message}")
            false
        }
    }

}