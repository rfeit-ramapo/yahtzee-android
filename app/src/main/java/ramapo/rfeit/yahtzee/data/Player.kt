package ramapo.rfeit.yahtzee.data

import java.util.*

abstract class Player(
    val logName: String,
    val internalName: String,
    var score: Int = 0
) {

    // Mutators
    fun addScore(add: Int) {
        score += add
    }

    // Functions
    /* *********************************************************************
    Function Name: RollAll
    Purpose: Rolls all unlocked dice for the player
    Parameters:
                dice, a reference to the dice set to be rerolled
    Return Value: a list of the newly generated dice face values
    Algorithm:
        1) Checks if the player wants to manually input the roll
            2) If so, gets new values for unlocked dice
        3) Otherwise, uses automatic reroll function
    Reference: none
    ********************************************************************* */
    fun rollAll(dice: Dice): List<Int> {

        // Automatically reroll dice that are unlocked
        return dice.rollAll()
    }

    // Virtual Functions (abstract methods)

    // Roll one die (abstract, to be implemented by subclasses)
    abstract fun rollOne(dice: Dice): Int

    // List available categories (abstract, to be implemented by subclasses)
    abstract fun listAvailableCategories(strat: StrategyEngine, dice: Dice): List<Int>

    // Pursue categories (abstract, to be implemented by subclasses)
    abstract fun pursueCategories(strat: StrategyEngine, availableCategories: List<Int>, dice: Dice)

    // Handle rerolls (abstract, to be implemented by subclasses)
    abstract fun handleRerolls(dice: Dice): Boolean

    // Choose category (abstract, to be implemented by subclasses)
    abstract fun chooseCategory(scorecard: Scorecard, round: Int, strat: StrategyEngine, availableCategories: List<Int>, dice: Dice)
}