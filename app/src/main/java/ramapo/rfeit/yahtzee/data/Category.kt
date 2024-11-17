package ramapo.rfeit.yahtzee.data

// Category class to handle different scorecard categories
abstract class Category(
    val name: String,
    val description: String,
    val score: String
) {

    // Properties
    var winner: String = ""
    var points: Int = 0
    var round: Int = 0
    var full: Boolean = false

    // Selectors
    fun isFull(): Boolean = full

    // Functions (abstract to be implemented by subclasses)
    abstract fun score(dice: Dice): Int
    abstract fun getRerollStrategy(dice: Dice): Strategy
}
// MultiplesCategory.kt

class MultiplesCategory(
    name: String,
    description: String,
    score: String,
    private val multipleIndex: Int,
) : Category(name, description, score) {
    override fun score(aDice: Dice): Int {
        // The score = how many times this face appears * the value of the face
        return aDice.diceCount[multipleIndex] * (multipleIndex + 1)
    }

    override fun getRerollStrategy(aDice: Dice): Strategy {
        // Return a default, 0 score strategy if this category has already been filled.
        if (full) return Strategy()

        val currentScore = score(aDice)
        val currentDiceCounts = aDice.diceCount

        // Try to determine what the best possible roll would look like.
        val perfectScore = MutableList<Int>(Dice.NUM_DICE_FACES) { 0 }

        // Find all dice that are not locked and are not contributing to the current score.
        val unlockedUnscored = aDice.getUnlockedUnscored(perfectScore)

        // Determine how many dice can be rerolled.
        val rerolledDice = unlockedUnscored.sum()

        // Add all possible rerolls to the scoring face.
        perfectScore[multipleIndex] = currentDiceCounts[multipleIndex] + rerolledDice

        // Calculate maximum score from maximum possible dice of desired face
        val maxScore = (multipleIndex + 1) * perfectScore[multipleIndex]

        // Stand if there are no possible dice to reroll, or create a reroll strategy.
        return if (rerolledDice == 0) {
            Strategy(currentScore, maxScore, this::class.simpleName!!)
        } else {
            Strategy(currentScore, maxScore, this::class.simpleName!!, aDice, perfectScore, unlockedUnscored)
        }
    }
}

// KindCategory.kt

class KindCategory(
    name: String,
    description: String,
    score: String,
    private val numKind: Int,
) : Category(name, description, score) {

    override fun score(aDice: Dice): Int {
        var conditionMet = false
        var score = 0

        // Loop through counts for each face
        val diceValues = aDice.diceCount
        for (i in diceValues.indices) {
            // Calculate score by adding face value * count
            score += diceValues[i] * (i + 1)
            // If there are enough of the same kind, the condition is met
            if (diceValues[i] >= numKind) conditionMet = true
        }
        // Either 0, or the sum of all dice faces
        return score * if (conditionMet) 1 else 0
    }

    fun score(aDiceValues: MutableList<Int>): Int {
        var conditionMet = false
        var score = 0

        // Loop through counts for each face
        for (i in aDiceValues.indices) {
            // Calculate score by adding face value * count
            score += aDiceValues[i] * (i + 1)
            // If there are enough of the same kind, the condition is met
            if (aDiceValues[i] >= numKind) conditionMet = true
        }
        // Either 0, or the sum of all dice faces
        return score * if (conditionMet) 1 else 0
    }

    override fun getRerollStrategy(aDice: Dice): Strategy {
        // Return a default, 0 score strategy if this category has already been filled.
        if (full) return Strategy()

        val currentScore = score(aDice)
        val diceValues = aDice.diceCount

        // Try to determine what the best possible roll would look like.
        var idealDice: MutableList<Int> = MutableList(Dice.NUM_DICE_FACES) { 0 }
        var rerollable: MutableList<Int>

        // Loop through each dice face counting down. (The best score comes from higher faces.)
        for (i in Dice.NUM_DICE_FACES - 1 downTo 0) {
            val minimumScore = MutableList<Int>(Dice.NUM_DICE_FACES) { 0 }
            minimumScore[i] = numKind

            // Set rerollable as any non-essential dice for scoring.
            rerollable = aDice.getUnlockedUnscored(minimumScore).toMutableList()

            // Ideal dice set must at minimum include locked dice.
            idealDice = aDice.locked.toMutableList()

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
                rerollable = aDice.getUnlockedUnscored(idealDice).toMutableList()
                val maxScore = score(idealDice)
                return Strategy(currentScore, maxScore, this::class.simpleName!!, aDice, idealDice, rerollable)
            }
        }

        // If no face values returned, this means it is impossible to get this category.
        return Strategy()
    }
}

// FullHouseCategory.kt

