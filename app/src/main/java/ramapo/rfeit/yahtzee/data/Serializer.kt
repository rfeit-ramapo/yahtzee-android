package ramapo.rfeit.yahtzee.data

import android.content.Context
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.flow.MutableStateFlow
import java.io.File
import java.io.PrintWriter

class Serializer(private val context: Context?) {

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
            scorecard.value?.fillMultiple(
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
            // Open the file for writing using context.openFileOutput()
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