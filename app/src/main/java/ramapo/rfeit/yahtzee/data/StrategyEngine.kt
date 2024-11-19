package ramapo.rfeit.yahtzee.data

/**
 * A utility class responsible for evaluating possible strategies and selecting the optimal one
 * based on the current game state, scorecard, and dice configuration.
 * Provides methods to determine possible scoring categories and compute the best strategy.
 */
class StrategyEngine {

    /**
     * Determines the list of scoring categories that are possible based on the current dice set
     * and the state of the scorecard. Optionally applies stricter conditions to exclude certain categories.
     *
     * @param scorecard The [Scorecard] object representing the current state of the game.
     * @param dice A [Dice] object representing the current dice configuration.
     * @param isStrict A [Boolean] indicating whether to apply strict rules for category inclusion.
     *                 If `true`, excludes Multiples categories unless the current dice match.
     *
     * @return A mutable list of integers, where each integer is the index of a possible scoring category.
     *
     * @reference Used ChatGPT to convert C++ version functions and classes before clean-up.
    */
    fun getPossibleCategories(scorecard: Scorecard, dice: Dice, isStrict: Boolean = false): MutableList<Int> {
        // Set up the list to hold category indices.
        val possibleCategories = mutableListOf<Int>()

        // Retrieve the categories from the scorecard
        val categories = scorecard.categories

        // Check if max possible score > 0 (if the category is possible given current dice set)
        for (i in 0 until Scorecard.NUM_CATEGORIES) {
            val strategy = categories[i].getRerollStrategy(dice)
            // If possible add the category. Skip Multiples if none of the current dice
            if (strategy != null && strategy.maxScore != 0) {
                // Skip Multiples if in "strict" mode and none of the current dice match
                if (isStrict && i < 6 && strategy.currentScore == 0) continue
                // Save the index of all possible categories.
                possibleCategories.add(i)
            }
        }

        return possibleCategories
    }

    /**
     * Computes the best strategy based on the current dice set and the state of the scorecard.
     * Iterates through all categories to determine the strategy that maximizes the potential score.
     *
     * @param scorecard The [Scorecard] object representing the current state of the game.
     * @param dice A [Dice] object representing the current dice configuration.
     *
     * @return A [Strategy] object representing the best strategy to maximize the score,
     *         or `null` if no valid strategy is found.
     *
     * @reference Used ChatGPT to convert C++ version functions and classes before clean-up.
     */
    fun strategize(scorecard: Scorecard, dice: Dice): Strategy? {
        // Retrieve the categories from the scorecard
        val categories = scorecard.categories

        // Use default constructor to initialize the best strategy
        var bestStrategy: Strategy? = null

        // Iterate over each category
        for (category in categories) {
            val testStrategy = category.getRerollStrategy(dice) ?: continue

            // Update the best strategy based on the score
            if ((bestStrategy?.compareTo(testStrategy) ?: 0) <= 0) {
                bestStrategy = testStrategy
            }
        }
        return bestStrategy
    }
}