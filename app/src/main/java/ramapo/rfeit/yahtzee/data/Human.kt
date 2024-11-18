package ramapo.rfeit.yahtzee.data

import java.util.*

// Class to handle functionality for a human player.
class Human : Player("You", "Human") {

    // Functions

    /**
     * Rolls one die for this player
     * @param dice The dice set to roll
     * @return The result of the roll
     */
    override fun rollOne(dice: Dice): Int {
        return dice.rollOne()
    }

    /**
     * Lists available categories based on the current dice set.
     * @param strat The strategy engine for generating possibilities
     * @param dice The dice to determine available categories
     * @return A list of available categories by index [1,12]
     */
    override fun listAvailableCategories(strat: StrategyEngine, dice: Dice): List<Int> {
        //val availableCategories = strat.getPossibleCategories(dice).map { it + 1 }
        //val helpString = "The available categories are: " + availableCategories.joinToString(" ")
        //println("\nPlease list all scorecard categories available, given the dice set so far.")
        //Input.validateExactIntList(availableCategories, "categories", helpString)
        //return availableCategories
        return listOf()
    }

    /**
     * Makes player choose which categories to pursue
     * @param strat The strategy engine for generating possibilities
     * @param availableCategories The available categories by index [1,12]
     * @param dice The dice object for determining possibilities
     */
    override fun pursueCategories(strat: StrategyEngine, availableCategories: List<Int>, dice: Dice) {
        println("Please input one or two categories to pursue.")
        //mHelpStrat = strat.strategize(dice)
        //Input.validateIntList(availableCategories, "categories", 0, mHelpStrat.getString(true))
    }

    /**
     * Handles rerolls by deciding whether to stand or reroll, and which dice to reroll
     * @param dice The dice to be rerolled
     * @return True if the player stands, false if the player rerolls
     */
    override fun handleRerolls(dice: Dice): Boolean {
        println("Please choose whether to stand or reroll.")
        // if (Input.validateStandReroll(mHelpStrat.getString(true))) return true

        println("Which dice would you like to reroll? Input the face values.")
        // do {
            // val playerDiceList = Input.validateIntList(listOf(1, 2, 3, 4, 5, 6), "dice faces")
            // val playerRerollCount = Dice.listToCount(playerDiceList)
        // } while (!dice.lockDice(playerRerollCount))
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
        if (availableCategories.isEmpty()) {
            println("There is no way to score with the current dice set and open categories. Skipping turn.\n")
            return
        }

        //mHelpStrat = strat.strategize(dice)
        println("Please identify the category you would like to claim.")
        // val chosenCategory = Input.validateInt(availableCategories, "categories", mHelpStrat.getString(true)) - 1
        println("Please enter the score earned for this category.")
        // val claimedScore = Input.validateInt(listOf(scorecard.getCategory(chosenCategory).score(dice)), "score")

        println("Please input the current round.")
        // Input.validateInt(listOf(round), "round")

        println("Please press enter to fill the scorecard.")
        readLine()
        // addScore(claimedScore)
        // scorecard.fillCategory(chosenCategory, claimedScore, round, "Human")
        // scorecard.print()
    }

    private lateinit var mHelpStrat: Strategy
}