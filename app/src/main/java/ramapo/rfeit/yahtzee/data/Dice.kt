package ramapo.rfeit.yahtzee.data

import kotlin.random.Random

class Dice {
    companion object {
        // Constants
        const val NUM_DICE = 5
        const val NUM_DICE_FACES = 6
    }

    // Properties
    var diceList = MutableList(NUM_DICE) { 1 }
    var diceCount = MutableList(NUM_DICE_FACES) { 0 }
    var locked = MutableList(NUM_DICE_FACES) { 0 }

    // Functions

    // Roll a single die and return the value (1-6)
    fun rollOne(): Int = generateDieValue()

    // Function to lock all dice
    fun lockAllDice() {
        locked = diceCount.toMutableList()
    }

    // Function to unlock all dice
    fun resetDice() {
        locked = MutableList(NUM_DICE_FACES) { 0 }
        diceCount = mutableListOf(5, 0, 0, 0, 0, 0)
        diceList = mutableListOf(1, 1, 1, 1, 1)
    }

    // Convert a list of dice to a count of each face
    fun listToCount(diceList: List<Int>): List<Int> {
        val diceCount = MutableList(NUM_DICE_FACES) { 0 }
        diceList.forEach { face -> diceCount[face - 1]++ }
        return diceCount
    }

    // Convert a dice count to a list of dice faces
    fun countToList(diceCount: List<Int>): List<Int> {
        val diceList = mutableListOf<Int>()
        for (i in 0 until NUM_DICE_FACES) {
            repeat(diceCount[i]) { diceList.add(i + 1) }
        }
        return diceList
    }

    // Lock dice that are not part of the kept dice
    fun lockDice(keptDice: List<Int>) {
        val newLocked = MutableList(NUM_DICE_FACES) { 0 }
        for (i in 0 until NUM_DICE_FACES) {
            val numToLock = diceCount[i] - keptDice[i]
            newLocked[i] = numToLock
        }
        locked = newLocked
    }

    // Function to roll all dice that are not locked
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

    // Manual roll input for free dice
    fun manualRoll(input: List<Int>): List<Int> {
        diceCount = locked.toMutableList()
        diceList = countToList(locked).toMutableList()
        diceList.addAll(input)
        input.forEach { face -> diceCount[face - 1]++ }
        return diceList
    }

    // Get unlocked and unscored dice
    fun getUnlockedUnscored(required: List<Int>): List<Int> {
        val unlockedUnscored = MutableList(NUM_DICE_FACES) { 0 }
        for (i in 0 until NUM_DICE_FACES) {
            // Get whichever is greater: required dice of this face or locked dice of this face
            // Subtract the above from current dice to see if there are extraneous dice of this face
            // Take the max between this and zero to avoid negative numbers
            unlockedUnscored[i] = maxOf(diceCount[i] - maxOf(required[i], locked[i]), 0)
        }
        return unlockedUnscored
    }

    // Get the free (unlocked) dice
    fun getFreeDice(): List<Int> {
        return diceList.filter { locked[diceList.indexOf(it)] == 0 }
    }

    // Print the dice
    fun printDice() {
        diceList.forEachIndexed { index, face ->
            if (locked[index] > 0) {
                print("\u001B[31m$face\u001B[0m ") // Red for locked
            } else {
                print("$face ")
            }
        }
        println()
    }

    // Randomly generate a die value (1-6)
    private fun generateDieValue(): Int {
        return Random.nextInt(1, NUM_DICE_FACES + 1)
    }
}
