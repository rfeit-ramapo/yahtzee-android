package ramapo.rfeit.yahtzee.data

class Strategy(
    var currentScore: Int = 0,
    var maxScore: Int = 0,
    var categoryName: String = "",
    var dice: Dice? = null,
    var targetDice: List<Int> = emptyList(),
    var rerollCounts: List<Int> = emptyList()
) {

    // Return whether this strategy requires the player to stand
    fun planToStand(): Boolean = maxScore == currentScore
    fun getRerollDice(): List<Int> = rerollCounts

    // Operator overloads for comparison based on maxScore
    infix fun lessThan(s: Strategy): Boolean = maxScore < s.maxScore
    infix fun equalsTo(s: Strategy): Boolean = maxScore == s.maxScore
    infix fun greaterThan(s: Strategy): Boolean = maxScore > s.maxScore
    infix fun lessThanOrEqual(s: Strategy): Boolean = maxScore <= s.maxScore
    infix fun greaterThanOrEqual(s: Strategy): Boolean = maxScore >= s.maxScore
    infix fun notEquals(s: Strategy): Boolean = maxScore != s.maxScore

    // Print the strategy in user-friendly format
    fun print(suggest: Boolean) {
        println(getString(suggest))
    }

    // Creates a user-friendly string describing this strategy
    fun getString(suggest: Boolean): String {
        var stratString = ""

        // Stand strategy - no fillable categories
        if (maxScore == 0) {
            stratString = if (suggest) {
                "I recommend that you stand because there are no fillable categories given your current dice set.\n"
            } else {
                "The computer plans to stand because there are no fillable categories given its current dice set.\n"
            }
        }

        // Stand strategy - already reached maximum score
        else if (currentScore == maxScore) {
            stratString = if (suggest) {
                "I recommend that you try for the $categoryName category with your current dice set because it gives the maximum possible points ($maxScore) among all the options.\n"
            } else {
                "The computer plans to stand and try for the $categoryName category with its current dice set because it gives the maximum possible points ($maxScore) among all the options.\n"
            }
        }

        // Reroll strategy - current score is not at its maximum potential
        else {
            stratString = if (suggest) {
                "I recommend that you try for the $categoryName category " +
                        if (targetDice.sum() > 0) "with ${printDice(targetDice)}" else "" +
                                " because it gives the maximum possible points ($maxScore) among all the options. Therefore, ${printDice(rerollCounts)} should be rerolled.\n" +
                                if (currentScore == 0) {
                                    "However, depending on dice rolls you may not be able to score in this category, so be cautious!\n"
                                } else {
                                    "At minimum, you will score $currentScore points in this category.\n"
                                }
            } else {
                "The computer plans to try for the $categoryName category " +
                        if (targetDice.sum() > 0) "with ${printDice(targetDice)}" else "" +
                                " because it gives the maximum possible points ($maxScore) among all the options. Therefore, ${printDice(rerollCounts)} will be rerolled.\n" +
                                if (currentScore != 0) {
                                    "At minimum, the computer will score $currentScore points in this category.\n"
                                } else ""
            }
        }

        return stratString
    }

    // Enacts this strategy for a Computer player by filling the scorecard
    fun enact(a_scorecard: Scorecard, a_round: Int) {
        // If this strategy is not scoring, do not fill any categories.
        if (currentScore == 0) {
            println("There is no way to score with the current dice set and open categories. Skipping turn.\n")
            return
        }

        // Fill the category with info
        val categoryIndex = a_scorecard.getCategoryIndex(categoryName)
        a_scorecard.fillCategory(categoryIndex, currentScore, a_round, "Computer")
        println("Filling the $categoryName category with a score of $currentScore points.\n")
    }

    // Gets a string listing dice counts in a user-friendly format
    fun printDice(a_diceValues: List<Int>): String {
        var fullString = ""

        var diceCounted = 0
        val totalDice = a_diceValues.sum()
        // Record if multiple faces were listed in order to determine if "and" is required
        var multipleFaces = false

        for (i in 0 until Dice.NUM_DICE_FACES) {
            val value = i + 1
            val count = a_diceValues[i]
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
