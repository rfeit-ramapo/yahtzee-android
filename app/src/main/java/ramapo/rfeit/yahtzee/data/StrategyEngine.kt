package ramapo.rfeit.yahtzee.data;

import java.util.*

class StrategyEngine(private val scorecard: Scorecard? = null) {

    // Selector
    fun getScorecard(): Scorecard? = scorecard

    // Functions

    /**
     * Function Name: GetPossibleCategories
     * Purpose: Checks all categories that can be achieved, given locked dice
     * Parameters:
     *    a_dice, a reference to a Dice object to compare against
     * Return Value: a list of category indices that are possible for this dice set
     */
    fun getPossibleCategories(aDice: Dice): List<Int> {
        // Set up the list to hold category indices.
        val possibleCategories = mutableListOf<Int>()

        // Retrieve the categories from the scorecard
        val categories = scorecard?.getCategories() ?: return possibleCategories

        // Check if max possible score > 0 (if the category is possible given current dice set)
        for (i in 0 until Scorecard.NUM_CATEGORIES) {
            if (categories[i].getRerollStrategy(aDice).maxScore > 0) {
                // Save the index of all possible categories.
                possibleCategories.add(i)
            }
        }

        return possibleCategories
    }

    /**
     * Function Name: Strategize
     * Purpose: Determines the best strategy to follow based on the given dice set
     * Parameters:
     *    a_dice, a reference to a Dice object to analyze
     * Return Value: the best Strategy found
     */
    fun strategize(aDice: Dice): Strategy {
        // Retrieve the categories from the scorecard
        val categories = scorecard?.getCategories() ?: return Strategy()

        // Use default constructor to initialize the best strategy
        var bestStrategy = Strategy()

        // Iterate over each category
        for (category in categories) {
            // Call GetRerollStrategy() on the category object
            val testStrategy = category.getRerollStrategy(aDice)

            // Update the best strategy based on the score
            if (bestStrategy.lessThanOrEqual(testStrategy)) {
                bestStrategy = testStrategy
            }
        }

        return bestStrategy
    }
}