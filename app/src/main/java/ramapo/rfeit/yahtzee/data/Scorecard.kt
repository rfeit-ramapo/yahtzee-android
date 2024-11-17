package ramapo.rfeit.yahtzee.data

class Scorecard {

    companion object {
        // Number of categories in the scorecard
        const val NUM_CATEGORIES = 12
    }

    // The number of categories currently filled
    private var numFilled = 0

    // A mutable list containing Category objects
    private val categories: MutableList<Category> = mutableListOf(
        MultiplesCategory("Aces", "Any combination", "Sum of dice with the number 1", 1),
        MultiplesCategory("Twos", "Any combination", "Sum of dice with the number 2", 2),
        MultiplesCategory("Threes", "Any combination", "Sum of dice with the number 3", 3),
        MultiplesCategory("Fours", "Any combination", "Sum of dice with the number 4", 4),
        MultiplesCategory("Fives", "Any combination", "Sum of dice with the number 5", 5),
        MultiplesCategory("Sixes", "Any combination", "Sum of dice with the number 6", 6),
        KindCategory("Three of a Kind", "At least three dice the same", "Sum of all the dice", 3),
        KindCategory("Four of a Kind", "At least four dice the same", "Sum of all the dice", 4),
        FullHouseCategory("Full House", "Three of one number and two of another", "25"),
        StraightCategory("Four Straight", "Four sequential dice", "30", 4, 30),
        StraightCategory("Five Straight", "Five sequential dice", "40", 5, 40),
        YahtzeeCategory("Yahtzee", "All five dice the same", "50")
    )

    // Selectors

    fun getCategories(): List<Category> = categories

    fun getCategory(index: Int): Category = categories[index]

    fun isFull(): Boolean = numFilled == NUM_CATEGORIES

    fun getCategoryIndex(categoryName: String): Int {
        return categories.indexOfFirst { it.name == categoryName }
    }

    // Functions

    // Fill a specific category with score and winner info
    fun fillCategory(categoryIndex: Int, points: Int, round: Int, winner: String) {
        val category = categories[categoryIndex]
        category.full = true
        category.points = points
        category.round = round
        category.winner = winner
        numFilled++
    }

    // Fill multiple categories at once
    fun fillMultiple(
        categoryIndices: List<Int>,
        scores: List<Int>,
        winners: List<String>,
        rounds: List<Int>,
        humanPlayer: Player,
        pcPlayer: Player
    ) {
        categoryIndices.forEachIndexed { index, categoryIndex ->
            if (winners[index] == "Human") humanPlayer.addScore(scores[index])
            else pcPlayer.addScore(scores[index])

            fillCategory(categoryIndex, scores[index], rounds[index], winners[index])
        }
    }
}