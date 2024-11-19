package ramapo.rfeit.yahtzee.data

import ramapo.rfeit.yahtzee.data.categories.FullHouseCategory
import ramapo.rfeit.yahtzee.data.categories.KindCategory
import ramapo.rfeit.yahtzee.data.categories.MultiplesCategory
import ramapo.rfeit.yahtzee.data.categories.StraightCategory
import ramapo.rfeit.yahtzee.data.categories.YahtzeeCategory

/**
 * Represents the scorecard for a game of Yahtzee, managing multiple scoring categories.
 */
class Scorecard {

    companion object {
        /**
         * The total number of scoring categories in the scorecard.
         */
        const val NUM_CATEGORIES = 12
    }

    /**
     * Tracks the number of scoring categories that have been filled during the game.
     */
    private var numFilled = 0

    /**
     * A list of all scoring categories in the scorecard, each represented by a `Category` object.
     */
    val categories: List<Category> = listOf(
        MultiplesCategory("Aces", "Any combination", "Sum of dice with the number 1", 0),
        MultiplesCategory("Twos", "Any combination", "Sum of dice with the number 2", 1),
        MultiplesCategory("Threes", "Any combination", "Sum of dice with the number 3", 2),
        MultiplesCategory("Fours", "Any combination", "Sum of dice with the number 4", 3),
        MultiplesCategory("Fives", "Any combination", "Sum of dice with the number 5", 4),
        MultiplesCategory("Sixes", "Any combination", "Sum of dice with the number 6", 5),

        KindCategory("Three of a Kind", "At least three dice the same", "Sum of all the dice", 3),
        KindCategory("Four of a Kind", "At least four dice the same", "Sum of all the dice", 4),

        FullHouseCategory("Full House", "Three of one number and two of another", "25"),

        StraightCategory("Four Straight", "Four sequential dice", "30", 4, 30),
        StraightCategory("Five Straight", "Five sequential dice", "40", 5, 40),

        YahtzeeCategory("Yahtzee", "All five dice the same", "50")
    )

    /**
     * Checks if all categories on the scorecard have been filled.
     * @return True if all categories are filled; false otherwise.
     *
     * @reference Used ChatGPT to convert C++ version functions and classes before clean-up.
     */
    fun isFull(): Boolean = numFilled == NUM_CATEGORIES

    /**
     * Retrieves the index of a category by its name.
     * @param categoryName The name of the category to find.
     * @return The index of the category, or -1 if not found.
     *
     * @reference Used ChatGPT to convert C++ version functions and classes before clean-up.
     */
    fun getCategoryIndex(categoryName: String): Int {
        return categories.indexOfFirst { it.name == categoryName }
    }

    // Methods to modify the scorecard

    /**
     * Marks a specific category as filled, recording its score, round, and winner information.
     * @param categoryIndex The index of the category to fill.
     * @param points The points scored in the category.
     * @param round The round number when the category was filled.
     * @param winner The name of the player who won this category.
     *
     * @reference Used ChatGPT to convert C++ version functions and classes before clean-up.
     */
    fun fillCategory(categoryIndex: Int, points: Int, round: Int, winner: String) {
        val category = categories[categoryIndex]
        category.full = true
        category.points = points
        category.round = round
        category.winner = winner
        numFilled++
    }

    /**
     * Fills multiple categories at once, updating scores and tracking who won each category.
     * @param categoryIndices List of indices for the categories to fill.
     * @param scores List of scores corresponding to each category.
     * @param winners List of winners for each category.
     * @param rounds List of rounds in which each category was filled.
     * @param humanPlayer The human player for this game.
     * @param pcPlayer The computer player for this game.
     *
     * @reference Used ChatGPT to convert C++ version functions and classes before clean-up.
     */
    fun fillMultiple(
        categoryIndices: List<Int>,
        scores: List<Int>,
        winners: List<String>,
        rounds: List<Int>,
        humanPlayer: Player,
        pcPlayer: Player
    ) {
        categoryIndices.forEachIndexed { index, categoryIndex ->
            // Add scores to the appropriate player.
            if (winners[index] == "Human") humanPlayer.addScore(scores[index])
            else pcPlayer.addScore(scores[index])

            // Fill the specific category with the corresponding details.
            fillCategory(categoryIndex, scores[index], rounds[index], winners[index])
        }
    }
}
