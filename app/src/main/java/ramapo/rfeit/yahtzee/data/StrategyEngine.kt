package ramapo.rfeit.yahtzee.data

class StrategyEngine {

    fun getPossibleCategories(scorecard: Scorecard, dice: Dice, isStrict: Boolean = false): MutableList<Int> {
        // Set up the list to hold category indices.
        val possibleCategories = mutableListOf<Int>()

        // Retrieve the categories from the scorecard
        val categories = scorecard.categories

        // Check if max possible score > 0 (if the category is possible given current dice set)
        for (i in 0 until Scorecard.NUM_CATEGORIES) {
            val strategy = categories[i].getRerollStrategy(dice)
            // If possible add the category. Skip Multiples if none of the current dice
            if (strategy != null) {
                // Skip Multiples if in "strict" mode and none of the current dice match
                if (isStrict && (!(i < 6 && strategy.currentScore == 0))) continue
                // Save the index of all possible categories.
                possibleCategories.add(i)
            }
        }

        return possibleCategories
    }

    fun strategize(scorecard: Scorecard, dice: Dice): Strategy? {
        // Retrieve the categories from the scorecard
        val categories = scorecard.categories

        // Use default constructor to initialize the best strategy
        var bestStrategy: Strategy? = null

        // Iterate over each category
        for (category in categories) {
            // Call GetRerollStrategy() on the category object
            val testStrategy = category.getRerollStrategy(dice) ?: continue

            // Update the best strategy based on the score
            if (bestStrategy?.lessThanOrEqual(testStrategy) != false) {
                bestStrategy = testStrategy
            }
        }

        return bestStrategy
    }
}