package ramapo.rfeit.yahtzee.data.categories

import ramapo.rfeit.yahtzee.data.Category
import ramapo.rfeit.yahtzee.data.Dice
import ramapo.rfeit.yahtzee.data.Strategy

/**
 * Represents the Full House scoring category in the Yahtzee game.
 * A Full House consists of three dice showing one number and two dice showing another.
 * Scoring this category awards a fixed score of 25 points if the conditions are met.
 *
 * @param name the name of the category.
 * @param description a description of how to qualify for this category.
 * @param score a [String] of how to calculate points earned for this category.
 */
class FullHouseCategory(
    name: String,
    description: String,
    score: String,
) : Category(name, description, score) {

    companion object {
        /**
         * The fixed (constant) score awarded for a Full House.
         */
        const val FULL_HOUSE_SCORE = 25
    }

    /**
     * Calculates the score for the Full House category based on the given dice.
     * A Full House requires three dice of one face value and two of another.
     *
     * @param dice the set of [Dice] to calculate the score for.
     * @return the score for a Full House (25 points) if the conditions are met, or 0 otherwise.
     *
     * @reference Used ChatGPT to convert C++ version functions and classes before clean-up.
     */
    override fun score(dice: Dice): Int {
        // Conditions to score for Full House: 3 of one face and 2 of another.
        var condition1Met = false
        var condition2Met = false

        // Loop through counts for each face
        val diceValues = dice.diceCount
        for (i in diceValues.indices) {
            // 3 faces are the same
            if (diceValues[i] == 3) condition1Met = true
            // 2 faces are the same
            else if (diceValues[i] == 2) condition2Met = true
        }

        // Return the score (25 for Full House) only if both conditions were met, otherwise 0
        return if (condition1Met && condition2Met) FULL_HOUSE_SCORE else 0
    }

    /**
     * Determines the reroll strategy for attempting to score a Full House.
     * This considers the current dice and locked state to suggest an ideal reroll strategy.
     * Returns `null` if scoring is impossible.
     *
     * @param dice the set of [Dice] to analyze for reroll strategy.
     * @return a [Strategy] object with the ideal reroll strategy, or `null` if impossible to score.
     *
     * @algorithm
     * 1) Return an null if the category is full
     * 2) Return a stand strategy if this dice set scores already
     * 3) Determine the mode and secondary mode from locked dice
     *      4) If more than 3 faces are locked, or more than 3 dice of any face are locked,
     *         return empty strategy (impossible to score)
     * 5) Determine the mode and secondary mode from all dice
     * 6) Determine which face to aim for 3 and which to aim for 2
     * 7) Create a strategy based on these modes
     *
     * @reference Used ChatGPT to convert C++ version functions and classes before clean-up.
     */
    override fun getRerollStrategy(dice: Dice): Strategy? {
        // Return null if this category has already been filled.
        if (full) return null

        // If this scores, stand because you always get maximum points in this category.
        val currentScore = score(dice)
        if (currentScore > 0) return Strategy(currentScore, currentScore, name)

        // Check locked dice to see if this strategy is possible.
        val lockedDice = dice.locked
        var lockedFaces = 0
        var lockedMode1 = -1
        var lockedMode2 = -1
        var lockedMaxCount = 0

        // Loop through each dice face and check for locked dice.
        for (i in 0 until Dice.NUM_DICE_FACES) {
            if (lockedDice[i] > 0) {
                if (lockedFaces > 0 && lockedDice[i] > lockedMaxCount) {
                    lockedMaxCount = lockedDice[i]
                    lockedMode2 = lockedMode1
                    lockedMode1 = i
                } else {
                    lockedMode2 = i
                }
                lockedFaces++
            }
            // Impossible to score if a face has more than 3 locked dice, so return null.
            if (lockedDice[i] > 3) return null
        }
        // Impossible to score if more than two face values have locked dice, so return null.
        if (lockedFaces > 2) return null

        // Check the mode among all dice.
        var mode1 = -1
        var mode2 = -1
        var maxCount1 = 0
        var maxCount2 = 0

        val diceValues = dice.diceCount
        for (i in 0 until Dice.NUM_DICE_FACES) {
            if (diceValues[i] > maxCount1) {
                // Shift current mode to secondary.
                mode2 = mode1
                maxCount2 = maxCount1
                // Set new mode.
                mode1 = i
                maxCount1 = diceValues[i]
            } else if (diceValues[i] > maxCount2) {
                // Replace secondary mode.
                mode2 = i
                maxCount2 = diceValues[i]
            }
        }

        // Check which mode values to use.
        if (maxCount1 < 2) mode1 = -1
        if (maxCount2 < 2) mode2 = -1

        if (lockedMode1 >= 0) {
            if (lockedMode2 >= 0) {
                mode1 = lockedMode1
                mode2 = lockedMode2
            } else if (mode1 != lockedMode1) {
                if (maxCount1 > lockedMaxCount) {
                    mode2 = lockedMode1
                } else {
                    mode2 = mode1
                    mode1 = lockedMode1
                }
            }
        }

        // Ideal dice will always be 3 of mode1 and 2 of mode2.
        val idealDice = MutableList(Dice.NUM_DICE_FACES) { 0 }
        if (mode1 >= 0) idealDice[mode1] = 3
        if (mode2 >= 0) idealDice[mode2] = 2

        // Return a strategy that rerolls all non-scoring dice.
        val toReroll = dice.getUnlockedUnscored(idealDice)
        return Strategy(currentScore, FULL_HOUSE_SCORE, name, dice, idealDice, toReroll)
    }
}
