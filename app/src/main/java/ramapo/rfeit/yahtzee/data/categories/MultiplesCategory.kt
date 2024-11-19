package ramapo.rfeit.yahtzee.data.categories

import ramapo.rfeit.yahtzee.data.Category
import ramapo.rfeit.yahtzee.data.Dice
import ramapo.rfeit.yahtzee.data.Strategy

/**
 * Represents the Multiples scoring category in the Yahtzee game.
 * Multiples consists of at least one die of a given face.
 * Scoring this category awards the sum of all dice of the indicated face.
 *
 * @param name the name of the category.
 * @param description a description of how to qualify for this category.
 * @param score a [String] of how to calculate points earned for this category.
 * @param multipleIndex the index of the dice face required (face value - 1)
 */
class MultiplesCategory(
    name: String,
    description: String,
    score: String,
    private val multipleIndex: Int,
) : Category(name, description, score) {

    /**
     * Calculates the score for Multiples category based on the given dice.
     * Multiples requires at least one die matching the required face (e.g. a 1 for Aces).
     *
     * @param dice the set of dice to calculate the score for.
     * @return the score received in this category for the given diceset.
     *
     * @reference Used ChatGPT to convert C++ version functions and classes before clean-up.
     */
    override fun score(dice: Dice): Int {
        // The score = how many times this face appears * the value of the face
        return dice.diceCount[multipleIndex] * (multipleIndex + 1)
    }

    /**
     * Determines the reroll strategy for attempting to score a Multiples category.
     * This considers the current dice and locked state to suggest an ideal reroll strategy.
     * Returns `null` if scoring is impossible.
     *
     * @param dice the set of dice to analyze for reroll strategy.
     * @return a [Strategy] object with the ideal reroll strategy, or `null` if impossible to score.
     *
     * @algorithm
     * 1) Return an empty strategy if the category is full
     * 2) Get the current score and dice values
     * 3) Determine what the ideal dice set would be for maximum points
     * 4) Return a strategy using the ideal dice set
     *
     * @reference Used ChatGPT to convert C++ version functions and classes before clean-up.
     */
    override fun getRerollStrategy(dice: Dice): Strategy? {
        // Return a default, 0 score strategy if this category has already been filled.
        if (full) return null

        val currentScore = score(dice)

        // Try to determine what the best possible roll would look like.
        val perfectScore = MutableList(Dice.NUM_DICE_FACES) { 0 }
        perfectScore[multipleIndex] = Dice.NUM_DICE

        // Find all dice that are not locked and are not contributing to the current score.
        val unlockedUnscored = dice.getUnlockedUnscored(perfectScore)

        // Determine how many dice can be rerolled.
        val rerolledDice = unlockedUnscored.sum()

        // Add all possible rerolls to the scoring face.
        perfectScore[multipleIndex] = dice.diceCount[multipleIndex] + rerolledDice

        // Calculate maximum score from maximum possible dice of desired face
        val maxScore = (multipleIndex + 1) * perfectScore[multipleIndex]

        // Stand if there are no possible dice to reroll, or create a reroll strategy.
        return if (rerolledDice == 0) {
            Strategy(currentScore, maxScore, name)
        } else {
            Strategy(currentScore, maxScore, name, dice, perfectScore, unlockedUnscored)
        }
    }
}
