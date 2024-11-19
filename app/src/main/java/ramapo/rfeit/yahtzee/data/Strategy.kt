package ramapo.rfeit.yahtzee.data

/**
 * Represents a strategy for a player in the game, containing information about the current
 * and maximum potential scores, the target category, and the dice configuration.
 * Implements [Comparable] to allow strategies to be compared based on their scores.
 *
 * @param currentScore The current score achievable by this strategy.
 * @param maxScore The maximum potential score achievable by this strategy.
 * @param categoryName The name of the category this strategy targets.
 * @param dice A [Dice] object representing the dice set this strategy is based on.
 * @param targetDice A [List] of integers indicating the dice values targeted by this strategy.
 * @param rerollCounts A [List] of integers representing the counts of each die face to be rerolled.
 */
class Strategy(
    internal var currentScore: Int = 0,
    internal var maxScore: Int = 0,
    internal var categoryName: String = "",
    internal var dice: Dice? = null,
    private var targetDice: List<Int> = emptyList(),
    internal var rerollCounts: List<Int> = emptyList()
): Comparable<Strategy> {

    /**
     * Compares this `Strategy` object with another `Strategy` object.
     *
     * @param other The other `Strategy` to compare with.
     * @return A negative integer if this object's `maxScore` is less than the other;
     *         0 if they are equal; a positive integer if this object's `maxScore`
     *         is greater than the other.
     *
     * @reference Used ChatGPT to ask about Comparable Interface in Kotlin.
     */
    override fun compareTo(other: Strategy): Int {
        return maxScore.compareTo(other.maxScore)
    }

    /**
     * Generates a user-friendly string describing the strategy based on the current game state.
     *
     * @param isForHuman Boolean indicating whether the string is for a human or the computer.
     *                   If `true`, the string will be phrased as a recommendation for the human player.
     *                   If `false`, the string will be phrased as the computer's strategy.
     *
     * @return a string describing the strategy, with specific information about the
     *
     * @algorithm
     * 1) Initialize an empty string `stratString` to hold the strategy description.
     * 2) Check if the current score is equal to the maximum possible score:
     *    a) If so, the strategy will recommend standing and targeting the highest-scoring category.
     *    b) If not, the strategy will recommend rerolling the necessary dice to maximize the score.
     * 3) For both strategies, customize the message depending on whether the recommendation is for a human or the computer.
     * 4) For rerolling, add a cautionary note if the current score is 0, and mention the minimum points achievable.
     * 5) Return the constructed strategy string.
     *
     * @reference Used ChatGPT to convert C++ version functions and classes before clean-up.
     */
    fun getString(isForHuman: Boolean): String {
        var stratString = ""

        // Stand strategy - already reached maximum score
        if (currentScore == maxScore) {
            stratString = if (isForHuman) {
                "I recommend that you stand and go for the $categoryName category with your current dice set because it gives the maximum possible points ($maxScore) among all the options.\n"
            } else {
                "The computer plans to stand and go for the $categoryName category with its current dice set because it gives the maximum possible points ($maxScore) among all the options.\n"
            }
        }

        // Reroll strategy - current score is not at its maximum potential
        else {
            stratString = if (isForHuman) {
                "I recommend that you try for the $categoryName category " +
                        (if (targetDice.sum() > 0) "with ${printDice(targetDice)}" else "") +
                                " because it gives the maximum possible points ($maxScore) among all the options. Therefore, ${printDice(rerollCounts)} should be rerolled.\n" +
                                if (currentScore == 0) {
                                    "However, depending on dice rolls you may not be able to score in this category, so be cautious!\n"
                                } else {
                                    "At minimum, you will score $currentScore points in this category.\n"
                                }
            } else {
                "The computer plans to try for the $categoryName category " +
                        (if (targetDice.sum() > 0) "with ${printDice(targetDice)}" else "") +
                                " because it gives the maximum possible points ($maxScore) among all the options. Therefore, ${printDice(rerollCounts)} will be rerolled.\n" +
                                if (currentScore != 0) {
                                    "At minimum, the computer will score $currentScore points in this category.\n"
                                } else ""
            }
        }
        return stratString
    }

    /**
     * Generates a user-friendly string listing the counts of each die face in the provided list.
     *
     * @param diceValues A list of integers where each element represents the count of a specific
     *                     die face (from 1 to 6) in the current set of dice. For example, the first
     *                     element represents the count of "Ace" faces, the second represents "Two"
     *                     faces, and so on.
     *
     * @return A formatted string listing the number of dice for each face in a human-readable format.
     *         The string will be in the form of "1 Ace, 2 Twos, 3 Fours", etc., with "and" added
     *         before the last die face listed. Plurals are correctly handled for counts greater than 1.
     *
     * @algorithm
     * 1) Initialize an empty string `fullString` to hold the final result.
     * 2) Calculate the total number of dice using the sum of values in `a_diceValues`.
     * 3) Initialize a variable `multipleFaces` to determine if "and" should be used before the last die face.
     * 4) Iterate through the `a_diceValues` list, where each index represents a die face:
     *    a) For each non-zero count, generate the appropriate string for the die face (e.g., "Ace", "Two", "Sixes").
     *    b) If there are multiple die faces, add a comma and "and" appropriately before the last one.
     * 5) Return the formatted string after processing all dice values.
     *
     * @reference Used ChatGPT to convert C++ version functions and classes before clean-up.
     */
    private fun printDice(diceValues: List<Int>): String {
        var fullString = ""

        var diceCounted = 0
        val totalDice = diceValues.sum()
        // Record if multiple faces were listed in order to determine if "and" is required
        var multipleFaces = false

        for (i in 0 until Dice.NUM_DICE_FACES) {
            val value = i + 1
            val count = diceValues[i]
            diceCounted += count

            if (count > 0) {
                var valueString = when (value) {
                    1 -> "Ace"
                    2 -> "Two"
                    3 -> "Three"
                    4 -> "Four"
                    5 -> "Five"
                    6 -> "Six"
                    else -> ""
                }

                // Account for plurals
                if (count > 1) {
                    valueString = if (value == 6) "Sixes" else "${valueString}s"
                }

                // If it's the last die in the list
                if (diceCounted == totalDice) {
                    if (multipleFaces) fullString += "and "
                    fullString += "$count $valueString "
                    return fullString
                }

                multipleFaces = true
                fullString += "$count $valueString, "
            }
        }
        return fullString
    }
}
