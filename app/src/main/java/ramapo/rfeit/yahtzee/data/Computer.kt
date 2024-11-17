package ramapo.rfeit.yahtzee.data

import java.util.*

// Class to handle functionality for a computer player.
class Computer : Player("The computer", "Computer") {

    /**
     * Rolls one die for this player
     * @param dice The dice set to roll
     * @return The result of the roll
     */
    override fun rollOne(dice: Dice): Int {
        println("Computer's roll...")
        println("Would you like to manually input this dice roll? (y/n)")
        return dice.rollOne()
    }

    /**
     * Lists available categories based on the current dice set.
     * @param strat The strategy engine for generating possibilities
     * @param dice The dice to determine available categories
     * @return A list of available categories by index [1,12]
     */
    override fun listAvailableCategories(strat: StrategyEngine, dice: Dice): List<Int> {
        println("\nListing all available categories, given the dice set so far...")

        val availableCategories = strat.getPossibleCategories(dice).map { it + 1 }
        availableCategories.forEach { print("$it ") }
        println()

        return availableCategories
    }

    /**
     * Makes player choose which categories to pursue
     * @param strat The strategy engine for generating possibilities
     * @param availableCategories The available categories by index [1,12]
     * @param dice The dice object for determining possibilities
     */
    override fun pursueCategories(strat: StrategyEngine, availableCategories: List<Int>, dice: Dice) {
        mStrat = strat.strategize(dice)
        println()
        mStrat.print(false)
    }

    /**
     * Handles rerolls by deciding whether to stand or reroll, and which dice to reroll
     * @param dice The dice to be rerolled
     * @return True if the player stands, false if the player rerolls
     */
    override fun handleRerolls(dice: Dice): Boolean {
        if (mStrat.planToStand()) return true

        val diceToReroll = mStrat.getRerollDice()
        dice.lockDice(diceToReroll)
        return false
    }

    /**
     * Makes player choose a category to fill based on the current round and strategy.
     * @param scorecard The scorecard to update
     * @param round The current round number
     * @param strat The strategy engine for determining strategy
     * @param availableCategories The list of available categories
     * @param dice The dice object for determining scoring possibilities
     */
    override fun chooseCategory(
        scorecard: Scorecard,
        round: Int,
        strat: StrategyEngine,
        availableCategories: List<Int>,
        dice: Dice
    ) {
        mStrat = strat.strategize(dice)
        addScore(mStrat.currentScore)
        mStrat.enact(scorecard, round)
    }

    private lateinit var mStrat: Strategy
}
