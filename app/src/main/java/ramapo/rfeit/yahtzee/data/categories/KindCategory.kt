package ramapo.rfeit.yahtzee.data.categories

import ramapo.rfeit.yahtzee.data.Category
import ramapo.rfeit.yahtzee.data.Dice
import ramapo.rfeit.yahtzee.data.Strategy

/**
 * Represents a Kind scoring category in the Yahtzee game.
 * A Kind category consists of three or four dice showing the same number, depending on type.
 * Scoring this category awards points equal to the sum of all dice faces.
 *
 * @param name the name of the category.
 * @param description a description of how to qualify for this category.
 * @param score a [String] of how to calculate points earned for this category.
 * @param numKind how many of the same face that are needed
 */
class KindCategory(
    name: String,
    description: String,
    score: String,
    private val numKind: Int,
) : Category(name, description, score) {

    /**
     * Calculates the score for the Kind category based on the given dice.
     * The Kind requires three or four dice of the same face value.
     *
     * @param dice the set of [Dice] to calculate the score for.
     * @return the score received in this category for the given diceset.
     *
     * @reference Used ChatGPT to convert C++ version functions and classes before clean-up.
     */
    override fun score(dice: Dice): Int {
        var conditionMet = false
        var score = 0

        // Loop through counts for each face
        for (i in dice.diceCount.indices) {
            // Calculate score by adding face value * count
            score += dice.diceCount[i] * (i + 1)
            // If there are enough of the same kind, the condition is met
            if (dice.diceCount[i] >= numKind) conditionMet = true
        }
        // Either 0, or the sum of all dice faces
        return score * if (conditionMet) 1 else 0
    }

    /**
     * Calculates the score for the Kind category based on the given dice.
     * The Kind requires three or four dice of the same face value.
     *
     * @param diceValues the set of dice as a [MutableList] of [Integer] to calculate the score for.
     * @return the score received in this category for the given diceset.
     *
     * @reference Used ChatGPT to convert C++ version functions and classes before clean-up.
     */
    private fun score(diceValues: MutableList<Int>): Int {
        var conditionMet = false
        var score = 0

        // Loop through counts for each face
        for (i in diceValues.indices) {
            // Calculate score by adding face value * count
            score += diceValues[i] * (i + 1)
            // If there are enough of the same kind, the condition is met
            if (diceValues[i] >= numKind) conditionMet = true
        }
        // Either 0, or the sum of all dice faces
        return score * if (conditionMet) 1 else 0
    }

    /**
    * Determines the reroll strategy for attempting to score a Kind.
    * This considers the current dice and locked state to suggest an ideal reroll strategy.
    * Returns `null` if scoring is impossible.
    *
    * @param dice the set of dice to analyze for reroll strategy.
    * @return a [Strategy] object with the ideal reroll strategy, or `null` if impossible to score.
    *
    * @reference Used ChatGPT to convert C++ version functions and classes before clean-up.
    */
    override fun getRerollStrategy(dice: Dice): Strategy? {
        // Return a default, 0 score strategy if this category has already been filled.
        if (full) return null

        val currentScore = score(dice)

        // Try to determine what the best possible roll would look like.
        var idealDice: MutableList<Int>
        var rerollable: MutableList<Int>

        // Loop through each dice face counting down. (The best score comes from higher faces.)
        for (i in Dice.NUM_DICE_FACES - 1 downTo 0) {
            val minimumScore = MutableList(Dice.NUM_DICE_FACES) { 0 }
            minimumScore[i] = numKind

            // Set rerollable as any non-essential dice for scoring.
            rerollable = dice.getUnlockedUnscored(minimumScore).toMutableList()

            // Ideal dice set must at minimum include locked dice.
            idealDice = dice.locked.toMutableList()

            // Loop through each dice face value to convert rerollable dice
            for (j in 0 until Dice.NUM_DICE_FACES) {
                var rerollableNum = rerollable[j]

                // Loop while there are rerollable dice and scoring face does not have enough.
                while (rerollableNum > 0 && idealDice[i] < numKind) {
                    rerollableNum--
                    idealDice[i]++
                }
                // Loop while there are rerollable dice (and scoring face has enough).
                while (rerollableNum > 0) {
                    rerollableNum--
                    idealDice[Dice.NUM_DICE_FACES - 1]++
                }
            }

            // If the requisite number of scoring faces were reached...
            if (idealDice[i] >= numKind) {
                rerollable = dice.getUnlockedUnscored(idealDice).toMutableList()
                val maxScore = score(idealDice)
                return Strategy(currentScore, maxScore, name, dice, idealDice, rerollable)
            }
        }

        // If no face values returned, this means it is impossible to get this category.
        return null
    }
}
