package ramapo.rfeit.yahtzee.data

import kotlin.random.Random

/**
 * Represents a set of dice used in the Yahtzee game.
 * Provides functionality for rolling, locking, and managing dice.
 */
class Dice {
    companion object {
        /**
         * The number of dice in the game.
         */
        const val NUM_DICE = 5

        /**
         * The number of faces on each die.
         */
        const val NUM_DICE_FACES = 6
    }

    /**
     * The current values each die within the diceset.
     */
    internal var diceList = MutableList(NUM_DICE) { 1 }

    /**
     * A count of each face value currently present in the dice.
     */
    internal var diceCount = MutableList(NUM_DICE_FACES) { 0 }

    /**
     * A count of locked dice for each face value.
     */
    internal var locked = MutableList(NUM_DICE_FACES) { 0 }

    /**
     * Rolls a single die and returns its value.
     *
     * @return a random integer between 1 and 6.
     *
     * @reference Used ChatGPT to convert C++ version functions and classes before clean-up.
     *
     */
    fun rollOne(): Int = generateDieValue()

    /**
     * Locks all dice by setting the locked count equal to the current dice count.
     *
     * @reference Used ChatGPT to convert C++ version functions and classes before clean-up.
     */
    fun lockAllDice() {
        locked = diceCount.toMutableList()
    }

    /**
     * Resets all dice to their initial state (unlocked with a value of 1).
     *
     * @reference Used ChatGPT to convert C++ version functions and classes before clean-up.
     */
    fun resetDice() {
        locked = mutableListOf(0, 0, 0, 0, 0, 0)
        diceCount = mutableListOf(5, 0, 0, 0, 0, 0)
        diceList = mutableListOf(1, 1, 1, 1, 1)
    }

    /**
     * Converts a list of dice values to a count of each face value.
     *
     * @param diceList the list of dice values to convert.
     * @return a list of counts for each face value.
     *
     * @reference Used ChatGPT to convert C++ version functions and classes before clean-up.
     */
    fun listToCount(diceList: List<Int>): List<Int> {
        val diceCount = MutableList(NUM_DICE_FACES) { 0 }
        diceList.forEach { face -> diceCount[face - 1]++ }
        return diceCount
    }

    /**
     * Locks dice that are not part of the provided set of kept dice.
     *
     * @param keptDice a list of dice values to keep unlocked.
     *
     * @reference Used ChatGPT to convert C++ version functions and classes before clean-up.
     */
    fun lockDice(keptDice: List<Int>) {
        val newLocked = MutableList(NUM_DICE_FACES) { 0 }
        for (i in 0 until NUM_DICE_FACES) {
            val numToLock = diceCount[i] - keptDice[i]
            newLocked[i] = numToLock
        }
        locked = newLocked
    }

    /**
     * Rolls all dice that are not locked and updates the dice list and counts.
     *
     * @return the updated list of dice values.
     *
     * @reference Used ChatGPT to convert C++ version functions and classes before clean-up.
     */
    fun rollAll(): List<Int> {
        diceCount = mutableListOf(0, 0, 0, 0, 0, 0)
        val lockedRemaining = locked.toMutableList()
        for (i in 0 until NUM_DICE) {
            if (lockedRemaining[diceList[i] - 1] > 0) {
                lockedRemaining[diceList[i] - 1]--
                diceCount[diceList[i] - 1]++
                continue
            }
            diceList[i] = generateDieValue()
            diceCount[diceList[i] - 1]++
        }
        return diceList
    }

    /**
     * Manually sets the values of unlocked dice based on the provided input.
     *
     * @param input a list of dice values to assign to unlocked dice.
     * @return the updated list of dice values.
     *
     * @reference Used ChatGPT to convert C++ version functions and classes before clean-up.
     */
    fun manualRoll(input: List<Int>): List<Int> {
        diceCount = locked.toMutableList()
        diceList = countToList(locked).toMutableList()
        diceList.addAll(input)
        input.forEach { face -> diceCount[face - 1]++ }
        return diceList
    }

    /**
     * Calculates the number of unlocked and unscored dice for each face value.
     *
     * @param required a list representing the minimum required dice for each face value.
     * @return a list of counts of unlocked and unscored dice for each face value.
     *
     * @reference Used ChatGPT to convert C++ version functions and classes before clean-up.
     */
    fun getUnlockedUnscored(required: List<Int>): List<Int> {
        val unlockedUnscored = MutableList(NUM_DICE_FACES) { 0 }
        for (i in 0 until NUM_DICE_FACES) {
            unlockedUnscored[i] = maxOf(diceCount[i] - maxOf(required[i], locked[i]), 0)
        }
        return unlockedUnscored
    }

    /**
     * Returns a string representation of the current dice values.
     *
     * @return a string showing the list of dice values.
     *
     * @reference None.
     */
    override fun toString(): String {
        return diceList.toString()
    }

    /**
     * Generates a random die value (1-6).
     *
     * @return a random integer representing a die face value.
     *
     * @reference Used ChatGPT to convert C++ version functions and classes before clean-up.
     */
    private fun generateDieValue(): Int {
        return Random.nextInt(1, NUM_DICE_FACES + 1)
    }

    /**
     * Converts a count of dice faces to a list of dice values.
     *
     * @param diceCount the count of each face value.
     * @return a list of dice values representing the count.
     *
     * @reference Used ChatGPT to convert C++ version functions and classes before clean-up.
     */
    private fun countToList(diceCount: List<Int>): List<Int> {
        val diceList = mutableListOf<Int>()
        for (i in 0 until NUM_DICE_FACES) {
            repeat(diceCount[i]) { diceList.add(i + 1) }
        }
        return diceList
    }
}