class FullHouseCategory(
    name: String,
    description: String,
    score: String,
) : Category(name, description, score) {

    override fun score(aDice: Dice): Int {
        var condition1Met = false
        var condition2Met = false

        // Loop through counts for each face
        val diceValues = aDice.diceCount
        for (i in diceValues.indices) {
            // 3 faces are the same
            if (diceValues[i] == 3) condition1Met = true
            // 2 faces are the same
            else if (diceValues[i] == 2) condition2Met = true
        }

        // Return the score only if both conditions were met, otherwise 0
        return 25 * if (condition1Met && condition2Met) 1 else 0
    }

    override fun getRerollStrategy(aDice: Dice): Strategy {
        // Return a default, 0 score strategy if this category has already been filled.
        if (full) return Strategy()

        // If this scores, stand because you always get maximum points in this category.
        val currentScore = score(aDice)
        if (currentScore != 0) return Strategy(currentScore, currentScore, this::class.simpleName!!)

        // Check locked dice to see if this strategy is possible.
        val lockedDice = aDice.locked
        var lockedFaces = 0
        var lockedMode1 = -1
        var lockedMode2 = -1
        var lockedMaxCount = 0

        // Loop through each dice face and check for locked dice.
        for (i in 0 until Dice.NUM_DICE_FACES) {
            if (lockedDice[i] > 0) {
                // Check if this is the new mode amongst locked dice.
                if (lockedFaces > 0 && lockedDice[i] > lockedMaxCount) {
                    // handle updating modes here
                }
            }
        }

        return Strategy()
    }
}

class StraightCategory(
    name: String,
    description: String,
    score: String,
    private val streakNum: Int,
    private val scoreValue: Int
) : Category(name, description, score) {

    // Score function for Straight category
    override fun score(a_dice: Dice): Int {
        var streak = 0

        // Loop through dice face counts
        val diceValues = a_dice.diceCount
        for (i in 0 until 6) {
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

    // Get Reroll Strategy function for Straight category
    override fun getRerollStrategy(a_dice: Dice): Strategy {
        // Return a default, 0 score strategy if this category has already been filled.
        if (full) return Strategy()

        // If this scores, stand because you always get maximum points in this category.
        val currentScore = score(a_dice)
        if (currentScore != 0) return Strategy(currentScore, currentScore, name)

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

        val diceValues = a_dice.diceCount
        val lockedDice = a_dice.locked

        // Go through each possible configuration to check which is possible and/or the best.
        for (config in dicesetsToCheck) {
            val straightAttempt = mutableListOf<Int>().apply { addAll(lockedDice) }
            val checkRerolls = a_dice.getUnlockedUnscored(config).toMutableList()
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
        if (minRerollNum == 0) return Strategy()

        // Otherwise, return the best configuration found.
        return Strategy(currentScore, scoreValue, name, a_dice, idealDice, toReroll)
    }
}


/*
    YAHTZEE CATEGORY
*/

class YahtzeeCategory(
    name: String,
    description: String,
    score: String
) : Category(name, description, score) {

    // Score function for Yahtzee category
    override fun score(a_dice: Dice): Int {
        val diceValues = a_dice.diceCount
        for (i in diceValues.indices) {
            if (diceValues[i] == Dice.NUM_DICE) return YAHTZEE_SCORE
        }

        return 0
    }

    // Get Reroll Strategy function for Yahtzee category
    override fun getRerollStrategy(a_dice: Dice): Strategy {
        // Return a default, 0 score strategy if this category has already been filled.
        if (full) return Strategy()

        // If this scores, stand because you always get maximum points in this category
        val currentScore = score(a_dice)
        if (currentScore != 0) return Strategy(currentScore, currentScore, name)

        // Check locked dice to see if this strategy is possible.
        val lockedDice = a_dice.locked
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
        if (lockedFaces > 1) return Strategy()

        // If there are locked dice, use those to score.
        val idealDice = MutableList(6) { 0 }
        if (lockedIndex >= 0) idealDice[lockedIndex] = Dice.NUM_DICE
        // Otherwise, use the mode of all other dice.
        else {
            var mode = -1
            var maxCount = 0
            val diceValues = a_dice.diceCount
            for (i in 0 until Dice.NUM_DICE_FACES) {
                if (diceValues[i] >= maxCount) {
                    mode = i
                    maxCount = diceValues[i]
                }
            }
            idealDice[mode] = Dice.NUM_DICE
        }

        // Return reroll strategy based on determined dice.
        val toReroll = a_dice.getUnlockedUnscored(idealDice)
        return Strategy(currentScore, YAHTZEE_SCORE, name, a_dice, idealDice, toReroll)
    }

    companion object {
        const val YAHTZEE_SCORE = 50
    }
}
