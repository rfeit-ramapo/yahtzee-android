package ramapo.rfeit.yahtzee.data.categories

import ramapo.rfeit.yahtzee.data.Category
import ramapo.rfeit.yahtzee.data.Dice
import ramapo.rfeit.yahtzee.data.Strategy

/**
 * Represents the Yahtzee scoring category in the Yahtzee game.
 * Yahtzee consists of all five dice showing the same face.
 * Scoring this category awards a fixed score of 50 points if the condition is met.
 *
 * @param name the name of the category.
 * @param description a description of how to qualify for this category.
 * @param score a [String] of how to calculate points earned for this category.
 */
class YahtzeeCategory(
    name: String,
    description: String,
    score: String
) : Category(name, description, score) {

    companion object {
        /**
         * The fixed (constant) score awarded for Yahtzee.
         */
        const val YAHTZEE_SCORE = 50
    }

    /**
     * Calculates the score for the Yahtzee category based on the given dice.
     * A Yahtzee requires five dice of the same face to score.
     *
     * @param dice the set of dice to calculate the score for.
     * @return the score for Yahtzee (50 points) if the conditions are met, or 0 otherwise.
     *
     * @reference Used ChatGPT to convert C++ version functions and classes before clean-up.
     */
    override fun score(dice: Dice): Int {
        val diceValues = dice.diceCount
        for (i in diceValues.indices) {
            // Always return max score if condition is met (50)
            if (diceValues[i] == Dice.NUM_DICE) return 50
        }
        return 0
    }

    /**
     * Determines the reroll strategy for attempting to score Yahtzee.
     * This considers the current dice and locked state to suggest an ideal reroll strategy.
     * Returns `null` if scoring is impossible.
     *
     * @param dice the set of dice to analyze for reroll strategy.
     * @return a [Strategy] object with the ideal reroll strategy, or `null` if impossible to score.
     *
     * @algorithm
     * 1) Return an null if the category is full
     * 2) Return a stand strategy if this dice set scores already
     * 3) Check for locked dice faces to see if this category is possible
     *      4) If there is more than one locked face, return null (impossible)
     * 5) Use either the locked dice face or mode of all dice to pursue
     * 6) Return a reroll strategy based on selected face
     *
     * @reference Used ChatGPT to convert C++ version functions and classes before clean-up.
     */
    override fun getRerollStrategy(dice: Dice): Strategy? {
        // Return a default, 0 score strategy if this category has already been filled.
        if (full) return null

        // If this scores, stand because you always get maximum points in this category
        val currentScore = score(dice)
        if (currentScore > 0) return Strategy(currentScore, currentScore, name)

        // Check locked dice to see if this strategy is possible.
        val lockedDice = dice.locked
        var lockedFaces = 0
        var lockedIndex = -1

        for (i in 0 until Dice.NUM_DICE_FACES) {
            // If this dice face is locked, update index and face count.
            if (lockedDice[i] > 0) {
                lockedFaces++
                lockedIndex = i
            }
        }
        // Impossible if more than one face value has locked dice; return empty Strategy.
        if (lockedFaces > 1) return null

        // If there are locked dice, use those to score.
        val idealDice = MutableList(6) { 0 }
        if (lockedIndex >= 0) idealDice[lockedIndex] = Dice.NUM_DICE
        // Otherwise, use the mode of all other dice.
        else {
            var mode = -1
            var maxCount = 0
            val diceValues = dice.diceCount
            for (i in 0 until Dice.NUM_DICE_FACES) {
                if (diceValues[i] >= maxCount) {
                    mode = i
                    maxCount = diceValues[i]
                }
            }
            idealDice[mode] = Dice.NUM_DICE
        }

        // Return reroll strategy based on determined dice.
        val toReroll = dice.getUnlockedUnscored(idealDice)
        return Strategy(currentScore, YAHTZEE_SCORE, name, dice, idealDice, toReroll)
    }
}
