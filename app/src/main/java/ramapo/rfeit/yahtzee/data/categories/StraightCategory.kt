package ramapo.rfeit.yahtzee.data.categories

import ramapo.rfeit.yahtzee.data.Category
import ramapo.rfeit.yahtzee.data.Dice
import ramapo.rfeit.yahtzee.data.Strategy

/**
 * Represents the Straight scoring category in the Yahtzee game.
 * Straights consists of several dice whose face values are in a row (e.g. 2 1 4 3 could be Four Straight).
 * Scoring this category awards a set score of either 30 or 40 points.
 *
 * @param name the name of the category.
 * @param description a description of how to qualify for this category.
 * @param score a [String] of how to calculate points earned for this category.
 * @param streakNum the number of dice that need to be in a row to score.
 * @param scoreValue the score earned when qualifying for this category.
 */
class StraightCategory(
    name: String,
    description: String,
    score: String,
    private val streakNum: Int,
    private val scoreValue: Int
) : Category(name, description, score) {

    /**
     * Calculates the score for Multiples category based on the given dice.
     * Multiples requires at least one die matching the required face (e.g. a 1 for Aces).
     *
     * @param dice the set of [Dice] to calculate the score for.
     * @return the score received in this category for the given diceset.
     *
     * @reference Used ChatGPT to convert C++ version functions and classes before clean-up.
     */
    override fun score(dice: Dice): Int {
        var streak = 0

        // Loop through dice face counts
        val diceValues = dice.diceCount
        for (i in 0 until Dice.NUM_DICE_FACES) {
            // Add to the streak for every successive face that exists
            if (diceValues[i] >= 1) streak++
            // Otherwise, reset the streak
            else streak = 0

            // Once the streak hits the required value, return immediately
            if (streak == streakNum) return scoreValue
        }

        // If the streak requirement was not met, do not score.
        return 0
    }

    /**
     * Determines the reroll strategy for attempting to score a Straight category.
     * This considers the current dice and locked state to suggest an ideal reroll strategy.
     * Returns `null` if scoring is impossible.
     *
     * @param dice the set of [Dice] to analyze for reroll strategy.
     * @return a [Strategy] object with the ideal reroll strategy, or `null` if impossible to score.
     *
     * @algorithm
     * 1) Return an null if the category is full
     * 2) Return a stand strategy if this dice set scores already
     * 3) Create a vector of all possible dice configurations for this category
     * 4) Loop through the vector
     *      5) Check which dice need to be rerolled to achieve each configuration
     *          6) If more rerolls are required than are available, it is impossible
     *          7) If it is possible and requires less rerolls than any previous config, use this
     * 8) If no configurations were possible, return null
     * 9) Otherwise, return the best strategy found (fewest rerolls needed)
     *
     * @reference Used ChatGPT to convert C++ version functions and classes before clean-up.
     */
    override fun getRerollStrategy(dice: Dice): Strategy? {
        // Return a default, 0 score strategy if this category has already been filled.
        if (full) return null

        // If this scores, stand because you always get maximum points in this category.
        val currentScore = score(dice)
        if (currentScore > 0) return Strategy(currentScore, currentScore, name)

        // If this doesn't score, decide which dice to reroll.
        val dicesetsToCheck = mutableListOf<List<Int>>()
        if (streakNum == 4) {
            dicesetsToCheck.add(listOf(0, 1, 1, 1, 1, 0))
            dicesetsToCheck.add(listOf(1, 1, 1, 1, 0, 0))
            dicesetsToCheck.add(listOf(0, 0, 1, 1, 1, 1))
        } else if (streakNum == 5) {
            dicesetsToCheck.add(listOf(0, 1, 1, 1, 1, 1))
            dicesetsToCheck.add(listOf(1, 1, 1, 1, 1, 0))
        }

        // Check Straight restrictions
        var minRerollNum = 0
        var toReroll = mutableListOf<Int>()
        var idealDice = mutableListOf<Int>()

        val diceValues = dice.diceCount
        val lockedDice = dice.locked

        // Go through each possible configuration to check which is possible and/or the best.
        for (config in dicesetsToCheck) {
            val straightAttempt = lockedDice.toMutableList()
            val checkRerolls = dice.getUnlockedUnscored(config).toMutableList()
            val rerollsAvailable = checkRerolls.sum()

            var rerollsNeeded = 0
            for (i in 0 until Dice.NUM_DICE_FACES) {
                // If locked dice does not already include this face, but this config does
                if (diceValues[i] < config[i]) {
                    rerollsNeeded++
                    straightAttempt[i]++
                    // If we ran out of rerolls, this is not viable.
                    if (rerollsNeeded > rerollsAvailable) break
                }
                // There is at least one die of this face (and it is required)
                else if (config[i] > 0) {
                    // Keep either 1, or however many are locked.
                    straightAttempt[i] = maxOf(1, straightAttempt[i])
                }
            }

            // Ran out of rerolls, so this configuration is impossible.
            if (rerollsNeeded > rerollsAvailable) continue

            // If an option has not been set, or this configuration requires fewer rerolls than before
            if (minRerollNum == 0 || minRerollNum > rerollsNeeded) {
                // Set the new minimum rerolls, target dice config, and dice to reroll
                minRerollNum = rerollsNeeded
                idealDice = straightAttempt
                toReroll = checkRerolls
            }
        }

        // If there were no rerolls, this means that every config failed and this category is impossible given the current dice set.
        if (minRerollNum == 0) return null

        // Otherwise, return the best configuration found.
        return Strategy(currentScore, scoreValue, name, dice, idealDice, toReroll)
    }
}
